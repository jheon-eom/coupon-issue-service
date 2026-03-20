package com.example.couponservice.v4.controller

import com.example.couponservice.v1.service.CouponCreateService
import com.example.couponservice.v3.service.CouponIssueCounter
import com.example.couponservice.v4.dto.IssueCouponResponseV4
import com.example.couponservice.v4.service.CouponIssueServiceV4
import com.example.couponservice.v4.service.CouponIssueResultV4
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v4/coupons")
class CouponControllerV4(
    private val couponCreateService: CouponCreateService,
    private val couponIssueServiceV4: CouponIssueServiceV4,
    private val couponIssueCounter: CouponIssueCounter
) {
    @PostMapping
    fun create(): Long {
        val couponId = couponCreateService.create(COUPON_NAME)
        couponIssueCounter.initialize(couponId)
        return couponId
    }

    @PostMapping("/issue")
    fun issue(): ResponseEntity<IssueCouponResponseV4> {
        return when (couponIssueServiceV4.issue(COUPON_NAME)) {
            CouponIssueResultV4.ISSUED -> ResponseEntity.ok(IssueCouponResponseV4.issued())
            CouponIssueResultV4.SOLD_OUT -> ResponseEntity.ok(IssueCouponResponseV4.soldOut())
        }
    }

    companion object {
        private const val COUPON_NAME = "EventCouponV4"
    }
}
