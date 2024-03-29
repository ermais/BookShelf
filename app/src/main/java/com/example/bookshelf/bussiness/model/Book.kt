package com.example.bookshelf.bussiness.model

import com.example.bookshelf.bussiness.db.BookEntity
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class Book(
    val bookId: String = "",
    val authorUID: String = "",
    val title: String = "",
    val authorName: String = "",
    val category: String = "",
    val desc: String? = "",
    val pubDate: Long = 0L,
    val bookCover: String? = "",
    val bookUri: String = "",
    val rating: Double = 4.5,
    val downloadCount: Int = 0
) {

    constructor() : this(
        authorUID = "",
        title = "",
        authorName = "",
        category = "",
        desc = "",
        pubDate = 0L,
        bookCover = "",
        bookUri = "", rating = 4.5,
        downloadCount = 0
    ) {

    }

    override fun toString(): String {
        return "$title by $authorName"
    }

    @Exclude
    fun toMap(firebaseBookId: String): Map<String, Any?> {
        return mapOf(
            "bookId" to firebaseBookId,
            "authorUID" to authorUID,
            "title" to title,
            "authorName" to authorName,
            "category" to category,
            "desc" to desc,
            "pubDate" to pubDate,
            "bookCover" to bookCover,
            "bookUri" to bookUri,
            "rating" to rating,
            "downloadCount" to downloadCount

        )
    }
}

fun List<Book>.asBookEntity(): List<BookEntity> {
    return map { book ->
        BookEntity(
            bookId = book.bookId,
            title = book.title,
            description = book.desc,
            category = book.category,
            authorName = book.authorName,
            authorUID = book.authorUID,
            bookCoverUri = book.bookCover,
            bookDocUri = book.bookUri,
            pubDate = book.pubDate,
            rating = book.rating,
            downloadCount = book.downloadCount
        )
    }
}
