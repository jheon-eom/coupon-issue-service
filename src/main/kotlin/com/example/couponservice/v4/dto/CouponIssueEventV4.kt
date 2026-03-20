package com.example.couponservice.v4.dto

data class CouponIssueEventV4(
    val couponId: Long,
    val userId: String,
    val requestId: String,
    val issuedAt: String
)
