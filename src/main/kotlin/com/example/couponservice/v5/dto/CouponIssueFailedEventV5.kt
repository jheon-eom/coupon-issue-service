package com.example.couponservice.v5.dto

data class CouponIssueFailedEventV5(
    val couponId: Long,
    val userId: String,
    val requestId: String,
    val attempts: Int,
    val errorMessage: String,
    val failedAt: String
)
