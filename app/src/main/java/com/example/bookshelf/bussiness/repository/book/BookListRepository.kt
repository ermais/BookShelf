package com.example.bookshelf.bussiness.repository.book

import com.example.bookshelf.bussiness.Result.data
import com.example.bookshelf.bussiness.db.BookDatabase
import com.example.bookshelf.bussiness.db.DownloadEntity
import com.example.bookshelf.bussiness.model.Book
import com.example.bookshelf.bussiness.model.asBookEntity
import com.example.bookshelf.bussiness.networkdata.FirestoreBookDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BookListRepository(
    val firestoreBookDataSource: FirestoreBookDataSource,
    private val db: BookDatabase
) {
    var fireDb = Firebase.firestore
    var user = FirebaseAuth.getInstance().currentUser

    suspend fun getBookList() = firestoreBookDataSource.getBooksFromFirestore()
    suspend fun filterByCategory(category: String) =
        firestoreBookDataSource.filterByCategory(category)

    suspend fun filterByAuthor(author: String) = firestoreBookDataSource.filterByCategory(author)

    suspend fun refreshBooks() {
        var _books = listOf<Book>()
        getBookList().collect {
            _books = it.data as List<Book>
            clearDatabase()
            db.bookDao().insertBooks(_books.asBookEntity())
        }
    }

    fun getOfflineBooks() =
        db.bookDao().getBooks()

    private suspend fun clearDatabase() = db.bookDao().clear()

    suspend fun filterByCategoryOffline(category: String) = db.bookDao().filterByCategory(category)

    suspend fun filterBooks(query: String) = db.bookDao().queryBooks(query)

    suspend fun sortByAuthor() = db.bookDao().sortByAuthor()

    suspend fun sortByTitle() = db.bookDao().sortByTitle()

    suspend fun sortByPubDate() = db.bookDao().sortByPubDate()

    suspend fun addDownload(_book: DownloadEntity) = db.downloadDao().addDownload(_book)


}