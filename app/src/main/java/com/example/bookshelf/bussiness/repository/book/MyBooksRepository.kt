package com.example.bookshelf.bussiness.repository.book

import com.example.bookshelf.bussiness.Result.data
import com.example.bookshelf.bussiness.db.BookDatabase
import com.example.bookshelf.bussiness.db.MyBooksEntity
import com.example.bookshelf.bussiness.model.asMyBookEntity
import com.example.bookshelf.bussiness.networkdata.FirestoreMyBooksDataSource
import com.google.firebase.auth.FirebaseAuth


class MyBooksRepository(
    private val db: BookDatabase,
    private val myBooksNetworkResource: FirestoreMyBooksDataSource
) {
    private val auth = FirebaseAuth.getInstance().currentUser

    suspend fun getMyBooks() = myBooksNetworkResource.getMyBooks(auth!!.uid)

    suspend fun buyBook(book: MyBooksEntity) = myBooksNetworkResource.buyBook(book)

    suspend fun refreshMyBooks() {
        var myBooks = listOf<MyBooksEntity>()
        getMyBooks().collect {
            myBooks = it.data!!.asMyBookEntity()
            println("Network data -------------------------------")
            println(myBooks)
            db.myBooksDao().clear()
            db.myBooksDao().insertAllMyBooks(myBooks)
        }
    }

    fun getOfflineMyBooks() = db.myBooksDao().getMyBooks()
}