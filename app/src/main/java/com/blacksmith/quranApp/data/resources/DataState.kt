package com.blacksmith.quranApp.data.resources

sealed class DataState<out R> {
    data class Success<out T>(val response: T) : DataState<T>()
    data class ErrorInServer<out T>(val error: T) : DataState<T>()
    data class ErrorInNetwork(val error: String) : DataState<String>()
    data class Unauthenticated(val error: String?) : DataState<Nothing>()
    data object Loading : DataState<Nothing>()
}
