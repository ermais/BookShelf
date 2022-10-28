package com.example.bookshelf.worker

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.bookshelf.bussiness.Result.Result.Success
import com.example.bookshelf.data.KEY_BOOK_TITLE
import com.example.bookshelf.data.KEY_DOWNLOAD_BOOK_URI
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.io.path.exists

class DownloadBookWorker(ctx: Context, params:WorkerParameters) : CoroutineWorker(ctx,params) {
    @SuppressLint("NewApi")
    override suspend fun doWork(): Result {
        return suspendCoroutine { continuation ->
            val downloadBookUri = inputData.getString(KEY_DOWNLOAD_BOOK_URI)
            val bookTitle = inputData.getString(KEY_BOOK_TITLE)
            var localDownloadUri = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            localDownloadUri = File(localDownloadUri.path + "/books")
            if (localDownloadUri.mkdirs()){
                println("success!")
            }
            val file = File.createTempFile("images",".jpg",localDownloadUri)

            val downloadRef = FirebaseStorage.getInstance().getReferenceFromUrl(downloadBookUri.toString())
            downloadRef.getFile(file).addOnFailureListener{
                continuation.resumeWithException(it)
            }.addOnSuccessListener {
                it.task.continueWithTask(Continuation<FileDownloadTask.TaskSnapshot,Task<Uri>> {
                    if(!it.isSuccessful){
                        it.exception?.let { it1 -> continuation.resumeWithException(it1) }
                    }

                    return@Continuation downloadRef.downloadUrl
                })
                    .addOnCompleteListener {
                        println("Download Success ---------------------------")
                        println(it.result.toString())
                        continuation.resume(Result.success((workDataOf(KEY_DOWNLOAD_BOOK_URI to localDownloadUri.toString()))))
                    }
            }
        }
    }
}