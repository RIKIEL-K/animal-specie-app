package com.example.projetfinsession

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class SpeciesAdapter(private val context : Context,private val SpeciesList: List<Specie>) : RecyclerView.Adapter<SpeciesAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeciesAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_species,parent,false)
        return SpeciesAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpeciesAdapter.ViewHolder, position: Int) {
       val specie = SpeciesList[position]
        holder.speciesMaxTemperature.text = specie.temperature_max.toString()
        holder.speciesMinTemperature.text = specie.temperature_min.toString()
        holder.speciesMaxHumidity.text = specie.humidity_max.toString()
        holder.speciesName.text = specie.name

        holder.itemView.setOnLongClickListener {
            (context as species).showSpeciesOptionsDialog(specie)
            true
        }
    }

    override fun getItemCount(): Int {
        return SpeciesList.size
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var speciesMaxTemperature : TextView
       var speciesMinTemperature : TextView
       var speciesMaxHumidity : TextView
        var speciesName : TextView

        init {
            speciesMaxTemperature = itemView.findViewById(R.id.speciesMaxTemperature)
            speciesMinTemperature = itemView.findViewById(R.id.speciesMinTemperature)
            speciesMaxHumidity = itemView.findViewById(R.id.speciesMaxHumidity)
            speciesName = itemView.findViewById(R.id.speciesName)
        }

    }
}

