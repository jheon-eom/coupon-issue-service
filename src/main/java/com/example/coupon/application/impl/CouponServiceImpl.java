package com.example.coupon.application.impl;

import com.example.coupon.application.CouponService;
import com.example.coupon.model.Coupon;
import com.example.coupon.model.UserCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements CouponService {

    private Coupon coupon;

    private final UserCouponRepository userCouponRepository;

    @Override
    public void create() {
        if (this.coupon != null) {
            throw new IllegalArgumentException("이미 쿠폰이 발행되었습니다.");
        }

        synchronized (this) {
            if (this.coupon == null) {
                this.coupon = Coupon.create("eventCoupon", 200);
            }
        }
    }

    @Override
    public void request(String userCode) {
        if (this.coupon.isClosed()) {
            throw new UnsupportedOperationException("선착순 쿠폰 발행이 마감되었습니다.");
        }

        synchronized (this) {
            this.coupon.issue();
            UserCoupon userCoupon = UserCoupon.issue(this.coupon.getName(), userCode);
            userCouponRepository.save(userCoupon);
        }
    }
}
