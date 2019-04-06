package com.example.observationdatabase

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.coroutines.*
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.RuntimeException
import android.support.v7.graphics.drawable.DrawableWrapper;
import com.google.android.gms.location.FusedLocationProviderClient
import java.util.jar.Manifest

import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ObservationAdapter
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var observationList: List<ObservationEntity>
    private lateinit var observationRepository: ObservationRepository

    private var PERMISSION: Int = 100
    private var ALLOWEDLOCATION: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        ALLOWEDLOCATION = checkPermissions(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE)

        observationRepository = ObservationRepository(application)
        observationList = observationRepository.getObservations("desc")


        viewManager = LinearLayoutManager(this)
        viewAdapter = ObservationAdapter(observationList)


        recyclerView = findViewById<RecyclerView>(R.id.observations).apply{
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        viewAdapter.notifyDataSetChanged()

        fab.setOnClickListener { view ->
        val intent = Intent(this, ObservationInput::class.java)
            intent.putExtra("LOCATION_ALLOWED", ALLOWEDLOCATION)
            startActivity(intent)



        }



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.order_ascending->{

                observationList = observationRepository.getObservations("asc")
                viewAdapter.update(observationList)
                return true
            }
            R.id.order_descending->{

                observationList = observationRepository.getObservations("desc")
                viewAdapter.update(observationList)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

private fun checkPermissions(vararg permissions: String):Boolean{
    val permissionsGranted = permissions.toList().all{
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
    if(!permissionsGranted){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION)
        return permissionsGranted
    }
return permissionsGranted
}

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION -> {
                if(grantResults.isNotEmpty()){
                   ALLOWEDLOCATION = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

                }
            }

        }
    }


}
