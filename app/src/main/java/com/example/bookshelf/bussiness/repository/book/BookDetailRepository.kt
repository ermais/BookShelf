package com.example.bookshelf.bussiness.repository.book

import com.example.bookshelf.bussiness.db.BookDao

class BookDetailRepository(private val db:BookDao) {

    suspend fun getBook(title:String) = db.getBook(title)
}