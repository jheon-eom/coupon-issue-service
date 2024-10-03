package com.example.coupon.coupon.domain.factory;

import com.example.coupon.coupon.domain.entity.Coupon;
import com.example.coupon.coupon.domain.entity.UserCoupon;

public interface UserCouponFactory {

    UserCoupon create(String id, Coupon coupon);
}
