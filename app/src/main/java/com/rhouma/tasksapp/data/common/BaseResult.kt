package com.rhouma.tasksapp.data.common

sealed class BaseResult<out T : Any, out U : Any> {
    data class Success<T : Any>(val data: T) : BaseResult<T, Nothing>()
    data class Error<U : Any>(val rawResponse: U) : BaseResult<Nothing, U>()
    object Loading : BaseResult<Nothing, Nothing>()
    object Init : BaseResult<Nothing, Nothing>()
    object NoInternet : BaseResult<Nothing, Nothing>()
}
