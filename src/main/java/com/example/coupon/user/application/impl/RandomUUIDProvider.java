package com.example.coupon.user.application.impl;

import com.example.coupon.user.application.IdProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RandomUUIDProvider implements IdProvider {

    @Override
    public String issueId() {
        return null;
    }
}
