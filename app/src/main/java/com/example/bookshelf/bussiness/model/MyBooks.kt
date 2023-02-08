package com.example.bookshelf.bussiness.model

import com.example.bookshelf.bussiness.db.MyBooksEntity
import java.util.*


data class MyBooks(
    val id: Int = 0,
    val user: String,
    val bookId: String,
    val boughtDate: Long = Date().time
) {
    constructor() : this(
        id = 0,
        user = "",
        bookId = "",
        boughtDate = 0L
    ) {

    }
}

fun List<MyBooks>.asMyBookEntity(): List<MyBooksEntity> {
    return map {
        MyBooksEntity(
            it.user,
            it.bookId,
            it.boughtDate
        )
    }
}
