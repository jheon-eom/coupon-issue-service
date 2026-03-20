package com.example.couponservice.v4.service

import com.example.couponservice.domain.IssueCoupon
import com.example.couponservice.domain.IssueCouponFailure
import com.example.couponservice.domain.IssueCouponFailureRepository
import com.example.couponservice.domain.IssueCouponRepository
import com.example.couponservice.v4.dto.CouponIssueEventV4
import com.example.couponservice.v4.dto.CouponIssueFailedEventV4
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.errors.WakeupException
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import jakarta.annotation.PreDestroy
import java.time.Duration
import java.time.Instant
import java.util.Properties

@Component
class CouponIssueKafkaWorkerV4(
    private val issueCouponRepository: IssueCouponRepository,
    private val issueCouponFailureRepository: IssueCouponFailureRepository,
    private val couponIssueKafkaPublisherV4: CouponIssueKafkaPublisherV4,
    @Value("\${spring.kafka.bootstrap-servers:localhost:9092}")
    private val bootstrapServers: String,
    @Value("\${coupon.v4.kafka.topic:coupon.issue.v4}")
    private val issueTopic: String,
    @Value("\${coupon.v4.kafka.group-id:coupon-issue-v4-worker}")
    private val groupId: String,
    @Value("\${coupon.v4.worker.poll-timeout-ms:1000}")
    private val pollTimeoutMs: Long,
    @Value("\${coupon.v4.worker.batch-size:100}")
    private val batchSize: Int,
    @Value("\${coupon.v4.worker.db-max-retries:3}")
    private val dbMaxRetries: Int,
    @Value("\${coupon.v4.worker.retry-backoff-ms:100}")
    private val retryBackoffMs: Long
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val consumer: KafkaConsumer<String, String> by lazy {
        val props = Properties().apply {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
            put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
            put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false")
            put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, batchSize.toString())
        }
        KafkaConsumer<String, String>(props).also {
            it.subscribe(listOf(issueTopic))
        }
    }

    @Scheduled(fixedDelayString = "\${coupon.v4.worker.fixed-delay-ms:200}")
    fun consumeChunk() {
        val records = try {
            consumer.poll(Duration.ofMillis(pollTimeoutMs))
        } catch (ex: WakeupException) {
            return
        } catch (ex: Exception) {
            log.error("Failed to poll Kafka records.", ex)
            return
        }

        for (record in records) {
            val processed = processRecord(record.value())
            if (!processed) {
                // Keep offset uncommitted to retry this record in next poll.
                break
            }

            val partition = TopicPartition(record.topic(), record.partition())
            val offset = OffsetAndMetadata(record.offset() + 1)
            consumer.commitSync(mapOf(partition to offset))
        }
    }

    private fun processRecord(payload: String): Boolean {
        val event = try {
            deserializeIssue(payload)
        } catch (ex: Exception) {
            log.error("Invalid coupon issue event payload. payload={}", payload, ex)
            return true
        }

        var lastError: Exception? = null
        val totalAttempts = dbMaxRetries + 1

        for (attempt in 1..totalAttempts) {
            try {
                issueCouponRepository.save(
                    IssueCoupon(
                        userId = event.userId,
                        couponId = event.couponId
                    )
                )
                return true
            } catch (ex: DataIntegrityViolationException) {
                if (isDuplicateKey(ex)) {
                    // Idempotency: duplicate event treated as successfully handled.
                    return true
                }
                lastError = ex
            } catch (ex: Exception) {
                lastError = ex
            }

            if (attempt < totalAttempts) {
                Thread.sleep(retryBackoffMs)
            }
        }

        val finalError = lastError ?: IllegalStateException("Unknown DB save failure")
        return handleFinalFailure(event, totalAttempts, finalError)
    }

    private fun handleFinalFailure(event: CouponIssueEventV4, attempts: Int, error: Exception): Boolean {
        return try {
            issueCouponFailureRepository.save(
                IssueCouponFailure(
                    couponId = event.couponId,
                    userId = event.userId,
                    requestId = event.requestId,
                    attempts = attempts,
                    errorMessage = (error.message ?: "Unknown error").take(1000),
                    failedAt = Instant.now()
                )
            )

            couponIssueKafkaPublisherV4.publishFailedEvent(
                CouponIssueFailedEventV4(
                    couponId = event.couponId,
                    userId = event.userId,
                    requestId = event.requestId,
                    attempts = attempts,
                    errorMessage = error.message ?: "Unknown error",
                    failedAt = Instant.now().toString()
                )
            )

            true
        } catch (ex: Exception) {
            log.error(
                "Failed to persist/publish final coupon issue failure. requestId={}",
                event.requestId,
                ex
            )
            false
        }
    }

    private fun isDuplicateKey(ex: DataIntegrityViolationException): Boolean {
        return ex.message?.contains("Duplicate", ignoreCase = true) == true ||
            ex.rootCause?.message?.contains("Duplicate", ignoreCase = true) == true
    }

    private fun deserializeIssue(payload: String): CouponIssueEventV4 {
        val tokens = payload.split("|", limit = 4)
        if (tokens.size != 4) {
            throw IllegalArgumentException("Invalid event format")
        }

        return CouponIssueEventV4(
            couponId = tokens[0].toLong(),
            userId = tokens[1],
            requestId = tokens[2],
            issuedAt = tokens[3]
        )
    }

    @PreDestroy
    fun closeConsumer() {
        runCatching { consumer.close() }
    }
}
