package com.example.bookshelf.bussiness.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.bookshelf.bussiness.model.Book
import com.example.bookshelf.data.DATABASE_NAME

@Database(entities = [BookEntity::class,DownloadEntity::class], version = 21, exportSchema = false)
@TypeConverters()
abstract class BookDatabase : RoomDatabase(){
    abstract fun bookDao() : BookDao
    abstract fun downloadDao() : DownloadDao

    companion object {
        @Volatile private var instance : BookDatabase? = null

        fun getDatabase(context: Context): BookDatabase{
            return instance?: synchronized(BookDatabase::class.java){
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context:Context): BookDatabase{
            return Room.databaseBuilder(context,BookDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}