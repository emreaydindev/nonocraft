package com.neilb.nonocraft.common

sealed class Resource<T>(val data: T? = null, open val error: AppError? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(override val error: AppError, data: T? = null) : Resource<T>(data, error) {
        constructor(code: ErrorCode, data: T? = null) : this(AppError(code), data)
    }
    class Loading<T>(data: T? = null) : Resource<T>(data)
}
