package com.example.bookshelf.bussiness.repository.book

import android.net.ConnectivityManager
import com.example.bookshelf.bussiness.db.BookDatabase
import com.example.bookshelf.bussiness.networkdata.FirestoreBookDataSource
import com.example.bookshelf.util.NetworkStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RecentBookRepository(
    private val firestoreBookDataSource: FirestoreBookDataSource,
    private val db: BookDatabase,
    private val connMgr: ConnectivityManager
) {
    var fireDb = Firebase.firestore
    var user = FirebaseAuth.getInstance().currentUser
    private val isConnected = NetworkStatus.isConnected(connMgr)


}
