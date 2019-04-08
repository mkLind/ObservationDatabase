package com.example.observationdatabase

import android.arch.persistence.room.*
// Database access object. define operations for use with the database
@Dao
interface ObservationDao {
    @Query("SELECT * FROM ObservationEntity")
    fun loadAllObservations():List<ObservationEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertObservation(observation: ObservationEntity)
    @Delete
    fun deleteObservation(observation: ObservationEntity)

}