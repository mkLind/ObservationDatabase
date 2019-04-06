package com.example.observationdatabase

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import kotlinx.android.synthetic.main.activity_observation_input.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.sql.Date
import kotlin.math.round
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.drawable.BitmapDrawable
import android.provider.DocumentsContract

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
    lateinit var loadedImage:Bitmap
    var PICK_IMAGE:Int = 1
    var ENABLEDLOCATION = 2
    var LOCATIONALLOWED = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_observation_input)
        setSupportActionBar(toolbar)
        rarities = findViewById(R.id.rarities)
        notes = findViewById(R.id.notes)
        speciesElement = findViewById(R.id.species)
        repo = ObservationRepository(application)
        image = findViewById(R.id.speciesImage)
        LOCATIONALLOWED= intent.getBooleanExtra("LOCATION_ALLOWED", false)



        ArrayAdapter.createFromResource(
            this,
            R.array.rarities,
            android.R.layout.simple_spinner_dropdown_item
        ).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            rarities.adapter = adapter
        }
        save = findViewById(R.id.save_button)
        save.setOnClickListener {
            processForm()
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
        addImage = findViewById(R.id.imageButton)
        addImage.setOnClickListener {
            var intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK

            startActivityForResult(intent, PICK_IMAGE)
        }
        notes.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                processForm()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                true
            }else{
                false
            }
        }

        latitude ="-"
        longitude="-"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(LOCATIONALLOWED){
            if(isLocationEnabled()){
                getLocation()
            }else{
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




   private fun processForm(){
        species = speciesElement.text.toString()
        rarity = rarities.getSelectedItem().toString()
        note = notes.text.toString()
        val dateTime = Date(System.currentTimeMillis())
        val observation = ObservationEntity(id= 0,species = species, rarity = rarity, notes = note,date = dateTime, timestamp = dateTime.time, latitude = latitude, longitude = longitude)
        repo.insertObservation(observation)


    }





   private fun getLocation(){
        val locationManager: LocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
            try {
                var criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_FINE
                locationManager.requestSingleUpdate(criteria, listener, null)
            }catch(e:SecurityException) {}
    }



   private fun isLocationEnabled():Boolean{
        val locationManager: LocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var providers = locationManager.getProviders(true)
        return providers.contains("gps") || providers.contains("network")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when(requestCode){
            PICK_IMAGE -> {
                if(resultCode == Activity.RESULT_OK){

                    if(data != null){

                        var imageUri:Uri = Uri.parse(data!!.dataString)
                        try{
                            var file = File(getRealPath(imageUri))
                            var uri:Uri = Uri.fromFile(file)
                            loadedImage = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                            image.setImageBitmap(loadedImage)
                       }catch(e:IOException){
                           Log.d("IOEXCEPTION", "" + e)

                       }catch(e:FileNotFoundException){
                           Log.d("FILENOTFOUND", "" + e)
                       }


                   }

               }
           }ENABLEDLOCATION ->{
            Log.d("LOCATION","ENABLED")
            if(isLocationEnabled() && LOCATIONALLOWED){
                getLocation()
            }
        }

       }

   }
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
