package com.polter.mobipolter.activities.converters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.polter.mobipolter.activities.model.MeasurementTabEntity

class MeasurementTabConverter {

    @TypeConverter
    fun stringToBasicDetailEntity(json: String): MeasurementTabEntity? {
        val gson = Gson()
        val type = object : TypeToken<MeasurementTabEntity?>() {

        }.type
        return gson.fromJson<MeasurementTabEntity?>(json, type)
    }

    @TypeConverter
    fun basicDetailToString(measureEntity:MeasurementTabEntity?): String {
        val gson = Gson()
        val type = object : TypeToken<MeasurementTabEntity?>() {

        }.getType()
        return gson.toJson(measureEntity, type)
    }


}