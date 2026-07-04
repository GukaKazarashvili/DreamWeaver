package com.example.dreamweaver.util

/** Simple sealed wrapper used to model the result of an async/network operation. */
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}
