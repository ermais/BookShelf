package com.example.bookshelf.bussiness.repository.book

import com.example.bookshelf.bussiness.db.BookDatabase
import com.example.bookshelf.bussiness.db.DownloadDao
import com.example.bookshelf.bussiness.db.DownloadEntity

class DownloadRepository(private val db: BookDatabase,) {

    suspend fun downloadBook(_download : DownloadEntity) = db.downloadDao().addDownload(_download)

    suspend fun deleteDownloadBook(_download: DownloadEntity) = db.downloadDao().deleteDownload(_download)

    suspend fun updateDownloadBook(_download: DownloadEntity) = db.downloadDao().updateDownload(_download)

     fun getDownloadsAndBooks() = db.downloadDao().getAllDownloads()

    fun getDownloads() = db.downloadDao().getDownloads()
}