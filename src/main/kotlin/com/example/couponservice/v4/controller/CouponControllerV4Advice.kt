package com.example.couponservice.v4.controller

import com.example.couponservice.v4.dto.IssueCouponResponseV4
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["com.example.couponservice.v4"])
class CouponControllerV4Advice {
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<IssueCouponResponseV4> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(IssueCouponResponseV4.error("Internal server error."))
    }
}
