package com.polter.mobipolter.activities.converters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.polter.mobipolter.activities.model.LocationEntity

class LocationEntityConverter {

    @TypeConverter
    fun stringToLocationDetailEntity(json: String): LocationEntity? {
        val gson = Gson()
        val type = object : TypeToken<LocationEntity?>() {

        }.type
        return gson.fromJson<LocationEntity?>(json, type)
    }

    @TypeConverter
    fun locationDetailToString(locatioEntity:LocationEntity?): String {
        val gson = Gson()
        val type = object : TypeToken<LocationEntity?>() {

        }.getType()
        return gson.toJson(locatioEntity, type)
    }


}