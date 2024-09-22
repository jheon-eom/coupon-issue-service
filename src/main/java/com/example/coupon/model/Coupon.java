package com.example.coupon.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Coupon {

    private String name;

    private int quantity;

    public static Coupon create(String name, int quantity) {
        return new Coupon(name, quantity);
    }

    public void issue() {
        if (this.quantity <= 0) {
            throw new UnsupportedOperationException();
        }
        this.quantity--;
    }

    public boolean isClosed() {
        return this.quantity <= 0;
    }
}
