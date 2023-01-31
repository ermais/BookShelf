package com.example.bookshelf.bussiness.db

import androidx.room.*
import com.google.android.material.circularreveal.CircularRevealHelper.Strategy
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addDownload(download : DownloadEntity)

    @Delete
    suspend fun deleteDownload(download : DownloadEntity)

    @Update
    suspend fun updateDownload(download : DownloadEntity)

    @Query("SELECT * FROM downloads")
    fun getDownloads():Flow<List<DownloadEntity>>

    @Query("SELECT title, description, bookId,downloads.bookUri AS bookUri,downloads.downloadDate as downloadDate FROM downloads " +
            "INNER JOIN books ON downloads.downloadBookId = books.bookId")
    fun getAllDownloads(): Flow<List<DownloadAndBook>>

}