package com.example.coupon.coupon.infrastructure.repository;

import com.example.coupon.coupon.domain.entity.Coupon;

import java.util.Optional;

public interface CouponRepository {

    Coupon save(Coupon coupon);

    Optional<Coupon> getTodayEventCoupon();
}
