package com.example.observationdatabase

import android.app.Application
import android.content.Context
import android.support.test.InstrumentationRegistry
import android.util.Log
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import java.sql.Date

class ObservationRepositoryTest {
private var cnt = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application




    @Test
    fun testInsert() {
        var repo = ObservationRepository(cnt)
        val dateTime = Date(System.currentTimeMillis())
           var observation = ObservationEntity(
               id = 0,
               species = "Thrush",
               rarity = "common",
               notes = "Was seen in the morning",
               timestamp = dateTime.time,
               latitude = "60,2205",
               longitude = "20,5542",
               imageUri = ""
           )
        repo.insertObservation(observation)

        var observations:List<ObservationEntity> = repo.getObservations("newest")
        assert(observations.size >= 0)
        assert(observations.contains(observation))
        repo.emptyDatabase()

    }
@Test
fun testOrdering(){
    var repo = ObservationRepository(cnt)


    for(i in 0..4){
        var observation = ObservationEntity(
            id = 0,
            species = "Thrush",
            rarity = "common",
            notes = "Was seen in the morning",
            timestamp = Date(System.currentTimeMillis()).time,
            latitude = "60,2205",
            longitude = "20,5542",
            imageUri = ""
        )
        repo.insertObservation(observation)
        Thread.sleep(1000)

    }


    var observations_newest:List<ObservationEntity> = repo.getObservations("newest")

    var observations_oldest:List<ObservationEntity> = repo.getObservations("oldest")


    for(i in 1..observations_newest.size - 1){
        var date = Date(observations_newest[i - 1].timestamp as Long)
        var date2 = Date(observations_newest[i].timestamp as Long)
        assertTrue(date.compareTo(date2) > 0)
    }
    for(i in 1..observations_oldest.size - 1){
        var date = Date(observations_oldest[i - 1].timestamp as Long)
        var date2 = Date(observations_oldest[i].timestamp as Long)
        assertTrue(date.compareTo(date2) < 0)
    }




    repo.emptyDatabase()


}

    @Test
    fun testDelete(){
        var repo = ObservationRepository(cnt)
        for(i in 0..4){

            var observation = ObservationEntity(
                id = 0,
                species = "Thrush",
                rarity = "common",
                notes = "Was seen in the morning",
                timestamp = Date(System.currentTimeMillis()).time,
                latitude = "60,2205",
                longitude = "20,5542",
                imageUri = ""
            )
            repo.insertObservation(observation)
            Thread.sleep(1000)
        }

        var testlist= repo.getObservations("newest")

 for (item in testlist){
     repo.deleteObservation(item)
     Thread.sleep(1000)
 }
        var observation:List<ObservationEntity> = repo.getObservations("newest")
        assertEquals(0, observation.size)

        repo.emptyDatabase()

    }


}