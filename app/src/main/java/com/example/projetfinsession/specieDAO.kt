package com.example.projetfinsession

import android.content.ContentValues
import android.content.Context

class specieDAO(context: Context) {
    private val dbHelper=BddHelper(context)
    private val db=dbHelper.writableDatabase

    //fonction d'insertion d'un specimen dans la base de données
    fun insertSpecie(specie: Specie) {
        val values = ContentValues().apply{
            put(BddHelper.COLUMN_ID,specie.id)
            put(BddHelper.COLUMN_NAME ,specie.name)
            put(BddHelper.COLUMN_DESCRIPTION ,specie.description)
            put(BddHelper.COLUMN_STATUS,specie.status)
            put(BddHelper.COLUMN_LATITUDE,specie.latitude)
            put(BddHelper.COLUMN_LONGITUDE,specie.longitude)
            put(BddHelper.COLUMN_TEMP_MAX,specie.temperature_max)
            put(BddHelper.COLUMN_TEMP_MIN,specie.temperature_min)
            put(BddHelper.COLUMN_HUMIDITY_MIN,specie.humidity_min)
            put(BddHelper.COLUMN_HUMIDITY_MAX,specie.humidity_max)
            put(BddHelper.COLUMN_POPULATION,specie.population)

        }
        db.insert(BddHelper.TABLE_NAME,null,values)

    }

    //fonction pour obtenir tous les specimens de la base de données
    fun getAllSpecies(): List<Specie> {
       val species = mutableListOf<Specie>()
        val projection = arrayOf(
            BddHelper.COLUMN_ID,
            BddHelper.COLUMN_NAME,
            BddHelper.COLUMN_STATUS,
            BddHelper.COLUMN_POPULATION,
            BddHelper.COLUMN_TEMP_MAX,
            BddHelper.COLUMN_TEMP_MIN,
            BddHelper.COLUMN_HUMIDITY_MIN,
            BddHelper.COLUMN_HUMIDITY_MAX,
            BddHelper.COLUMN_LATITUDE,
            BddHelper.COLUMN_LONGITUDE,
            BddHelper.COLUMN_DESCRIPTION
        )
        val cursor = db.query(BddHelper.TABLE_NAME,projection,null,null,null,null,null)
        with(cursor){
            while(moveToNext()){
                val id = getString(getColumnIndexOrThrow(BddHelper.COLUMN_ID))
                val name = getString(getColumnIndexOrThrow(BddHelper.COLUMN_NAME))
                val status = getString(getColumnIndexOrThrow(BddHelper.COLUMN_STATUS))
                val population = getDouble(getColumnIndexOrThrow(BddHelper.COLUMN_POPULATION))
                val temperature_max = getDouble(getColumnIndexOrThrow(BddHelper.COLUMN_TEMP_MAX))
                val temperature_min = getDouble(getColumnIndexOrThrow(BddHelper.COLUMN_TEMP_MIN))
                val humidity_min = getDouble(getColumnIndexOrThrow(BddHelper.COLUMN_HUMIDITY_MIN))
                val humidity_max = getDouble(getColumnIndexOrThrow(BddHelper.COLUMN_HUMIDITY_MAX))
                val latitude = getString(getColumnIndexOrThrow(BddHelper.COLUMN_LATITUDE))
                val longitude = getString(getColumnIndexOrThrow(BddHelper.COLUMN_LONGITUDE))
                val description = getString(getColumnIndexOrThrow(BddHelper.COLUMN_DESCRIPTION))

                species.add(Specie(id,name,status,population,temperature_max,temperature_min,humidity_min,humidity_max,latitude,longitude,description))
            }
        }
        cursor.close()
        return species
    }

    fun deleteSpecie(id: String) {
        val selection = "${BddHelper.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(id)
        db.delete(BddHelper.TABLE_NAME, selection, selectionArgs)
    }

    fun updateSpecie(clickedSpecie: Specie) {
        val values = ContentValues().apply {
            put(BddHelper.COLUMN_NAME, clickedSpecie.name)
            put(BddHelper.COLUMN_DESCRIPTION, clickedSpecie.description)
            put(BddHelper.COLUMN_STATUS, clickedSpecie.status)
            put(BddHelper.COLUMN_LATITUDE, clickedSpecie.latitude)
            put(BddHelper.COLUMN_LONGITUDE, clickedSpecie.longitude)
            put(BddHelper.COLUMN_TEMP_MAX, clickedSpecie.temperature_max)
            put(BddHelper.COLUMN_TEMP_MIN, clickedSpecie.temperature_min)
            put(BddHelper.COLUMN_HUMIDITY_MIN, clickedSpecie.humidity_min)
            put(BddHelper.COLUMN_HUMIDITY_MAX, clickedSpecie.humidity_max)
            put(BddHelper.COLUMN_POPULATION, clickedSpecie.population)
        }
        val selection = "${BddHelper.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(clickedSpecie.id)
        db.update(BddHelper.TABLE_NAME, values, selection, selectionArgs)

    }

    fun getSpecieById(speciesId: String?): Specie {
        val projection = arrayOf(
            BddHelper.COLUMN_ID,
            BddHelper.COLUMN_NAME,
            BddHelper.COLUMN_STATUS,
            BddHelper.COLUMN_POPULATION,
            BddHelper.COLUMN_TEMP_MAX,
            BddHelper.COLUMN_TEMP_MIN,
            BddHelper.COLUMN_HUMIDITY_MIN,
            BddHelper.COLUMN_HUMIDITY_MAX,
            BddHelper.COLUMN_LATITUDE,
            BddHelper.COLUMN_LONGITUDE,
            BddHelper.COLUMN_DESCRIPTION,
        )
        val selection = "${BddHelper.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(speciesId)
        val cursor = db.query(
            BddHelper.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        cursor.moveToFirst()
            val id = cursor.getString(cursor.getColumnIndexOrThrow(BddHelper.COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(BddHelper.COLUMN_NAME))
            val status = cursor.getString(cursor.getColumnIndexOrThrow(BddHelper.COLUMN_STATUS))
            val population = cursor.getDouble(cursor.getColumnIndexOrThrow(BddHelper.COLUMN_POPULATION))
            val temperature_max = cursor.getDouble(cursor.getColumnIndexOrThrow(BddHelper.COLUMN_TEMP_MAX))
            val temperature_min = cursor.getDouble(cursor.getColumnIndexOrThrow(BddHelper.COLUMN_TEMP_MIN))
            val humidity_min = cursor.getDouble(cursor.getColumnIndexOrThrow(BddHelper.COLUMN_HUMIDITY_MIN))
            val humidity_max = cursor.getDouble(cursor.getColumnIndexOrThrow(BddHelper.COLUMN_HUMIDITY_MAX))
            val latitude = cursor.getString(cursor.getColumnIndexOrThrow(BddHelper.COLUMN_LATITUDE))
            val longitude = cursor.getString(cursor.getColumnIndexOrThrow(BddHelper.COLUMN_LONGITUDE))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(BddHelper.COLUMN_DESCRIPTION))
        cursor.close()
        return Specie(id, name, status, population, temperature_max, temperature_min, humidity_min, humidity_max, latitude, longitude, description)

    }

}