package com.example.coupon.coupon.domain.factory;

import com.example.coupon.coupon.domain.entity.Coupon;

public interface CouponFactory {

    Coupon create(String name, int quantity);
}
