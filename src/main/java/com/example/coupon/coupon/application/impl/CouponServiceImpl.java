package com.example.coupon.coupon.application.impl;

import com.example.coupon.coupon.application.CouponService;
import com.example.coupon.coupon.domain.entity.Coupon;
import com.example.coupon.coupon.infrastructure.repository.CouponRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements CouponService {

    /** Infrastructure */
    private final CouponRepository couponRepository;

    @Override
    public Coupon getTodayEventCoupon() {
        Optional<Coupon> todayEventCoupon = couponRepository.getTodayEventCoupon();
        if (todayEventCoupon.isEmpty()) {
            throw new EntityNotFoundException("Today event coupon is not exist.");
        }
        return todayEventCoupon.get();
    }
}
