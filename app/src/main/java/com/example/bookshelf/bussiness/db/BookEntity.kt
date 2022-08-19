package com.example.bookshelf.bussiness.db

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.example.bookshelf.bussiness.model.Book
import java.util.*


@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val bookId : Int,
    @ColumnInfo(name="title")
    val title : String,
    @ColumnInfo(name="description")
    val description: String?,
    @ColumnInfo(name="category", defaultValue = "category")
    val category : String,
    @ColumnInfo(name="authorName")
    val authorName: String,
    @ColumnInfo(name="authorUID")
    val authorUID : String,
    @ColumnInfo(name = "book_cover_uri")
    val bookCoverUri : String?,
    @ColumnInfo(name="book_doc_uri")
    val bookDocUri : String,
    @ColumnInfo(name="pub_date")
    val pubDate : Date,
    @ColumnInfo(name="rating", defaultValue = "0.0")
    val rating : String?,
    @ColumnInfo(name="download_count", defaultValue = "0")
    val downloadCount : Int?
    ){
    fun asDomainModel():Book{
        return Book(
            authorUID,
            title,
            authorName,
            category,
            description,
            pubDate,
            bookCoverUri,
            bookDocUri,
            rating)
    }

    fun List<BookEntity>.asDomainModel() : List<Book>{
        return this.map { book->Book(book.authorUID,
            book.title,
            book.authorName,
            book.category,
            book.description,
            book.pubDate,
            book.bookCoverUri,
            book.bookDocUri,
            book.rating
            ) }
    }
}