package com.example.bookshelf.bussiness.db

import androidx.room.TypeConverter
import java.sql.Date
import java.util.*

class Converters {
    @TypeConverter
    fun datestampToCalender(value:Long) : Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = value
        }
    }

    @TypeConverter
    fun calenderToDatestamp(calendar:Calendar) : Long? {
        return calendar.timeInMillis
    }

}