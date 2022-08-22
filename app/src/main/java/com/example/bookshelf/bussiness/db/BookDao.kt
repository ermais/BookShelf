package com.example.bookshelf.bussiness.db


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert(onConflict =OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books : List<BookEntity>)

    @Insert
    suspend fun createBook(vararg book:BookEntity)

    @Query("SELECT * FROM books")
    fun getBooks() : Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE category = :query")
    fun filterByCategory(query:String) : Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE authorName = :author")
    fun filterByAuthor(author:String) : Flow<List<BookEntity>>


    @Query("SELECT * FROM books WHERE authorName LIKE :query" +
            " OR title LIKE :query" +
            " OR category LIKE :query"
    )
    fun queryBooks(query:String) : Flow<List<BookEntity>>

    @Update
    suspend fun updateBook(vararg book:BookEntity)


}