package com.example.coupon.coupon.application;

import com.example.coupon.coupon.presentation.dto.CouponCreateRequest;

public interface CouponProvider {

    void createCoupon(CouponCreateRequest couponCreateRequest);
}
