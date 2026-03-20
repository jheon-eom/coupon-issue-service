package com.example.couponservice.v3.controller

import com.example.couponservice.v1.service.CouponCreateService
import com.example.couponservice.v3.dto.IssueCouponResponse
import com.example.couponservice.v3.service.CouponIssueCounter
import com.example.couponservice.v3.service.CouponIssueResult
import com.example.couponservice.v3.service.CouponIssueServiceV3
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v3/coupons")
class CouponControllerV3(
    private val couponCreateService: CouponCreateService,
    private val couponIssueServiceV3: CouponIssueServiceV3,
    private val couponIssueCounter: CouponIssueCounter
) {
    @PostMapping
    fun create(): Long {
        val couponId = couponCreateService.create(COUPON_NAME)
        couponIssueCounter.initialize(couponId)
        return couponId
    }

    @PostMapping("/issue")
    fun issue(): ResponseEntity<IssueCouponResponse> {
        return when (couponIssueServiceV3.issue(COUPON_NAME)) {
            CouponIssueResult.ISSUED -> ResponseEntity.ok(IssueCouponResponse.issued())
            CouponIssueResult.SOLD_OUT -> ResponseEntity.ok(IssueCouponResponse.soldOut())
        }
    }

    companion object {
        private const val COUPON_NAME = "EventCouponV3"
    }
}
