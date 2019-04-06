package com.example.observationdatabase

import android.arch.persistence.room.*
import android.graphics.Bitmap
import java.sql.Date



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
    var longitude: String?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var image:Bitmap? = null

)