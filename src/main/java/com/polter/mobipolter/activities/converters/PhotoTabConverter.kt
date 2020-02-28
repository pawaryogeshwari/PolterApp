package com.polter.mobipolter.activities.converters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PhotoTabConverter {

    @TypeConverter
    fun stringToList(json: String): List<String>? {
        val gson = Gson()
        val type = object : TypeToken<List<String>?>() {

        }.type
        return gson.fromJson<List<String>?>(json, type)
    }

    @TypeConverter
    fun listToString(list:List<String>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<String>?>() {

        }.getType()
        return gson.toJson(list, type)
    }


}