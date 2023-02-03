package com.example.bookshelf.bussiness.db


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Insert
    suspend fun createBook(vararg book: BookEntity)

    @Query("SELECT * FROM books")
    fun getBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE category = :query")
    fun filterByCategory(query: String): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE authorName = :author")
    fun filterByAuthor(author: String): Flow<List<BookEntity>>


    @Query(
        "SELECT * FROM books WHERE authorName LIKE '%'||:query ||'%'" +
                " OR title  LIKE '%' || :query || '%'" +
                " OR category LIKE '%' || :query || '%'"
    )
    fun queryBooks(query: String): Flow<List<BookEntity>>

    @Update
    suspend fun updateBook(vararg book: BookEntity)

    @Query("DELETE  FROM books")
    fun clear()

    @Query("SELECT * FROM books ORDER BY authorName ASC")
    fun sortByAuthor(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books ORDER BY title ASC")
    fun sortByTitle(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books ORDER BY pub_date ASC")
    fun sortByPubDate(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE title = :title")
    fun getBook(title: String): Flow<BookEntity>

//
//    @Transaction
//    @Query("SELECT * FROM books")
//    fun getAllDownloads(): Flow<List<BookAndDownloads>>

}