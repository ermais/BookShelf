package com.example.bookshelf.bussiness.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bookshelf.bussiness.model.Book
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable


@Entity(tableName = "books")
data class BookEntity(

    @PrimaryKey
    val bookId: String = "",
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "category", defaultValue = "category")
    val category: String,
    @ColumnInfo(name = "authorName")
    val authorName: String,
    @ColumnInfo(name = "authorUID")
    val authorUID: String,
    @ColumnInfo(name = "book_cover_uri")
    @Nullable
    val bookCoverUri: String?,
    @ColumnInfo(name = "book_doc_uri")
    @NotNull
    val bookDocUri: String,
    @ColumnInfo(name = "pub_date")
    val pubDate: Long = 0,
    @ColumnInfo(name = "rating")
    val rating: String,
    @ColumnInfo(name = "download_count", defaultValue = "0")
    val downloadCount: Int
) {
    fun asDomainModel(): Book {
        return Book(
            bookId,
            authorUID,
            title,
            authorName,
            category,
            description,
            pubDate,
            bookCoverUri.toString(),
            bookDocUri,
            rating,
            downloadCount,
        )
    }


}

//fun BookEntity.asDomainModel() : Book{
//    return Book(this.authorUID,
//        this.title,
//        this.authorName,
//        this.category,
//        this.description,
//        this.pubDate,
//        this.bookCoverUri.toString(),
//        this.bookDocUri,
//        this.rating,
//        this.downloadCount
//    )
//}


fun List<BookEntity>.asDomainModel(): List<Book> {
    return this.map { book ->
        Book(
            book.bookId,
            book.authorUID,
            book.title,
            book.authorName,
            book.category,
            book.description,
            book.pubDate,
            book.bookCoverUri.toString(),
            book.bookDocUri,
            book.rating,
            book.downloadCount
        )
    }
}