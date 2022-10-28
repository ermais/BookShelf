package com.example.bookshelf.bussiness.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "downloads")
data class DownloadEntity(
val bookByteContent : Byte,
val bookId : Int
){
    @PrimaryKey(autoGenerate = true)
    var downloadId : Int=0
}