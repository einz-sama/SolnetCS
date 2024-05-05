package com.einz.solnetcs.data

sealed class State<out T> {
    data class Success<out T>(val data: T) : State<T>()
    data class Error(val errorMessage: String) : State<Nothing>()
    object Loading : State<Nothing>()
}