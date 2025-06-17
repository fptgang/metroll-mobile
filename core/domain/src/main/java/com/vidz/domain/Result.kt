package com.vidz.domain

sealed class Result<out T>{
    object Init : Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    sealed class ServerError(open val message: String) : Result<Nothing>(){
        data class Token(override val message: String) : ServerError(message)
        data class General(override val message: String) : ServerError(message)
        data class MissingParam(override val message: String) : ServerError(message)
        data class RequiredLogin(override val message: String) : ServerError(message)
        data class RequiredVip(override val message: String) : ServerError(message)
        data class NotEnoughCredit(override val message: String) : ServerError(message)
        data class Internet(override val message: String) : ServerError(message)
    }
}
