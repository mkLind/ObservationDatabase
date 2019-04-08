package com.example.observationdatabase

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ObservationAdapter
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var observationList: List<ObservationEntity>
    private lateinit var observationRepository: ObservationRepository
    private var PERMISSIONREQUEST: Int = 100





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)



        // Check if the user has given the permission for the application to access location and to read memory
        // Save the result in a variable. If at least one is not granted, ask for permissions.
        var permissionsGranted = verifyPermissionGranted(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE)

        if(!permissionsGranted){

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSIONREQUEST)
        }
        // Instantiate observation repository and fetch the observations so that they are ordered from newest to oldest
        observationRepository = ObservationRepository(application)
        observationList = observationRepository.getObservations("newest")

        // Linear layout manager and ObservationAdapter for the Recycler view of the Main activity
        viewManager = LinearLayoutManager(this)
        viewAdapter = ObservationAdapter(observationList)

        // Define the recycler view and set the adapter and layout manager
        recyclerView = findViewById<RecyclerView>(R.id.observations).apply{
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        viewAdapter.notifyDataSetChanged()



        // Set on click listener to the floating action button.
        // For storage and location permissions pass a boolean extra. True if the permission has been granted, false otherwise
        fab.setOnClickListener { view ->
        val intent = Intent(this, ObservationInput::class.java)

            if(verifyPermissionGranted(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                intent.putExtra("LOCATION_ALLOWED",true)
            }else{
                intent.putExtra("LOCATION_ALLOWED",false)
            }
            if(verifyPermissionGranted(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                intent.putExtra("STORAGE_READ_ALLOWED", true)
            }else{
                intent.putExtra("STORAGE_READ_ALLOWED", false)
            }

            startActivity(intent)



        }



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // When the user changes the ordering of the items displayed in the main activity,
        // the change of ordering is handled here
        return when (item.itemId) {
            R.id.order_newest_first->{

                observationList = observationRepository.getObservations("newest")
                viewAdapter.update(observationList)
                return true
            }
            R.id.order_oldest_first->{

                observationList = observationRepository.getObservations("oldest")
                viewAdapter.update(observationList)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // A method for verifying that the permissions passed as varargs have been granted
    private fun verifyPermissionGranted(vararg  permissions: String):Boolean{
        var granted = permissions.toList().all{
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        return granted
    }




}
