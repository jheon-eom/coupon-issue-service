package com.example.coupon.coupon.application.impl;

import com.example.coupon.coupon.application.CouponProvider;
import com.example.coupon.coupon.presentation.dto.CouponCreateRequest;
import com.example.coupon.coupon.domain.entity.Coupon;
import com.example.coupon.coupon.domain.factory.CouponFactory;
import com.example.coupon.coupon.infrastructure.repository.CouponRepository;
import com.example.coupon.user.application.IdProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponProviderImpl implements CouponProvider {

    /** Application */
    private final IdProvider idProvider;

    /** Infrastructure */
    private final CouponRepository couponRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, Long, Integer> hashOperations;

    /** Model factory */
    private final CouponFactory couponFactory; // 팩토리 클래스 의존성 역전 시 이점은?

    /** Static variable */
    private static final String COUPON_KEY = "EVENT_COUPON";
    private boolean isCouponRegistered = false;

    @Override
    public void createCoupon(CouponCreateRequest couponCreateRequest) {
        Coupon coupon = couponFactory.create(couponCreateRequest.name(), couponCreateRequest.quantity());
        Coupon savedCoupon = couponRepository.save(coupon);
        // Redis 객체 저장
        synchronized (this) {
            if (!isCouponRegistered) {
                hashOperations.put(COUPON_KEY, savedCoupon.getCouponId(), savedCoupon.getQuantity());
                isCouponRegistered = true;
            } else {
                throw new UnsupportedOperationException("Coupon is already registered.");
            }
        }
    }
}
