package com.example.couponservice.v1.controller

import com.example.couponservice.v1.service.CouponCreateService
import com.example.couponservice.v1.service.CouponIssueService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/coupons")
class CouponControllerV1(
    private val couponCreateService: CouponCreateService,
    private val couponIssueService: CouponIssueService
) {
    @PostMapping
    fun create(): Long {
        return couponCreateService.create("EventCoupon")
    }

    @PostMapping("/issue")
    fun issue(): Boolean {
        return couponIssueService.issue("EventCoupon")
    }
}