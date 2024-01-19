package com.example.bookshelf.bussiness.repository.book

import android.net.Uri
import com.example.bookshelf.bussiness.db.BookDao
import com.example.bookshelf.bussiness.networkdata.FirestoreBookDataSource

class BookEditRepository(private val db: BookDao,private val firestoreBookDataSource: FirestoreBookDataSource) {

    suspend fun getBookById(bookId: String) = db.getBookById(bookId)

    suspend fun editBook(bookId : String,vararg obj : Map<String,String?>) =
        firestoreBookDataSource.editBook(bookId,*obj)

    suspend fun uploadBookDoc(uriFromFile: Uri, bookTitle: String) =
        firestoreBookDataSource.uploadBookDoc(uriFromFile, bookTitle)

    suspend fun uploadBookCover(uriFromFile: Uri, bookTitle: String) =
        firestoreBookDataSource.uploadBookCover(uriFromFile, bookTitle)

    
}

