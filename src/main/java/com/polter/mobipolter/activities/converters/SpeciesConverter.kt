package com.polter.mobipolter.activities.converters

import com.polter.mobipolter.activities.model.SpeciesEntity


import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SpeciesConverter {

    @TypeConverter
    fun stringToList(json: String): List<SpeciesEntity>? {
        val gson = Gson()
        val type = object : TypeToken<List<SpeciesEntity>?>() {

        }.type
        return gson.fromJson<List<SpeciesEntity>?>(json, type)
    }

    @TypeConverter
    fun listToString(list:List<SpeciesEntity>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<SpeciesEntity>?>() {

        }.getType()
        return gson.toJson(list, type)
    }


}