package com.yudistudios.foodland.utils

class Status(var result: Result)

enum class Result {
    SUCCESS,
    NETWORK_ERROR,
    WAITING,
    INCORRECT_PASSWORD,
    EMAIL_IN_USE,
    NONE
}