package com.example.couponservice.v3.controller

import com.example.couponservice.v3.dto.IssueCouponResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["com.example.couponservice.v3"])
class CouponControllerV3Advice {
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<IssueCouponResponse> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(IssueCouponResponse.error("Internal server error."))
    }
}
