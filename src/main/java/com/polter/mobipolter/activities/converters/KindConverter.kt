package com.polter.mobipolter.activities.converters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.polter.mobipolter.activities.model.KindEntity

class KindConverter {

    @TypeConverter
    fun stringToList(json: String): List<KindEntity>? {
        val gson = Gson()
        val type = object : TypeToken<List<KindEntity>?>() {

        }.type
        return gson.fromJson<List<KindEntity>?>(json, type)
    }

    @TypeConverter
    fun listToString(list:List<KindEntity>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<KindEntity>?>() {

        }.getType()
        return gson.toJson(list, type)
    }


}