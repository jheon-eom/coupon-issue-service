package com.example.coupon.application;

public interface CouponService {

    // 쿠폰을 생성한다.
    void create();

    // 쿠폰 발급을 요청한다.
    void request(String userCode);
}
