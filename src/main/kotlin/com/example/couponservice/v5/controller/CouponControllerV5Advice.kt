package com.example.couponservice.v5.controller

import com.example.couponservice.v5.dto.IssueCouponResponseV5
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["com.example.couponservice.v5"])
class CouponControllerV5Advice {
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<IssueCouponResponseV5> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(IssueCouponResponseV5.error("Internal server error."))
    }
}
