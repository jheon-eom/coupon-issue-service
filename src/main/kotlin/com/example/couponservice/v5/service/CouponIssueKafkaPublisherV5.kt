package com.example.couponservice.v5.service

import com.example.couponservice.v5.dto.CouponIssueFailedEventV5
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class CouponIssueKafkaPublisherV5(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${coupon.v5.kafka.issue-topic:coupon.issue.v5}")
    private val issueTopic: String,
    @Value("\${coupon.v5.kafka.failed-topic:coupon.issue.failed.v5}")
    private val failedTopic: String,
    @Value("\${coupon.v5.producer.send-timeout-ms:5000}")
    private val sendTimeoutMs: Long
) {
    fun publishIssueEventFromEnvelope(envelope: String) {
        val decoded = CouponIssuePayloadCodecV5.decodeEnvelope(envelope)
        val payload = serializeEvent(decoded.event)
        kafkaTemplate.send(issueTopic, decoded.event.requestId, payload).get(sendTimeoutMs, TimeUnit.MILLISECONDS)
    }

    fun publishFailedEvent(event: CouponIssueFailedEventV5) {
        val payload = serializeFailed(event)
        kafkaTemplate.send(failedTopic, event.requestId, payload).get(sendTimeoutMs, TimeUnit.MILLISECONDS)
    }

    private fun serializeEvent(event: com.example.couponservice.v5.dto.CouponIssueEventV5): String {
        return listOf(event.couponId.toString(), event.userId, event.requestId, event.issuedAt).joinToString("|")
    }

    private fun serializeFailed(event: CouponIssueFailedEventV5): String {
        val sanitizedError = event.errorMessage.replace("\n", " ").replace("|", "/")
        return listOf(
            event.couponId.toString(),
            event.userId,
            event.requestId,
            event.attempts.toString(),
            sanitizedError,
            event.failedAt
        ).joinToString("|")
    }
}
