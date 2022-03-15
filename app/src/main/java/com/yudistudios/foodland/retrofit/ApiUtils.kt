package com.yudistudios.foodland.retrofit

class ApiUtils {
    companion object {
        const val BASE_URL = "http://kasimadalan.pe.hu/yemekler/"
        const val GET_ALL_FOOD = "tumYemekleriGetir.php"
        const val GET_BASKET = "sepettekiYemekleriGetir.php"
        const val ADD_FOOD_TO_BASKET = "sepeteYemekEkle.php"
        const val REMOVE_FOOD_FROM_BASKET = "sepettenYemekSil.php"
    }
}