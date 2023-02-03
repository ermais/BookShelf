package com.example.bookshelf.worker

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result.*
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.bookshelf.data.KEY_BOOK_COVER_URI
import com.example.bookshelf.data.KEY_BOOK_TITLE
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UploadBookCover(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result {

        return suspendCoroutine { continuation ->
            var downloadUri: Uri? = null
            val bookUriString = inputData.getString(KEY_BOOK_COVER_URI)
            val bookTitle = inputData.getString(KEY_BOOK_TITLE)
            val bookUri = Uri.parse(bookUriString)
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("BookCover/${bookTitle}/cover")
            val uploadTask = imageRef.putFile(bookUri as Uri)
            uploadTask.addOnFailureListener {
                continuation.resumeWithException(it)
            }.addOnSuccessListener {
                it.task.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!it.task.isSuccessful) {
                        it.task.exception?.let { e -> continuation.resumeWithException(e) }
                    }
                    return@Continuation imageRef.downloadUrl
                }).addOnCompleteListener {
                    if (it.isSuccessful) {
                        downloadUri = it.result
                    }
                    continuation.resume(success(workDataOf(KEY_BOOK_COVER_URI to downloadUri.toString())))
                }
            }

        }

    }
}