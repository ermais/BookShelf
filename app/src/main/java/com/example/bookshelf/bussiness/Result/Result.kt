package com.example.bookshelf.bussiness.Result



sealed class Result<out R>{
    object Loading : Result<Nothing>()
    data class Success<out T>(val data: T):Result<T>()
    data class Failure(val message: String) : Result<Nothing>()
}

val <T> Result<T>.data: T?
get() = (this as? Result.Success)?.data