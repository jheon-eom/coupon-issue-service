package com.example.couponservice.domain

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param

interface CouponRepository: JpaRepository<Coupon, Long> {
    fun findByName(name: String): Coupon?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Coupon c where c.name = :name")
    fun findByNameWithLock(@Param("name") name: String): Coupon?
}
