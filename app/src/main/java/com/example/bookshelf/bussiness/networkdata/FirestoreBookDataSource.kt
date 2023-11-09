package com.example.bookshelf.bussiness.networkdata

import android.net.Uri
import android.util.Log
import com.example.bookshelf.bussiness.Result.Result
import com.example.bookshelf.bussiness.Result.Result.Loading
import com.example.bookshelf.bussiness.Result.Result.Success
import com.example.bookshelf.bussiness.model.Book
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirestoreBookDataSource(db: FirebaseFirestore, cloudStorage: FirebaseStorage) :
    BookDataSource {
    private val cloudRef = cloudStorage.reference
    private val bookRef = db.collection("books")
    override suspend fun publishBook(book: Book): Flow<Result<Void>> {
        val flow = flow {
            try {
                emit(Loading)
                val bookId = bookRef.document().id
                val bookAdded = bookRef.
                document(bookId).
                set(book.
                toMap(bookId))
                    .await()
                emit(Success(bookAdded))
            } catch (e: Exception) {
                emit(Result.Failure(e.message ?: e.toString()))
            }
        }
        return flow
    }

    override suspend fun getBooksFromFirestore() = callbackFlow {
        val snapShot = bookRef.orderBy("title").addSnapshotListener { snapshot, e ->
            val response = if (snapshot != null) {
                Log.d("FireStore", snapshot.joinToString("||||"))
                val books = snapshot.toObjects(Book::class.java)
                Success(books)
            } else {
                Result.Failure(e?.message ?: e.toString())
            }
            trySend(response).isSuccess
        }
        awaitClose {
            snapShot.remove()
        }
    }

//    override suspend fun updateBook(book: Book) = flow {
//        try {
//            emit(Result.Loading)
//            val bookData = book.toMap()
//            val updated = bookRef.document("${book.title}")
//                .update(bookData).await()
//            emit(Result.Success(updated))
//        }catch (e : Exception){
//            Result.Failure(e.message ?: e.toString())
//        }
//    }

    override suspend fun deleteBook(bookId: String) = flow {
        try {
            emit(Loading)
            val deleted = bookRef.document(bookId).delete().await()
            emit(Success(deleted))
        } catch (e: Exception) {
            Result.Failure(e.message ?: e.toString())
        }
    }

    override suspend fun filterByCategory(category: String) = callbackFlow {
        val snapShot = bookRef.orderBy("title").whereEqualTo("category", category)
            .addSnapshotListener { snapshot, e ->
                val response = if (snapshot != null) {
                    val books = snapshot.toObjects(Book::class.java)
                    Success(books)
                } else {
                    Result.Failure(e?.message ?: e.toString())
                }
                trySend(response)
            }
        awaitClose {
            snapShot.remove()
        }
    }

    override suspend fun filterByAuthor(author: String) = callbackFlow {
        val snapShot = bookRef.orderBy("author").whereEqualTo("author", author)
            .addSnapshotListener { snapshot, e ->
                val response = if (snapshot != null) {
                    Success(snapshot.toObjects(Book::class.java))
                } else {
                    Result.Failure(e?.message ?: e.toString())
                }
                trySend(response)
            }
        awaitClose {
            snapShot.remove()
        }
    }

    override suspend fun queryBooks(query: String) = callbackFlow {
        val snapshot = bookRef.addSnapshotListener { snapshot, e ->
            val response = if (snapshot != null) {
                var books = snapshot.toObjects(Book::class.java)
                books = books.filter { book -> book.title == query || book.authorName == query }
                books = books as MutableList<Book>
                Success(books)

            } else {
                Result.Failure(e?.message ?: e.toString())
            }
            trySend(response)
        }
        awaitClose {
            snapshot.remove()
        }
    }


    override suspend fun updateBookDoc(
        bookUID: String,
        bookTitle: String,
        bookUriFromFile: Uri
    ): Flow<Result<Void>> {
        return flow {
            try {
                val bookDocUpdated = bookRef.document(bookUID)
                    .update(mapOf("bookDocCover" to bookUriFromFile))
                    .await()
                emit(Success(bookDocUpdated))
            } catch (e: Exception) {
                emit(Result.Failure(e.message ?: e.toString()))
            }

        }
    }

    override suspend fun updateBookCover(
        bookUID: String,
        bookTitle: String,
        bookCoverUriFromFile: String
    ): Flow<Result<Void>> {
        return flow {
            try {
                emit(Loading)
                val bookCoverUpdated = bookRef.document(bookUID)
                    .update(mapOf("bookCover" to bookCoverUriFromFile))
                    .await()
                emit(Success(bookCoverUpdated))
            } catch (e: Exception) {
                emit(Result.Failure(e.message ?: e.toString()))
            }
        }
    }

    override suspend fun rateBook(rating: String, bookUID: String): Flow<Result<Void>> {
        return flow {
            try {
                emit(Loading)
                val bookRated = bookRef.document(bookUID)
                    .update(mapOf("rating" to rating))
                    .await()
                emit(Success(bookRated))
            } catch (e: Exception) {
                Result.Failure(e.message ?: e.toString())
            }
        }
    }

    override suspend fun sortByDate(): Flow<Result<List<Book>>> {
        return getBooksFromFirestore()
    }

    override suspend fun sortByBookTitle(): Flow<Result<List<Book>>> {
        return getBooksFromFirestore()
    }

    override suspend fun uploadBookDoc(uriFromFile: Uri, bookTitle: String) =
        callbackFlow {
            Loading
            var downloadUri: Uri? = null
            val imageRef = cloudRef.child("Books/${bookTitle}/document")
            val uploadTask = imageRef.putFile(uriFromFile)
            val loading = uploadTask.continueWithTask {
                imageRef.downloadUrl
            }.addOnSuccessListener {
                downloadUri = it
                trySend(Success(it))
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    Success(it.result)
                } else {
                    Result.Failure(it.exception?.message.toString())
                }
            }.addOnFailureListener {
                Result.Failure(it.message ?: it.toString())
            }
            awaitClose { loading.result }
        }

    override suspend fun uploadBookCover(uriFromFile: Uri, bookTitle: String): Flow<Result<Uri>> {
        return callbackFlow {
            Loading
            var downloadUri: Uri? = null
            val imageRef = cloudRef.child("BookCover/${bookTitle}/cover")
            val uploadTask = imageRef.putFile(uriFromFile)
            val loading = uploadTask.continueWithTask {
                imageRef.downloadUrl
            }.addOnSuccessListener {
                downloadUri = it
                trySend(Success(it))
            }.addOnFailureListener {
                Result.Failure(it.message ?: it.toString())
            }
            awaitClose {
                loading.result
            }
        }
    }
}