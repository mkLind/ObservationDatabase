package com.example.observationdatabase

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverter
import android.arch.persistence.room.TypeConverters
import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDateTime


@Entity

data class ObservationEntity(
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    var species: String?,
    var rarity: String?,
    var notes: String?,
    var date: Date?,
    var timestamp: Long?,
    var latitude: String?,
    var longitude: String?

)