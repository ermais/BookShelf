package com.example.bookshelf.worker

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.bookshelf.bussiness.db.BookDatabase
import com.example.bookshelf.bussiness.db.DownloadEntity
import com.example.bookshelf.bussiness.repository.book.DownloadRepository
import com.example.bookshelf.data.KEY_BOOK_ID
import com.example.bookshelf.data.KEY_BOOK_TITLE
import com.example.bookshelf.data.KEY_DOWNLOAD_BOOK_URI
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DownloadBookWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    @SuppressLint("NewApi")
    override suspend fun doWork(): Result {
        return suspendCoroutine { continuation ->
            val db = BookDatabase.getDatabase(applicationContext)
            val downloadRepository: DownloadRepository = DownloadRepository(db)
            val downloadBookUri = inputData.getString(KEY_DOWNLOAD_BOOK_URI)
            val bookTitle = inputData.getString(KEY_BOOK_TITLE)
            val bookId = inputData.getString(KEY_BOOK_ID)
            println("doWork book id ----------------------------------${bookId}")
            var localDownloadUri =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            localDownloadUri = File(localDownloadUri.path + "/books")
            if (localDownloadUri.mkdirs()) {
                println("success!")
            }
            val file = File.createTempFile("book", ".pdf", localDownloadUri)

            val downloadRef =
                FirebaseStorage.getInstance().getReferenceFromUrl(downloadBookUri.toString())
            downloadRef.getFile(file).addOnFailureListener {
                continuation.resumeWithException(it)
            }.addOnSuccessListener {
                it.task.continueWithTask(Continuation<FileDownloadTask.TaskSnapshot, Task<Uri>> {
                    if (!it.isSuccessful) {
                        it.exception?.let { it1 -> continuation.resumeWithException(it1) }
                    }

                    return@Continuation downloadRef.downloadUrl
                })
                    .addOnCompleteListener {
                        println("Download Success ---------------------------")
                        println(it.result.toString())
                        val download = DownloadEntity(file.toString(), bookId.toString())
                        CoroutineScope(Dispatchers.IO).launch {
                            downloadRepository.downloadBook(download)
                        }
                        continuation.resume(
                            Result.success(
                                (workDataOf(
                                    KEY_DOWNLOAD_BOOK_URI to file.toString(),
                                    KEY_BOOK_ID to bookId
                                ))
                            )
                        )
                    }
            }
        }
    }
}