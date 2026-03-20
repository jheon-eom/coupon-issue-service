package com.example.couponservice.v5.controller

import com.example.couponservice.v1.service.CouponCreateService
import com.example.couponservice.v3.service.CouponIssueCounter
import com.example.couponservice.v5.dto.IssueCouponResponseV5
import com.example.couponservice.v5.service.CouponIssueResultV5
import com.example.couponservice.v5.service.CouponIssueServiceV5
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v5/coupons")
class CouponControllerV5(
    private val couponCreateService: CouponCreateService,
    private val couponIssueServiceV5: CouponIssueServiceV5,
    private val couponIssueCounter: CouponIssueCounter
) {
    @PostMapping
    fun create(): Long {
        val couponId = couponCreateService.create(COUPON_NAME)
        couponIssueCounter.initialize(couponId)
        return couponId
    }

    @PostMapping("/issue")
    fun issue(): ResponseEntity<IssueCouponResponseV5> {
        return when (couponIssueServiceV5.issue(COUPON_NAME)) {
            CouponIssueResultV5.ISSUED -> ResponseEntity.ok(IssueCouponResponseV5.issued())
            CouponIssueResultV5.SOLD_OUT -> ResponseEntity.ok(IssueCouponResponseV5.soldOut())
        }
    }

    companion object {
        private const val COUPON_NAME = "EventCouponV5"
    }
}
