package com.example.couponservice.v4.service

import com.example.couponservice.v4.dto.CouponIssueEventV4
import com.example.couponservice.v4.dto.CouponIssueFailedEventV4
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class CouponIssueKafkaPublisherV4(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${coupon.v4.kafka.topic:coupon.issue.v4}")
    private val issueTopic: String,
    @Value("\${coupon.v4.kafka.failed-topic:coupon.issue.failed.v4}")
    private val failedTopic: String,
    @Value("\${coupon.v4.producer.send-timeout-ms:5000}")
    private val sendTimeoutMs: Long
) {
    fun publishIssueEvent(event: CouponIssueEventV4) {
        val payload = serializeIssue(event)
        kafkaTemplate.send(issueTopic, event.requestId, payload).get(sendTimeoutMs, TimeUnit.MILLISECONDS)
    }

    fun publishFailedEvent(event: CouponIssueFailedEventV4) {
        val payload = serializeFailed(event)
        kafkaTemplate.send(failedTopic, event.requestId, payload).get(sendTimeoutMs, TimeUnit.MILLISECONDS)
    }

    private fun serializeIssue(event: CouponIssueEventV4): String {
        return listOf(event.couponId.toString(), event.userId, event.requestId, event.issuedAt).joinToString("|")
    }

    private fun serializeFailed(event: CouponIssueFailedEventV4): String {
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
