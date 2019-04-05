package com.example.observationdatabase

import android.app.Application

import org.junit.Test

import org.junit.Assert.*
import java.sql.Date

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class DatabaseUnitTest{
    

    @Test
    fun verifyInsertion(){
        var repo = ObservationRepository(Application.)
        val dateTime = Date(System.currentTimeMillis())
        var observation = ObservationEntity(id=0,species = "Hawk",rarity="common",notes="TESTING"
            ,date = dateTime, timestamp = dateTime.time ,latitude = "60.43256",longitude = "22.543256")
        repo.insertObservation(observation)
        assertEquals(1, repo.getObservations("asc").size)
    }

    @Test
    fun verifyDelete(){
        var repo = ObservationRepository(Application())
        val dateTime = Date(System.currentTimeMillis())
        var observation = ObservationEntity(id=0,species = "Hawk",rarity="common",notes="TESTING"
            ,date = dateTime, timestamp = dateTime.time ,latitude = "60.43256",longitude = "22.543256")
        repo.insertObservation(observation)
        assertEquals(1, repo.getObservations("asc").size)
        repo.deleteObservation(observation)
        assertEquals(0, repo.getObservations("asc").size)
    }

    @Test
    fun verifySorting(){
        var repo = ObservationRepository(Application())
        val dateTime = Date(System.currentTimeMillis())
        var observation = ObservationEntity(id=0,species = "Hawk",rarity="common",notes="TESTING"
            ,date = dateTime, timestamp = dateTime.time ,latitude = "60.43256",longitude = "22.543256")
        var observation1 = ObservationEntity(id=0,species = "Hawk",rarity="common",notes="TESTING"
            ,date = dateTime, timestamp = dateTime.time + 1 ,latitude = "60.43256",longitude = "22.543256")
        var observation2 = ObservationEntity(id=0,species = "Hawk",rarity="common",notes="TESTING"
            ,date = dateTime, timestamp = dateTime.time + 2 ,latitude = "60.43256",longitude = "22.543256")
        var observation3 = ObservationEntity(id=0,species = "Hawk",rarity="common",notes="TESTING"
            ,date = dateTime, timestamp = dateTime.time + 3 ,latitude = "60.43256",longitude = "22.543256")

        var testObservationsAsc:List<ObservationEntity> = listOf(observation3,observation2,observation1,observation)
        var testObservationsDesc:List<ObservationEntity> = listOf(observation,observation1,observation2,observation3)
        repo.insertObservation(observation)
        repo.insertObservation(observation1)
        repo.insertObservation(observation2)
        repo.insertObservation(observation3)

        var observationsAsc:List<ObservationEntity> = repo.getObservations("asc")
        var observationsDesc:List<ObservationEntity> = repo.getObservations("desc")
        // Ascendign sorting
        assertEquals(testObservationsAsc, observationsAsc)
        // Descending sorting
        assertEquals(testObservationsDesc, observationsDesc)

    }


}
