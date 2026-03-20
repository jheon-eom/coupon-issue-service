package com.example.couponservice.domain

import org.springframework.data.jpa.repository.JpaRepository

interface IssueCouponRepository: JpaRepository<IssueCoupon, Long> {
}