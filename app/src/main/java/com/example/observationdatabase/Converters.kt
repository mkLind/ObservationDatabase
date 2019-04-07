package com.example.observationdatabase

import android.arch.persistence.room.TypeConverter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.sql.Date

class Converters {
    @TypeConverter
    fun toDate(value:Long?): Date? {
        return if(value == null) null else Date(value)
        }
    @TypeConverter
    fun fromDate(date:Date?):Long?{
        return if(date == null) null else date.time
    }


}
