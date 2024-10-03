package com.example.coupon.coupon.domain.factory.impl;

import com.example.coupon.coupon.domain.entity.Coupon;
import com.example.coupon.coupon.domain.entity.UserCoupon;
import com.example.coupon.coupon.domain.factory.UserCouponFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class JpaEntityUserCouponFactory implements UserCouponFactory {

    @Override
    public UserCoupon create(String userId, Coupon coupon) {
        return UserCoupon.builder()
                .couponId(coupon.getCouponId())
                .userId(userId)
                .issuedAt(LocalDateTime.now())
                .build();
    }
}
