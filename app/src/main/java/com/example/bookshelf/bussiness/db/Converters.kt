package com.example.bookshelf.bussiness.db

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import java.sql.Date
import java.util.*


class Converters {
    @TypeConverter
    fun datestampToCalender(value: Long): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = value
        }
    }

    @TypeConverter
    fun calenderToDatestamp(calendar: Calendar): Long? {
        return calendar.timeInMillis
    }

    @TypeConverter
    fun timestampToCalender(timestamp: Timestamp): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp.toDate().time
        }
    }

    @TypeConverter
    fun calenderToStamp(calender: Calendar): Timestamp {
        return Timestamp(Date(calender.timeInMillis))
    }


}
