package com.example.observationdatabase

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager

import android.net.Uri

import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.widget.*
import kotlinx.android.synthetic.main.activity_observation_input.*
import java.io.FileNotFoundException
import java.io.IOException
import java.sql.Date
import android.support.v4.app.ActivityCompat



class ObservationInput : AppCompatActivity() {
   private lateinit var rarities:Spinner
    private lateinit var notes:EditText
    private lateinit var note:String
    private lateinit var rarity:String

    private lateinit var latitude:String
    private lateinit var longitude:String

    private lateinit var speciesElement: EditText
    private lateinit var species:String
    private lateinit var save:Button
    private lateinit var addImage:Button
    private lateinit var repo:ObservationRepository
    private lateinit var image:ImageView
    private lateinit var imageUri: String
    private var pickImage:Int = 1
    private var enabledLocation = 2
    private var locationAllowed = false
    private var storageAllowed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_observation_input)
        setSupportActionBar(toolbar)
        // Fetch UI elements
        rarities = findViewById(R.id.rarities)

        notes = findViewById(R.id.notes)
        speciesElement = findViewById(R.id.species)
        image = findViewById(R.id.speciesImage)

        // Observation repository for datbase operations
        repo = ObservationRepository(application)


        // Fetch the intent extras
        locationAllowed= intent.getBooleanExtra("LOCATION_ALLOWED", false)
        storageAllowed = intent.getBooleanExtra("STORAGE_READ_ALLOWED", false)
        imageUri = ""

        // Define array adapter for Observation input spinner.
        // USe predefined list  defined in arrays.xml
        ArrayAdapter.createFromResource(
            this,
            R.array.rarities,
            android.R.layout.simple_spinner_dropdown_item
        ).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            rarities.adapter = adapter
        }

        // Fetch save button and in click lister, process observation from and start main activity
        save = findViewById(R.id.save_button)
        save.setOnClickListener {
            // Require at least name for species before saving observation
            if(speciesElement.text.toString() != "") {
                processForm()
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }else{
                Toast.makeText(this, "Provide at least the name for species",Toast.LENGTH_SHORT).show()
            }
        }


        // Fetch button for adding image to observations
        addImage = findViewById(R.id.imageButton)
        addImage.setOnClickListener {

            if(storageAllowed) {

                // Define and start intent for fetching an image from the device gallery
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_PICK

                startActivityForResult(intent, pickImage)
            }else{

                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1)
            }

        }




        latitude ="-"
        longitude="-"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Get location if it has been enabled.
        if(locationAllowed){
            if(isLocationEnabled()){
                getLocation()

            }else{

                // If location has been turned off, prompt the user once to turn it on with an alert dialog.
                // If positive button is pressed, user is taken to settings. There they can turn on the location
                val prompt = AlertDialog.Builder(this@ObservationInput)
                prompt.setTitle(R.string.gps_enable_prompt)
                prompt.setMessage(R.string.gps_enable_message)
                prompt.setPositiveButton(R.string.yes){dialog, which ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(intent, enabledLocation)


                }
                prompt.setNegativeButton(R.string.no){
                        dialog, which ->
                    dialog.dismiss()
                }
                val gpsDialog: AlertDialog = prompt.create()
                gpsDialog.show()

            }
        }

    }



// Method for retreiving and storing user provided observation info to Database
   private fun processForm(){
        // Fetch string data
        species = speciesElement.text.toString()
        rarity = rarities.selectedItem.toString()
        note = notes.text.toString()

        val dateTime = Date(System.currentTimeMillis())

    // Define Entity with required attributes. Require species name at least
    val observation: ObservationEntity

        observation = ObservationEntity(
            id = 0,
            species = species,
            rarity = rarity,
            notes = note,
            timestamp = dateTime.time,
            latitude = latitude,
            longitude = longitude,
            imageUri = imageUri
        )
        // Save the observations to database.
        repo.insertObservation(observation)








    }




// Location is retreived with this method
   private fun getLocation(){
        val locationManager: LocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    // Location listener implementation. Only on Location Changed is used
       val listener = object:LocationListener{
           override fun onLocationChanged(location: Location?) {
               if(location != null) {
                   latitude = location.latitude.toString()
                   longitude = location.longitude.toString()
               }
           }

           override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
               TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
           }

           override fun onProviderEnabled(provider: String?) {
               TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
           }

           override fun onProviderDisabled(provider: String?) {
               TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
           }

       }


            // Use location manager to fetch location once with fine accuracy
            try {
                val criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_FINE
                locationManager.requestSingleUpdate(criteria, listener, null)
            }catch(e:SecurityException) {}
    }



   private fun isLocationEnabled():Boolean{
       // Determine from provider strings, if location is enabled
        val locationManager: LocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = locationManager.getProviders(true)
        return providers.contains("gps") || providers.contains("network")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when(requestCode){
            pickImage -> {
                if(resultCode == Activity.RESULT_OK){

                    if(data != null){
                        /**
                         * From image fetching activity result, fetch content path to the selected image file
                         * Save the image path for storing it to database
                         * Get bitmap with the path and set it to the image view of the observation form
                         */
                        val uri:Uri = Uri.parse(data.dataString)
                        try{
                            imageUri = uri.toString()
                            val loadedImage:Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                            image.setImageBitmap(loadedImage)
                       }catch(e:IOException){

                       }catch(e:FileNotFoundException){

                       }


                   }

               }
           }enabledLocation ->{
            // If user enabled location only after accessing observation Input activity, fetch the location once.
            if(isLocationEnabled() && locationAllowed){
                getLocation()
            }
        }

       }

   }






}
