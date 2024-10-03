package com.example.coupon.coupon.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user_coupon")
@Entity
public class UserCoupon {

    @Column(name = "user_coupon_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long userCouponId;

    @Column(name = "coupon_id")
    private Long couponId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;
}
