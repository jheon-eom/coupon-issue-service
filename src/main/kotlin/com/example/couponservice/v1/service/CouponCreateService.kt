package com.example.couponservice.v1.service

import com.example.couponservice.domain.Coupon
import com.example.couponservice.domain.CouponRepository
import org.springframework.stereotype.Service

@Service
class CouponCreateService(
    private val couponRepository: CouponRepository
) {
    fun create(name: String): Long {
        val savedCoupon = couponRepository.save(
            Coupon(name = name)
        )

        return savedCoupon.id ?: throw RuntimeException("Coupon create is failed.")
    }
}