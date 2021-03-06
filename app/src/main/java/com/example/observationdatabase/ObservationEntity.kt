package com.example.observationdatabase

import android.arch.persistence.room.*


// Define the table and the attributes used in the database
@Entity

data class ObservationEntity(
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    var species: String?,
    var rarity: String?,
    var notes: String?,
    var timestamp: Long?,
    var latitude: String?,
    var longitude: String?,
    var imageUri: String?

)