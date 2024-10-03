package com.example.coupon.coupon.domain.factory.impl;

import com.example.coupon.coupon.domain.entity.Coupon;
import com.example.coupon.coupon.domain.factory.CouponFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class JpaEntityCouponFactory implements CouponFactory {

    @Override
    public Coupon create(String name, int quantity) {
        Coupon coupon = Coupon.builder()
                .name(name)
                .quantity(quantity)
                .createdAt(LocalDateTime.now())
                .build();
        return coupon;
    }
}
