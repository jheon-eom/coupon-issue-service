package com.example.coupon.dao.jpa;

import com.example.coupon.model.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public class JpaUserCouponRepository extends JpaRepository<UserCoupon, Long> {
}
