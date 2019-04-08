package com.example.observationdatabase

import android.arch.persistence.room.*
import android.content.Context
import android.os.AsyncTask
// The ObservationEntity and ObservationDAO is bind together here. The Application uses ROOM database
// Return database instance if one already exists, otherwise build a new one by using synchronized operation

@Database(entities = [(ObservationEntity::class)],version = 1)
abstract class ObservationDatabase:RoomDatabase(){
    abstract fun observationDao(): ObservationDao
    companion object DatabaseSingleton{

        private var dbInstance:ObservationDatabase?=null

        fun getDb(context:Context):ObservationDatabase{
            val tempInstance = dbInstance
            if(tempInstance!=null){
                return tempInstance
            }

            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ObservationDatabase::class.java,
                    "Database"
                ).build()
                dbInstance = instance
                return instance
            }

        }

    }
}

