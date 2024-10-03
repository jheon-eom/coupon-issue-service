package com.example.coupon.coupon.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "coupon")
@Entity
public class Coupon {

    @Column(name = "coupon_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long couponId;

    @Column(name = "name")
    private String name;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
