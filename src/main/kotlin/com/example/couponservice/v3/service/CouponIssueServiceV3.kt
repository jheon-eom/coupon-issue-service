package com.example.couponservice.v3.service

import com.example.couponservice.domain.CouponRepository
import com.example.couponservice.domain.IssueCoupon
import com.example.couponservice.domain.IssueCouponRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CouponIssueServiceV3(
    private val couponRepository: CouponRepository,
    private val issueCouponRepository: IssueCouponRepository,
    private val couponIssueCounter: CouponIssueCounter
) {
    @Transactional
    fun issue(name: String): CouponIssueResult {
        val coupon = couponRepository.findByName(name)
            ?: throw IllegalArgumentException("Coupon not found with name: $name")

        val couponId = coupon.id ?: throw IllegalStateException("Coupon id is null. name: $name")
        val issuedCount = couponIssueCounter.increment(couponId)

        if (issuedCount > coupon.issuedCount) {
            return CouponIssueResult.SOLD_OUT
        }

        val userId = UUID.randomUUID().toString()

        try {
            issueCouponRepository.save(
                IssueCoupon(
                    couponId = couponId,
                    userId = userId
                )
            )
        } catch (ex: RuntimeException) {
            // Keep Redis counter aligned when DB write fails after a successful reservation.
            couponIssueCounter.decrement(couponId)
            throw ex
        }

        return CouponIssueResult.ISSUED
    }
}

enum class CouponIssueResult {
    ISSUED,
    SOLD_OUT
}
