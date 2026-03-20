package com.example.couponservice.v5.service

import com.example.couponservice.v5.dto.CouponIssueEventV5

object CouponIssuePayloadCodecV5 {
    // Envelope format: attempts|couponId|userId|requestId|issuedAt
    fun encodeEnvelope(event: CouponIssueEventV5, attempts: Int = 0): String {
        return listOf(
            attempts.toString(),
            event.couponId.toString(),
            event.userId,
            event.requestId,
            event.issuedAt
        ).joinToString("|")
    }

    fun decodeEnvelope(envelope: String): DecodedEnvelope {
        val tokens = envelope.split('|', limit = 5)
        if (tokens.size != 5) {
            throw IllegalArgumentException("Invalid v5 envelope format")
        }

        return DecodedEnvelope(
            attempts = tokens[0].toInt(),
            event = CouponIssueEventV5(
                couponId = tokens[1].toLong(),
                userId = tokens[2],
                requestId = tokens[3],
                issuedAt = tokens[4]
            )
        )
    }

    data class DecodedEnvelope(
        val attempts: Int,
        val event: CouponIssueEventV5
    )
}
