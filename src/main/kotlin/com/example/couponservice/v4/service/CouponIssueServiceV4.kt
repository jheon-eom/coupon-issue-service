package com.example.couponservice.v4.service

import com.example.couponservice.domain.Coupon
import com.example.couponservice.domain.CouponRepository
import com.example.couponservice.v4.dto.CouponIssueEventV4
import com.example.couponservice.v3.service.CouponIssueCounter
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class CouponIssueServiceV4(
    private val couponRepository: CouponRepository,
    private val couponIssueCounter: CouponIssueCounter,
    private val couponIssueKafkaPublisherV4: CouponIssueKafkaPublisherV4
) {
    private var coupon: Coupon? = null

    fun issue(name: String): CouponIssueResultV4 {
        if (coupon == null) {
            coupon = couponRepository.findByName(name)
                ?: throw IllegalArgumentException("Coupon not found with name: $name")
        }

        val couponId = coupon!!.id ?: throw IllegalStateException("Coupon id is null. name: $name")
        val issuedCount = couponIssueCounter.increment(couponId)

        if (issuedCount > coupon!!.issuedCount) {
            return CouponIssueResultV4.SOLD_OUT
        }

        val userId = UUID.randomUUID().toString()
        val requestId = UUID.randomUUID().toString()

        try {
            couponIssueKafkaPublisherV4.publishIssueEvent(
                CouponIssueEventV4(
                    couponId = couponId,
                    userId = userId,
                    requestId = requestId,
                    issuedAt = Instant.now().toString()
                )
            )
        } catch (ex: RuntimeException) {
            // Roll back reservation when enqueue fails.
            couponIssueCounter.decrement(couponId)
            throw ex
        }

        return CouponIssueResultV4.ISSUED
    }
}
