package com.example.bookshelf.model.book
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*


@IgnoreExtraProperties
data class Book(
    val authorUID: String?="",
    val title: String?="",
    val authorName: String?="",
    val category:String?="",
    val desc: String?="",
    val pubDate: Date?=Date(),
    val bookCover: String?="",
    val bookUri : String? = "",
    val rating: String? ="4.5"
){

    constructor() : this(
        authorUID="",
        title="",
        authorName = "",
        category = "",
        desc = "",
        pubDate = Date(),
        bookCover = "",
        bookUri =""
        ,rating="4.5") {

    }
    override fun toString(): String {
        return "$title by $authorName"
    }
    @Exclude
    fun toMap() : Map<String,Any?>{
        return mapOf(
            "authorUID" to authorUID,
            "title" to title,
            "authorName" to authorName,
            "category" to category,
            "desc" to desc,
            "pubDate" to pubDate,
            "bookCover" to bookCover,
            "bookUri" to bookUri,
            "rating" to rating

        )
    }
}