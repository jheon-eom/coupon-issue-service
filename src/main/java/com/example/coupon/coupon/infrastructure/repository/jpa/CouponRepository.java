package com.example.coupon.coupon.infrastructure.repository.jpa;

import com.example.coupon.coupon.domain.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
