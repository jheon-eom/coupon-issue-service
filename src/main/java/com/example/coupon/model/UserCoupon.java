package com.example.coupon.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserCoupon {

    private Long id;

    private String coupon;

    private String userCode;

    public static UserCoupon issue(String coupon, String userCode) {
        return new UserCoupon(coupon, userCode);
    }
}
