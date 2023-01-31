package com.example.bookshelf.bussiness.db

import androidx.room.Embedded
import androidx.room.Relation
//data class BookAndDownloads(
//    @Embedded val book: BookEntity,
//    @Relation(
//        parentColumn = "bookId",
//        entity = DownloadEntity::class,
//        entityColumn = "downloadBookId"
//    )
//    val download: DownloadEntity
//)

data class DownloadAndBook(
    val title : String,
    val desc  : String?,
    val bookId : String,
    val downloadDate : Long,
    val bookUri : String
)