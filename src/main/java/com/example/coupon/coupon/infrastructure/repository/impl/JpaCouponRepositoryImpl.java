package com.example.coupon.coupon.infrastructure.repository.impl;

import com.example.coupon.coupon.domain.entity.Coupon;
import com.example.coupon.coupon.infrastructure.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JpaCouponRepositoryImpl implements CouponRepository {

    private final com.example.coupon.coupon.infrastructure.repository.jpa.CouponRepository couponRepository;

    @Override
    public Coupon save(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Override
    public Optional<Coupon> getTodayEventCoupon() {
         return couponRepository.findAll().stream().findFirst();
    }
}
