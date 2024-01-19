package com.example.bookshelf.bussiness.repository.book

import android.net.ConnectivityManager
import com.example.bookshelf.bussiness.Result.data
import com.example.bookshelf.bussiness.db.BookDatabase
import com.example.bookshelf.bussiness.model.Book
import com.example.bookshelf.bussiness.model.asBookEntity
import com.example.bookshelf.bussiness.networkdata.FirestoreBookDataSource
import com.example.bookshelf.util.NetworkStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TopRatedBookRepository(
    private val firestoreBookDataSource: FirestoreBookDataSource,
    private val db: BookDatabase,
    private val connMgr: ConnectivityManager
) {
    var user = FirebaseAuth.getInstance().currentUser
    private val isConnected = NetworkStatus.isConnected(connMgr)



    private suspend fun getBookList() =
        firestoreBookDataSource
            .getBooksFromFirestore()

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
    fun getTopRated(topRate : Double) = db.bookDao().getTopRated(topRate)
}
