package com.example.observationdatabase

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager

import android.net.Uri

import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.EditorInfo
import android.widget.*
import kotlinx.android.synthetic.main.activity_observation_input.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.sql.Date
import android.support.v4.app.ActivityCompat


class ObservationInput : AppCompatActivity() {
    lateinit var rarities:Spinner
    lateinit var notes:EditText
    lateinit var note:String
    lateinit var rarity:String

    lateinit var latitude:String
    lateinit var longitude:String

    lateinit var speciesElement: EditText
    lateinit var species:String
    lateinit var save:Button
    lateinit var addImage:Button
    lateinit var repo:ObservationRepository
    lateinit var image:ImageView
    lateinit var imageUri: String
    var PICK_IMAGE:Int = 1
    var ENABLEDLOCATION = 2
    var LOCATIONALLOWED = false
    var STORAGEALLOWED = false

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
        LOCATIONALLOWED= intent.getBooleanExtra("LOCATION_ALLOWED", false)
        STORAGEALLOWED = intent.getBooleanExtra("STORAGE_READ_ALLOWED", false)
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

            if(STORAGEALLOWED) {

                // Define and start intent for fetching an image from the device gallery
                var intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_PICK
                startActivityForResult(intent, PICK_IMAGE)
            }else{

                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1)
            }

        }




        latitude ="-"
        longitude="-"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Get location if it has been enabled.
        if(LOCATIONALLOWED){
            if(isLocationEnabled()){
                getLocation()

            }else{

                // If location has been turned off, prompt the user once to turn it on with an alert dialog.
                // If positive button is pressed, user is taken to settings. There they can turn on the location
                val dialog = AlertDialog.Builder(this@ObservationInput)
                dialog.setTitle(R.string.gps_enable_prompt)
                dialog.setMessage(R.string.gps_enable_message)
                dialog.setPositiveButton(R.string.yes){dialog, which ->
                    var intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(intent, ENABLEDLOCATION)


                }
                dialog.setNegativeButton(R.string.no){
                        dialog, which ->
                    dialog.dismiss()
                }
                val prompt: AlertDialog = dialog.create()
                prompt.show()

            }
        }

    }



// Method for retreiving and storing user provided observation info to Database
   private fun processForm(){
        // Fetch string data
        species = speciesElement.text.toString()
        rarity = rarities.getSelectedItem().toString()
        note = notes.text.toString()

        val dateTime = Date(System.currentTimeMillis())

    // Define Entity with required attributes. Require species name at least
    var observation: ObservationEntity

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
       var listener = object:LocationListener{
           override fun onLocationChanged(location: Location?) {
               if(location != null) {
                   latitude = location?.latitude.toString()
                   longitude = location?.longitude.toString()
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
                var criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_FINE
                locationManager.requestSingleUpdate(criteria, listener, null)
            }catch(e:SecurityException) {}
    }



   private fun isLocationEnabled():Boolean{
       // Determine from provider strings, if location is enabled
        val locationManager: LocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var providers = locationManager.getProviders(true)
        return providers.contains("gps") || providers.contains("network")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when(requestCode){
            PICK_IMAGE -> {
                if(resultCode == Activity.RESULT_OK){

                    if(data != null){
                        /**
                         * From image fetching activity result, fetch and process the path to the selected image file
                         * Save the image path for storing it to database
                         * Get bitmap with the path and set it to the image view of the observation form
                         */
                        var uri:Uri = Uri.parse(data!!.dataString)
                        try{
                            var file = File(getRealPath(uri))
                            var uri:Uri = Uri.fromFile(file)
                            imageUri = uri.toString()
                            var loadedImage:Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                            image.setImageBitmap(loadedImage)
                       }catch(e:IOException){

                       }catch(e:FileNotFoundException){

                       }


                   }

               }
           }ENABLEDLOCATION ->{
            // If user enabled location only after accessing observation Input activity, fetch the location once.
            if(isLocationEnabled() && LOCATIONALLOWED){
                getLocation()
            }
        }

       }

   }

    /**
     * Method for determining the real path to selected file
     * Pass in Content scheme uri and with cursor fetch the uri with which the file can be retreived
     */
    private fun getRealPath(uri:Uri):String{
    var result:String
    var cursor:Cursor = this.contentResolver.query(uri, null, null, null)
    if(cursor==null){
        result = uri.path
    }else{
        cursor.moveToFirst()
        var ind = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        result = cursor.getString(ind)
        cursor.close()
    }
    return result
}





}
