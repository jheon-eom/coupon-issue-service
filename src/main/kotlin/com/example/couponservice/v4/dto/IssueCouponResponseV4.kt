package com.example.couponservice.v4.dto

data class IssueCouponResponseV4(
    val success: Boolean,
    val code: String,
    val message: String
) {
    companion object {
        fun issued() = IssueCouponResponseV4(
            success = true,
            code = "ISSUED",
            message = "Coupon issued successfully."
        )

        fun soldOut() = IssueCouponResponseV4(
            success = false,
            code = "SOLD_OUT",
            message = "Coupon is sold out."
        )

        fun error(message: String) = IssueCouponResponseV4(
            success = false,
            code = "ERROR",
            message = message
        )
    }
}
