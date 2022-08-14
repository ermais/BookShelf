package com.example.bookshelf.model.book

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirestoreBookDataSource(db : FirebaseFirestore) : BookDataSource {
    val bookRef = db.collection("books")
    override suspend fun publishBook(book: Book) = flow {
        try {
            emit(Result.Loading)
            val bookId = bookRef.document().id
            val bookAdded = bookRef.document(bookId).set(book.toMap())
                .await()
            emit(Result.Success(bookAdded))
        }catch (e: Exception){
            emit(Result.Failure(e.message ?: e.toString()))
    }
    }

    override suspend fun getBooksFromFirestore() = callbackFlow {
        val snapShot = bookRef.orderBy("title").addSnapshotListener{snapshot,e->
            val response = if (snapshot != null){
                Log.d("FireStore",snapshot.joinToString("||||"))
                val books = snapshot.toObjects(Book::class.java)
                Result.Success(books)
            } else {
                Result.Failure(e?.message ?: e.toString())
            }
            trySend(response).isSuccess
        }
        awaitClose {
            snapShot.remove()
        }
    }

    override suspend fun updateBook(book: Book) = flow {
        try {
            emit(Result.Loading)
            val bookData = book.toMap()
            val updated = bookRef.document("${book.title}")
                .update(bookData).await()
            emit(Result.Success(updated))
        }catch (e : Exception){
            Result.Failure(e.message ?: e.toString())
        }
    }

    override suspend fun deleteBook(bookId: String) = flow {
        try {
            emit(Result.Loading)
            val deleted = bookRef.document(bookId).delete().await()
            emit(Result.Success(deleted))
        }catch (e: Exception){
            Result.Failure(e?.message ?: e.toString())
        }
    }

    override suspend fun filterByCategory(category: String) = callbackFlow {
        val snapShot = bookRef.orderBy("title").whereEqualTo("category",category)
            .addSnapshotListener{snapshot,e->
                val response = if (snapshot != null){
                    val books = snapshot.toObjects(Book::class.java)
                    Result.Success(books)
                }else {
                    Result.Failure(e?.message ?: e.toString())
                }
                trySend(response)
            }
        awaitClose{
            snapShot.remove()
        }
    }

    override suspend fun filterByAuthor(author: String) = callbackFlow {
        val snapShot = bookRef.orderBy("author").whereEqualTo("author",author)
            .addSnapshotListener{snapshot,e->
                val response = if (snapshot != null){
                    Result.Success(snapshot.toObjects(Book::class.java))
                }else {
                    Result.Failure(e?.message ?: e.toString())
                }
                trySend(response)
            }
        awaitClose{
            snapShot.remove()
        }
    }

    override suspend fun queryBooks(query: String) = callbackFlow {
        val snapshot = bookRef.addSnapshotListener { snapshot, e ->
            val response = if (snapshot != null) {
                var books = snapshot.toObjects(Book::class.java)
                books = books.filter { book -> book.title == query || book.authorName == query }
                books = books as MutableList<Book>
                Result.Success(books)

            } else {
                Result.Failure(e?.message ?: e.toString())
            }
            trySend(response)
        }
        awaitClose {
            snapshot.remove()
        }
    }


    }