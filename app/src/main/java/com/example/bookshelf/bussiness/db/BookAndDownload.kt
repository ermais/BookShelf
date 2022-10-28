package com.example.bookshelf.bussiness.db

import androidx.room.Embedded
import androidx.room.Relation

data class BookAndDownload(
    @Embedded val book : BookEntity,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "bookId"
    )
    val download: DownloadEntity
)