package com.example.couponservice.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Coupon(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    val name: String,

    @Column(nullable = false)
    var issuedCount: Int = 500
) {
    fun issue() {
        if (issuedCount <= 0) {
            throw RuntimeException("Coupon is not available. couponId: $id")
        }

        issuedCount -= 1
    }
}