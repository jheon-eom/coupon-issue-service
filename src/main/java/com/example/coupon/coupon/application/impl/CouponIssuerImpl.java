package com.example.coupon.coupon.application.impl;

import com.example.coupon.coupon.application.CouponIssuer;
import com.example.coupon.coupon.application.CouponService;
import com.example.coupon.coupon.domain.entity.Coupon;
import com.example.coupon.coupon.domain.entity.UserCoupon;
import com.example.coupon.coupon.domain.factory.UserCouponFactory;
import com.example.coupon.user.application.IdProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponIssuerImpl implements CouponIssuer {

    /** Service */
    private final IdProvider idProvider;
    private final CouponService couponService;

    /** Infrastructure */
    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, Long, Integer> hashOperations;

    /** Model factory */
    private final UserCouponFactory userCouponFactory;

    @Override
    public void issueCoupon() {
        String id = idProvider.issueId();
        Coupon coupon = couponService.getTodayEventCoupon();

        /**
         * 레디스에 저장되어 있는 쿠폰 조회
         * 쿠폰의 수량 검사
         * 회원이 이미 발급받았는지 여부 검사
         * 처음 발급 받는 회원이라면 레디스 락 획득 후 쿠폰 발급
         * 락 해제
         * 발급에 성공하면 데이터베이스 저장
         * 트랜잭션 종료
         */

        UserCoupon userCoupon = userCouponFactory.create(id, coupon);
    }
}
