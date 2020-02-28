package com.polter.mobipolter.activities.converters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.polter.mobipolter.activities.model.EstimationEntity

class EstimationConverter {

    @TypeConverter
    fun stringToEntity(json: String): EstimationEntity? {
        val gson = Gson()
        val type = object : TypeToken<EstimationEntity?>() {

        }.type
        return gson.fromJson<EstimationEntity?>(json, type)
    }

    @TypeConverter
    fun entityToString(entity:EstimationEntity?): String {
        val gson = Gson()
        val type = object : TypeToken<EstimationEntity?>() {

        }.getType()
        return gson.toJson(entity, type)
    }


}