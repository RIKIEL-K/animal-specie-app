package com.example.projetfinsession

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Specie(
    var id : String? = "",
    var name : String,
    var status:String,
    var population: Double,
    var temperature_max: Double,
    var temperature_min: Double,
    var humidity_min: Double,
    var humidity_max: Double,
    var latitude : String,
    var longitude : String,
    var description : String,
): Parcelable{
    constructor():this("","","",0.0,0.0,0.0,0.0,0.0,"","", "")
}

