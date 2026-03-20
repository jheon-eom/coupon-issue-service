package com.example.couponservice.v3.service

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class CouponIssueCounter(
    private val stringRedisTemplate: StringRedisTemplate
) {
    fun initialize(couponId: Long) {
        stringRedisTemplate.opsForValue().set(key(couponId), "0")
    }

    fun increment(couponId: Long): Long {
        return stringRedisTemplate.opsForValue().increment(key(couponId))
            ?: throw IllegalStateException("Failed to increment coupon counter. couponId: $couponId")
    }

    fun decrement(couponId: Long) {
        stringRedisTemplate.opsForValue().decrement(key(couponId))
    }

    private fun key(couponId: Long): String {
        return "coupon:$couponId:issued"
    }
}
