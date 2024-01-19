package com.example.bookshelf.bussiness.repository.book

import android.net.ConnectivityManager
import com.example.bookshelf.bussiness.Result.data
import com.example.bookshelf.bussiness.db.BookDatabase
import com.example.bookshelf.bussiness.db.DownloadEntity
import com.example.bookshelf.bussiness.model.Book
import com.example.bookshelf.bussiness.model.asBookEntity
import com.example.bookshelf.bussiness.networkdata.FirestoreBookDataSource
import com.example.bookshelf.util.NetworkStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BookListRepository(
    private val firestoreBookDataSource: FirestoreBookDataSource,
    private val db: BookDatabase,
    private val connMgr: ConnectivityManager
) {
    var fireDb = Firebase.firestore
    var user = FirebaseAuth.getInstance().currentUser
    private val isConnected = NetworkStatus.isConnected(connMgr)
    private suspend fun getBookList() =
        firestoreBookDataSource
            .getBooksFromFirestore()

    fun getOfflineBooks() =
        db.bookDao().getBooks()

    fun getRecentBooks() = db.bookDao().getRecent()


    suspend fun filterByCategory(category: String) =
        firestoreBookDataSource.filterByCategory(category)

    suspend fun filterByAuthor(author: String) =
        if (isConnected)
            firestoreBookDataSource
                .filterByCategory(author)
        else getOfflineBooks()

    suspend fun refreshBooks(
        callbackOnRefreshFailed: () -> Unit
    ) {
        if (true) {
            var _books: List<Book>
            getBookList().collect {
                if (it.data != null) {
                    _books = it.data as List<Book>
                    clearDatabase()
                    db.bookDao().insertBooks(_books.asBookEntity())
                }
            }
        } else {
            callbackOnRefreshFailed()
        }
    }


    private fun clearDatabase() = db.bookDao().clear()

    suspend fun filterByCategoryOffline(
        category: String
    ) =
        db.bookDao().filterByCategory(category)

    suspend fun filterBooks(query: String) = db.bookDao().queryBooks(query)

    suspend fun sortByAuthor() = db.bookDao().sortByAuthor()

    suspend fun sortByTitle() = db.bookDao().sortByTitle()

    suspend fun sortByPubDate() = db.bookDao().sortByPubDate()

    suspend fun addDownload(
        _book: DownloadEntity
    ) =
        db.downloadDao().addDownload(_book)


}