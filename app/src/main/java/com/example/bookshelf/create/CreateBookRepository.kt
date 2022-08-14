package com.example.bookshelf.create

import android.net.Uri
import com.example.bookshelf.model.book.Book
import com.example.bookshelf.model.book.FirestoreBookDataSource
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CreateBookRepository(val firestoreBookDataSource: FirestoreBookDataSource){
    suspend fun publishBook(book:Book) = firestoreBookDataSource.publishBook(book)
}































//
//class CreateBookRepository {
//    private val fireDb = Firebase.firestore
//    private val user = FirebaseAuth.getInstance().currentUser
//    private val storage = FirebaseStorage.getInstance()
//    private var bookInstance: Book? = null
//
//
//    suspend fun createBook(book: Book, uri: Uri?): Book{
////        val _book = book.toMap()
//
////        return fireDb.collection("books")
////           .document(book.title).set(_book)
//
//        return suspendCoroutine { continuation ->
//            val _book = book.toMap()
//            fireDb.collection("books")
//                .document(book.title!!).set(_book)
//                .addOnFailureListener{
//                    continuation.resumeWithException(it)
//                }
////                .addOnSuccessListener {
////                    continuation.resumeWith(Result.success(book))
////                }
//                .addOnCompleteListener{
//                    if (it.isSuccessful){
//                        continuation.resumeWith(Result.success(book))
//                    }
//                }
//        }
//    }
//
//    suspend fun updateBookCover(uri:Uri?,book:Book): Uri?{
//        return suspendCoroutine { continuation ->
//            println("Update Book Cover Uri .......................................................")
//            println("Book : ${book}")
//            println("Uri : ${uri}")
//            val data = mapOf("bookCover" to uri.toString())
//            fireDb.collection("books")
//                .document(book.title!!).update(data)
//                .addOnFailureListener{
//                    continuation.resumeWithException(it)
//                }
////                .addOnSuccessListener {
////                    continuation.resumeWith(Result.success(uri))
////                }
//                .addOnCompleteListener{task->
//                    if (!task.isSuccessful){
//                        task.exception?.let { exception ->
//                            continuation.resumeWithException(exception)
//                        }
//                    }
//                    continuation.resumeWith(Result.success(uri))
//                }
//        }
//    }
//
//    suspend fun uploadBookCover(book:Book, uri:Uri?)  : Uri? {
//                    return suspendCoroutine {continuation->
//                        val storageRef = storage.reference
//                        var imageRef = storageRef.child("BookCovers/${book!!.title}/cover")
//                        val uploadTask = imageRef.putFile(uri as Uri)
//                        uploadTask.addOnFailureListener{
//                            continuation.resumeWithException(it)
//                        }.addOnSuccessListener {
//                            it.task.continueWithTask(Continuation<UploadTask.TaskSnapshot,Task<Uri>>{task->
//                               if(!task.isSuccessful){
//                                   task.exception?.let { exception ->
//                                       continuation.resumeWithException(exception)
//                                   }
//                               }
//                               return@Continuation imageRef.downloadUrl
//                            }).addOnCompleteListener{uriTask->
//                                if (uriTask.isSuccessful){
//                                    val downloadUri = uriTask.result
//                                    continuation.resumeWith(Result.success(downloadUri))
//                                }
//                            }
//                        }
//                    }
//
//        }
//}
//
