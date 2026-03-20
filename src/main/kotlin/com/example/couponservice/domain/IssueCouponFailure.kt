package com.example.couponservice.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.Instant

@Entity
class IssueCouponFailure(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    val couponId: Long,

    @Column(nullable = false)
    val userId: String,

    @Column(nullable = false)
    val requestId: String,

    @Column(nullable = false)
    val attempts: Int,

    @Column(nullable = false, length = 1000)
    val errorMessage: String,

    @Column(nullable = false)
    val failedAt: Instant = Instant.now()
)
