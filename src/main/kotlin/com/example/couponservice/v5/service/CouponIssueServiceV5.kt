package com.example.couponservice.v5.service

import com.example.couponservice.domain.Coupon
import com.example.couponservice.domain.CouponRepository
import com.example.couponservice.v5.dto.CouponIssueEventV5
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class CouponIssueServiceV5(
    private val couponRepository: CouponRepository,
    private val couponIssuePendingQueueV5: CouponIssuePendingQueueV5
) {
    private var coupon: Coupon? = null

    fun issue(name: String): CouponIssueResultV5 {
        if (coupon == null) {
            coupon = couponRepository.findByName(name)
                ?: throw IllegalArgumentException("Coupon not found with name: $name")
        }

        val targetCoupon = coupon ?: throw IllegalStateException("Coupon cache is null")
        val couponId = targetCoupon.id ?: throw IllegalStateException("Coupon id is null. name: $name")

        val event = CouponIssueEventV5(
            couponId = couponId,
            userId = UUID.randomUUID().toString(),
            requestId = UUID.randomUUID().toString(),
            issuedAt = Instant.now().toString()
        )
        val envelope = CouponIssuePayloadCodecV5.encodeEnvelope(event, attempts = 0)

        val reserved = couponIssuePendingQueueV5.reserveAndEnqueue(
            couponId = couponId,
            issueLimit = targetCoupon.issuedCount,
            envelope = envelope
        )

        return if (reserved) CouponIssueResultV5.ISSUED else CouponIssueResultV5.SOLD_OUT
    }
}
