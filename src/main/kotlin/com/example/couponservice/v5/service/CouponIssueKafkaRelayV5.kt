package com.example.couponservice.v5.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CouponIssueKafkaRelayV5(
    private val couponIssuePendingQueueV5: CouponIssuePendingQueueV5,
    private val couponIssueKafkaPublisherV5: CouponIssueKafkaPublisherV5,
    @Value("\${coupon.v5.relay.batch-size:200}")
    private val batchSize: Int,
    @Value("\${coupon.v5.relay.retry-backoff-ms:200}")
    private val retryBackoffMs: Long,
    @Value("\${coupon.v5.relay.max-retries:0}")
    private val maxRetries: Int,
    @Value("\${coupon.v5.relay.recover-processing-batch-size:200}")
    private val recoverProcessingBatchSize: Int
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelayString = "\${coupon.v5.relay.fixed-delay-ms:100}")
    fun relayChunk() {
        val now = System.currentTimeMillis()
        couponIssuePendingQueueV5.moveDueRetriesToPending(limit = batchSize * 2, nowEpochMillis = now)
        couponIssuePendingQueueV5.recoverProcessingToPending(limit = recoverProcessingBatchSize)

        repeat(batchSize) {
            val envelope = couponIssuePendingQueueV5.popToProcessing() ?: return
            processEnvelope(envelope)
        }
    }

    private fun processEnvelope(envelope: String) {
        val decoded = try {
            CouponIssuePayloadCodecV5.decodeEnvelope(envelope)
        } catch (ex: Exception) {
            couponIssuePendingQueueV5.putRelayFailed(envelope, "Invalid envelope")
            couponIssuePendingQueueV5.acknowledge(envelope)
            return
        }

        try {
            couponIssueKafkaPublisherV5.publishIssueEventFromEnvelope(envelope)
            couponIssuePendingQueueV5.acknowledge(envelope)
        } catch (ex: Exception) {
            couponIssuePendingQueueV5.acknowledge(envelope)

            val nextAttempts = decoded.attempts + 1
            if (maxRetries > 0 && nextAttempts > maxRetries) {
                couponIssuePendingQueueV5.putRelayFailed(envelope, ex.message)
                log.error("Relay max retries exceeded. requestId={}", decoded.event.requestId, ex)
                return
            }

            val nextEnvelope = CouponIssuePayloadCodecV5.encodeEnvelope(decoded.event, attempts = nextAttempts)
            val nextRetryAt = System.currentTimeMillis() + (retryBackoffMs * nextAttempts.coerceAtLeast(1))
            couponIssuePendingQueueV5.scheduleRetry(nextEnvelope, nextRetryAt)
            log.warn(
                "Relay publish failed. requestId={}, attempts={}, error={}",
                decoded.event.requestId,
                nextAttempts,
                ex.message
            )
        }
    }
}
