package com.example.coupon.dao.impl;

import com.example.coupon.dao.UserCouponRepository;
import com.example.coupon.model.UserCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserCouponRepositoryImpl implements UserCouponRepository {

    private final JpaUserCouponRepository jpaUserCouponRepository;

    @Override
    public void save(UserCoupon userCoupon) {

    }
}
