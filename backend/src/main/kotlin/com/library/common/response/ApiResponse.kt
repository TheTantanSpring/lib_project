package com.library.common.response

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun <T> success(data: T, message: String = "성공"): ApiResponse<T> {
            return ApiResponse(
                success = true,
                message = message,
                data = data
            )
        }

        fun <T> success(message: String = "성공"): ApiResponse<T> {
            return ApiResponse(
                success = true,
                message = message,
                data = null
            )
        }

        fun <T> error(message: String): ApiResponse<T> {
            return ApiResponse(
                success = false,
                message = message,
                data = null
            )
        }
    }
} 