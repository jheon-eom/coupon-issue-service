package com.example.couponservice.v3.dto

data class IssueCouponResponse(
    val success: Boolean,
    val code: String,
    val message: String
) {
    companion object {
        fun issued() = IssueCouponResponse(
            success = true,
            code = "ISSUED",
            message = "Coupon issued successfully."
        )

        fun soldOut() = IssueCouponResponse(
            success = false,
            code = "SOLD_OUT",
            message = "Coupon is sold out."
        )

        fun error(message: String) = IssueCouponResponse(
            success = false,
            code = "ERROR",
            message = message
        )
    }
}
