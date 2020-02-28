package com.polter.mobipolter.activities.converters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.polter.mobipolter.activities.model.SectionEntity

class SectionConverter {

    @TypeConverter
    fun stringToEntity(json: String): SectionEntity? {
        val gson = Gson()
        val type = object : TypeToken<SectionEntity?>() {

        }.type
        return gson.fromJson<SectionEntity?>(json, type)
    }

    @TypeConverter
    fun entityToString(entity:SectionEntity?): String {
        val gson = Gson()
        val type = object : TypeToken<SectionEntity?>() {

        }.getType()
        return gson.toJson(entity, type)
    }


}