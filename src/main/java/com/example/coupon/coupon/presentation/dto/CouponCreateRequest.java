package com.example.coupon.coupon.presentation.dto;

public record CouponCreateRequest(
        String name,
        int quantity
) {
    public CouponCreateRequest(String name, int quantity) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Coupon name is required.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Coupon quantity is more then zero.");
        }
        this.name = name;
        this.quantity = quantity;
    }
}
