package com.example.bookshelf.bussiness.db

import androidx.room.*
import java.util.Date

@Entity(tableName = "downloads",
    indices = [Index(value = ["downloadBookId"], unique = true)],
)
data class DownloadEntity(
val bookUri : String,
@ColumnInfo()
val downloadBookId: Int,
val downloadDate : Long=Date().time
){
    @PrimaryKey(autoGenerate = true)
    var downloadId : Int=0
}

