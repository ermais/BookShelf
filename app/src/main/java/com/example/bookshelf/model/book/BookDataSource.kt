package com.example.bookshelf.model.book

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

}