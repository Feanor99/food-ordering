package com.yudistudios.foodordering.utils

class HttpRequestStatus(var result: HttpRequestResult)

enum class HttpRequestResult {
    SUCCESS,
    FAILED,
    WAITING
}