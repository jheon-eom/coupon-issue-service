package com.example.coupon.coupon.presentation;

import com.example.coupon.coupon.application.CouponIssuer;
import com.example.coupon.coupon.application.CouponProvider;
import com.example.coupon.coupon.presentation.dto.CouponCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/points")
@RestController
public class CouponController {

    private final CouponProvider couponProvider;
    private final CouponIssuer couponIssuer;

    @PostMapping
    public ResponseEntity<Void> createCoupon(@RequestBody CouponCreateRequest couponCreateRequest) {
        couponProvider.createCoupon(couponCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED.value()).build();
    }

    @PostMapping("/issue")
    public ResponseEntity<Void> issueCoupon() {
        couponIssuer.issueCoupon();
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
