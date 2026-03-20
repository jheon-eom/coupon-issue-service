package com.example.couponservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class CouponServiceApplication

fun main(args: Array<String>) {
    runApplication<CouponServiceApplication>(*args)
}
