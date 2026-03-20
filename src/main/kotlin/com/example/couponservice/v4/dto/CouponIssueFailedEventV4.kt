package com.example.couponservice.v4.dto

data class CouponIssueFailedEventV4(
    val couponId: Long,
    val userId: String,
    val requestId: String,
    val attempts: Int,
    val errorMessage: String,
    val failedAt: String
)
