package com.example.couponservice.v5.dto

data class IssueCouponResponseV5(
    val success: Boolean,
    val code: String,
    val message: String
) {
    companion object {
        fun issued() = IssueCouponResponseV5(
            success = true,
            code = "ISSUED",
            message = "Coupon issued successfully."
        )

        fun soldOut() = IssueCouponResponseV5(
            success = false,
            code = "SOLD_OUT",
            message = "Coupon is sold out."
        )

        fun error(message: String) = IssueCouponResponseV5(
            success = false,
            code = "ERROR",
            message = message
        )
    }
}
