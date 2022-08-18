package com.example.bookshelf.model.book

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface BookDataSource {
    suspend fun publishBook(book:Book) : Flow<Result<Void>>
    suspend fun getBooksFromFirestore() : Flow<Result<MutableList<Book>>>
    suspend fun updateBook(book: Book) : Flow<Result<Void>>
    suspend fun deleteBook(bookId: String) : Flow<Result<Void?>>
    suspend fun filterByCategory(category : String) : Flow<Result<List<Book>>>
    suspend fun filterByAuthor(author: String) : Flow<Result<List<Book>>>
    suspend fun queryBooks(query:String) : Flow<Result<List<Book>>>
    suspend fun updateBookDoc(bookUID:String,bookTitle:String,bookUriFromFile:Uri):Flow<Result<Void>>
    suspend fun updateBookCover(bookUID:String,bookTitle:String,bookCoverUriFromFile:String): Flow<Result<Void>>
    suspend fun rateBook(rating:String,bookUID:String): Flow<Result<Void>>
    suspend fun sortByDate():Flow<Result<List<Book>>>
    suspend fun sortByBookTitle():Flow<Result<List<Book>>>
    suspend fun uploadBookDoc(uriFromFile:Uri,bookTitle: String) : Flow<Result<Uri>>
    suspend fun uploadBookCover(uriFromFile: Uri,bookTitle: String): Flow<Result<Uri>>

}