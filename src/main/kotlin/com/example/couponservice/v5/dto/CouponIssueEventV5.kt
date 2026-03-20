package com.example.couponservice.v5.dto

data class CouponIssueEventV5(
    val couponId: Long,
    val userId: String,
    val requestId: String,
    val issuedAt: String
)
