package com.example.bookshelf.bussiness.networkdata

import com.example.bookshelf.bussiness.Result.Result
import com.example.bookshelf.bussiness.db.MyBooksEntity
import com.example.bookshelf.bussiness.model.MyBooks
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirestoreMyBooksDataSource(firestore: FirebaseFirestore) : MyBooksDataSource {
    val myBooksRef = firestore.collection("my-books")
    override suspend fun buyBook(book: MyBooksEntity) = flow {
        try {
            emit(Result.Loading)
            val bookId = myBooksRef.document().id
            val myBook = myBooksRef.document(bookId)
                .set(book.toMap()).await()
            emit(Result.Success(myBook))
        } catch (e: Exception) {
            emit(Result.Failure(e.message ?: e.toString()))
        }
    }

    override suspend fun getMyBooks(user: String) = callbackFlow {
        val snapshot = myBooksRef.orderBy("user")
            .addSnapshotListener { snapshot, e ->
                val response = if (snapshot != null) {
                    val myBooks = snapshot.toObjects(MyBooks::class.java)
                    Result.Success(myBooks)
                } else {
                    Result.Failure(e?.message ?: e.toString())
                }
                trySend(response).isSuccess
            }
        awaitClose {
            snapshot.remove()
        }
    }
}