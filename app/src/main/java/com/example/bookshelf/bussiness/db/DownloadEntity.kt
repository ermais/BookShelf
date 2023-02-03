package com.example.bookshelf.bussiness.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "downloads",
    indices = [Index(value = ["downloadBookId"], unique = true)],
)
data class DownloadEntity(
    val bookUri: String,
    @ColumnInfo()
    val downloadBookId: String,
    val downloadDate: Long = Date().time
) {
    @PrimaryKey(autoGenerate = true)
    var downloadId: Int = 0
}

