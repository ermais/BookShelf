package com.example.bookshelf.bussiness.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.bookshelf.data.DATABASE_NAME

@Database(entities = [BookEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun bookDao() : BookDao

    companion object {
        @Volatile private var instance : AppDatabase? = null

        fun getDatabase(context:Context): AppDatabase{
            return instance?: synchronized(this){
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context:Context): AppDatabase{
            return Room.databaseBuilder(context,AppDatabase::class.java, DATABASE_NAME)
                .addTypeConverter(Converters::class.java)
                .fallbackToDestructiveMigration()
                .addCallback(callback = object : RoomDatabase.Callback(){
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                    }
                })
                .build()
        }
    }
}