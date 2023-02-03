package com.example.bookshelf.bussiness.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MyBooksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMyBook(myBook: MyBooksEntity)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMyBooks(myBooks: List<MyBooksEntity>)

    @Update
    suspend fun update(myBook: MyBooksEntity)

    @Delete
    suspend fun delete(book: MyBooksEntity)

    @Query(
        "SELECT user, mybooks.bookId AS bookId, boughtDate, title FROM mybooks " +
                "INNER JOIN books ON mybooks.bookId = books.bookId"
    )
    fun getMyBooks(): Flow<List<MyBooksQuery>>

    @Query("DELETE FROM mybooks")
    fun clear()
}