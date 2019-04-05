package com.example.observationdatabase

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.location.Location
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
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
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.sql.Date

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
    private lateinit var fusedLocation: FusedLocationProviderClient
    var PICK_IMAGE:Int = 1100
    var ENABLED_LOCATION = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_observation_input)
        setSupportActionBar(toolbar)
        rarities = findViewById(R.id.rarities)
        notes = findViewById(R.id.notes)
        speciesElement = findViewById(R.id.species)
        repo = ObservationRepository(application)
        image = findViewById(R.id.speciesImage)
        ENABLED_LOCATION = intent.getBooleanExtra("ENABLED_LOCATION", false)

        ArrayAdapter.createFromResource(
            this,
            R.array.rarities,
            android.R.layout.simple_spinner_dropdown_item
        ).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            rarities.adapter = adapter
        }
        save = findViewById(R.id.save_button)
        save.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                processForm()
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }
        })
        addImage = findViewById(R.id.imageButton)
        addImage.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                var intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_PICK

                startActivityForResult(intent, PICK_IMAGE)

            }
        })
        notes.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                processForm()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                true
            }else{
                false
            }
        }

        latitude = "-"
        longitude="-"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fusedLocation = FusedLocationProviderClient(this)
        if(ENABLED_LOCATION) {
            fusedLocation.lastLocation.addOnSuccessListener {location:Location? ->
                if(location != null){
                    Log.d("GETLOC","GETTINGLOCATIN")
                    latitude = location.latitude.toString()
                    longitude = location.longitude.toString()
                }

            }
        }
    }
    fun processForm(){
        species = speciesElement.text.toString()
        rarity = rarities.getSelectedItem().toString()
        note = notes.text.toString()
        val dateTime = Date(System.currentTimeMillis())



        val observation = ObservationEntity(id= 0,species = species, rarity = rarity, notes = note,date = dateTime, timestamp = dateTime.time, latitude = latitude, longitude = longitude)
        repo.insertObservation(observation)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("ACTIVITY________REQUEST","" + requestCode)
        when(requestCode){
            PICK_IMAGE -> {
                if(resultCode == Activity.RESULT_OK){

                    Log.d("ACTIVITY________RESULT","" + resultCode)

                    if(data != null){

                        var imageUri:Uri = Uri.parse(data!!.dataString)
                        Log.d("ACTIVITY________IMGPATH","" + File(getRealPath(imageUri)).absolutePath)
                        try{
                            Log.d("ABSOLUTE PATH","" + File(getRealPath(imageUri)).absolutePath)
/*
                                                            //loadedImage = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                                                           loadedImage = BitmapFactory.decodeFile(File(getRealPath(imageUri)).absolutePath)
                                                           // loadedImage = FetchImageAsync(File(getRealPath(imageUri))).execute()
                                                          //var input:InputStream = this.contentResolver.openInputStream(imageUri)
                                                         //loadedImage = BitmapFactory.decodeStream(input)
                                                        var thumbnail:Bitmap = ThumbnailUtils.extractThumbnail(loadedImage, R.dimen.image_thumbnail_width_form,R.dimen.image_thumbnail_height_form)
                                                        image.setImageBitmap(thumbnail)


*/
                                                          Glide.with(this)
                                                              .load(File(getRealPath(imageUri)).absolutePath)
                                                              .format(DecodeFormat.PREFER_ARGB_8888)
                                                              .override(R.dimen.image_thumbnail_width_form, R.dimen.image_thumbnail_height_form)
                                                              .into(image)

                       }catch(e:IOException){
                           Log.d("IOEXCEPTION", "" + e)

                       }catch(e:FileNotFoundException){
                           Log.d("FILENOTFOUND", "" + e)
                       }


                   }

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
