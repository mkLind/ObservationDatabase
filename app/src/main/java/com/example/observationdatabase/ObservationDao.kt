package com.example.observationdatabase

import android.arch.persistence.room.*

@Dao
interface ObservationDao {
    @Query("SELECT * FROM ObservationEntity")
    fun loadAllObservations():List<ObservationEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertObservation(observation: ObservationEntity)
    @Delete
    fun deleteObservation(observation: ObservationEntity)

}