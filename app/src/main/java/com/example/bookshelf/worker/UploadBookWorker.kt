package com.example.bookshelf.worker

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.bookshelf.data.KEY_BOOK_COVER_URI
import com.example.bookshelf.data.KEY_BOOK_TITLE
import com.example.bookshelf.data.KEY_BOOK_URI
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.google.android.gms.tasks.Continuation
import com.google.firebase.database.Transaction
import kotlin.coroutines.resume

class UploadBookWorker(ctx: Context, params:WorkerParameters) : CoroutineWorker(ctx,params) {
    @SuppressLint("RestrictedApi")
    override suspend  fun doWork(): Result {

        return suspendCoroutine { continuation ->
            var downloadUri: Uri? = null
            var bookUriString = inputData.getString(KEY_BOOK_URI) ?: ""
            println("Logging, Uri from file-------------------------------------")
            println(bookUriString)
            val bookUri = Uri.parse(bookUriString)
            val bookTitle = inputData.getString(KEY_BOOK_TITLE)
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("BookDoc/${bookTitle}/bookUri")
            val uploadTask = imageRef.putFile(bookUri as Uri)

            uploadTask
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }.addOnSuccessListener {
                    it.task.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let { it1 -> continuation.resumeWithException(it1) }
                        }
                        return@Continuation imageRef.downloadUrl
                    }).addOnCompleteListener {
                        if (it.isSuccessful) {
                            downloadUri = it.result
                        }
                        continuation.resume(Result.success(workDataOf(KEY_BOOK_URI to downloadUri.toString())))
                    }
                }

        }
    }
}