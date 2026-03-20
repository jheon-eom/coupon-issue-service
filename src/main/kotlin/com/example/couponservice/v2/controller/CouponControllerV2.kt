package com.example.couponservice.v2.controller

import com.example.couponservice.v1.service.CouponCreateService
import com.example.couponservice.v2.service.CouponIssueServiceV2
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v2/coupons")
class CouponControllerV2(
    private val couponCreateService: CouponCreateService,
    private val couponIssueServiceV2: CouponIssueServiceV2
) {
    @PostMapping
    fun create(): Long {
        return couponCreateService.create("EventCoupon")
    }

    @PostMapping("/issue")
    fun issue(): Boolean {
        return couponIssueServiceV2.issue("EventCoupon")
    }
}
