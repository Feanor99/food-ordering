package com.yudistudios.foodland.models

data class CreditCard(
    val bank: String,
    val cardNo: String,
    val holderName: String,
    val expireMonth: String,
    val expireYear: String,
    val CVV: String
) {
    override fun toString(): String {
        return "$bank,$cardNo,$holderName,$expireMonth,$expireYear,$CVV"
    }
}
