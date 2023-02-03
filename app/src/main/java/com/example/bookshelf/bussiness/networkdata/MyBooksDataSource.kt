package com.example.bookshelf.bussiness.networkdata

import com.example.bookshelf.bussiness.Result.Result
import com.example.bookshelf.bussiness.db.MyBooksEntity
import com.example.bookshelf.bussiness.model.MyBooks
import kotlinx.coroutines.flow.Flow

interface MyBooksDataSource {
    suspend fun buyBook(book: MyBooksEntity): Flow<Result<Void>>

    suspend fun getMyBooks(user: String): Flow<Result<MutableList<MyBooks>>>

}