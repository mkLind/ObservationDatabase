package com.example.observationdatabase

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import java.sql.Date
// Class for binding database and its operations together
class ObservationRepository(application: Application) {

    private var observationData: List<ObservationEntity>
    private val observationDao: ObservationDao
    private var database:ObservationDatabase
    // Initialize database and fetch stored observations
    init{
        database = ObservationDatabase.getDb(application)
        observationDao = database.observationDao()
        observationData = FetchAsync(observationDao).execute().get()
    }
    fun emptyDatabase(){
        database.clearAllTables()
    }
    // Get observations. Can be ordered so that either the newer or older observations are first
    fun getObservations(sortOrder:String):List<ObservationEntity>{
        if(sortOrder == "oldest") {
            observationData =FetchAsync(observationDao).execute().get().sortedBy({ selector(it) })

        }else if(sortOrder == "newest"){
            observationData = FetchAsync(observationDao).execute().get().sortedBy({ selector(it) }).asReversed()

        }else{
            observationData =FetchAsync(observationDao).execute().get()
        }

        return observationData
    }
    fun insertObservation(obs:ObservationEntity){
        InsertAsync(observationDao).execute(obs)

    }
    fun deleteObservation(obs:ObservationEntity){
        DeleteAsync(observationDao).execute(obs)
    }

    // Selector for ordering the database based on observation timestamp
    private fun selector(observation: ObservationEntity): Long? = observation.timestamp




    // Since database operations should not be run on the main thread, perform the database operations by using  async tasks.

    private class InsertAsync internal constructor(private val observationDao: ObservationDao):AsyncTask<ObservationEntity, Void, Void>() {
        override fun doInBackground(vararg params: ObservationEntity): Void? {
            observationDao.insertObservation(params[0])
            return null
        }

    }
    private class DeleteAsync internal constructor(private val observationDao: ObservationDao):AsyncTask<ObservationEntity, Void, Void>() {
        override fun doInBackground(vararg params: ObservationEntity): Void? {
            observationDao.deleteObservation(params[0])
            return null
        }



    }
    private class FetchAsync internal constructor(private val observationDao: ObservationDao):AsyncTask<Void, Void, List<ObservationEntity>>(){
        override fun doInBackground(vararg params: Void?): List<ObservationEntity> {
            return observationDao.loadAllObservations()
        }



    }


}
