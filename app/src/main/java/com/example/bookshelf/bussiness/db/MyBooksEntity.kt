package com.example.bookshelf.bussiness.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "mybooks")
data class MyBooksEntity(
    val user: String,
    val bookId: String,
    val boughtDate: Long = Date().time
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    fun toMap(): Map<String, Any> {
        return mapOf(
            "user" to user,
            "bookId" to bookId,
            "boughtDate" to boughtDate,
            "id" to id
        )
    }
}

data class MyBooksQuery(
    val user: String,
    val bookId: Int,
    val boughtDate: Long,
    val title: String,
)