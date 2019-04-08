package com.example.observationdatabase

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.text.SimpleDateFormat


class ObservationAdapter(private var observations: List<ObservationEntity>):RecyclerView.Adapter<ObservationAdapter.ObservationHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObservationHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.observation_item, parent, false)
        return ObservationHolder(view)
    }

    override fun getItemCount(): Int {
        return observations.size

    }
    // Populate the observation item of recycler view with data from the adapter
    override fun onBindViewHolder(holder: ObservationHolder, position: Int) {
        val dateFormatter = SimpleDateFormat("dd.MM.yyyy")
        val timeFormatter = SimpleDateFormat("HH:mm")

        holder.species.text = "Spcies: " + observations[position].species
        holder.rarity.text = "Rarity: " + observations[position].rarity
        holder.notes.text = observations[position].notes

        holder.date.text = "Date: " + dateFormatter.format(observations[position].timestamp)
        holder.time.text = "Time: " + timeFormatter.format(observations[position].timestamp)

        holder.latitude.text = "Lat. " + observations[position].latitude
        holder.longitude.text = "Lon. " + observations[position].longitude
        if(observations[position].imageUri != "") {
            var imageUri: Uri = Uri.parse(observations[position].imageUri)
            holder.image.setImageURI(imageUri)
        }

    }

    // Defined observation holder and fetch all the recycler view elements from the observation item layout
    class ObservationHolder(observationView: View) : RecyclerView.ViewHolder(observationView){
        val species = observationView.findViewById(R.id.species_name) as TextView
        val rarity = observationView.findViewById(R.id.rarity) as TextView
        val notes = observationView.findViewById(R.id.notes) as TextView
        val date = observationView.findViewById(R.id.date) as TextView
        val time = observationView.findViewById(R.id.time) as TextView
        val latitude = observationView.findViewById(R.id.latitude) as TextView
        val longitude = observationView.findViewById(R.id.longitude) as TextView
        val image = observationView.findViewById(R.id.observationImage) as ImageView
    }
    // Update the adapter with this method, when the ordering of the items has changed
 fun update(sortedObservations:List<ObservationEntity>){
     observations = sortedObservations
     this.notifyDataSetChanged()
 }

}