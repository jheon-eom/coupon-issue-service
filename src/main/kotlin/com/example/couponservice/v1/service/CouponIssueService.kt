package com.example.couponservice.v1.service

import com.example.couponservice.domain.CouponRepository
import com.example.couponservice.domain.IssueCoupon
import com.example.couponservice.domain.IssueCouponRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CouponIssueService(
    private val couponRepository: CouponRepository,
    private val issueCouponRepository: IssueCouponRepository
) {
    @Transactional
    fun issue(name: String): Boolean {
        val coupon = couponRepository.findByName(name)
            ?: throw IllegalArgumentException("Coupon not found with name: $name")

        val userId = UUID.randomUUID().toString()

        issueCouponRepository.save(
            IssueCoupon(
                couponId = coupon.id!!,
                userId = userId
            )
        )

        coupon.issue()

        return true
    }
}