package com.example.bookshelf.bussiness.db
data class DownloadAndBook(
    val title: String,
    val desc: String?,
    val bookId: String,
    val downloadDate: Long,
    val bookUri: String
)