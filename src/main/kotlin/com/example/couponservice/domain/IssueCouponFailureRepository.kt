package com.example.couponservice.domain

import org.springframework.data.jpa.repository.JpaRepository

interface IssueCouponFailureRepository : JpaRepository<IssueCouponFailure, Long>
