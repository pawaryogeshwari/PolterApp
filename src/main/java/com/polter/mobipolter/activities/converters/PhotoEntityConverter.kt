package com.polter.mobipolter.activities.converters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.polter.mobipolter.activities.model.PhotoTabEntity

class PhotoEntityConverter {

    @TypeConverter
    fun stringToPhotoDetailEntity(json: String): PhotoTabEntity? {
        val gson = Gson()
        val type = object : TypeToken<PhotoTabEntity?>() {

        }.type
        return gson.fromJson<PhotoTabEntity?>(json, type)
    }

    @TypeConverter
    fun photoDetailToString(photoEntity:PhotoTabEntity?): String {
        val gson = Gson()
        val type = object : TypeToken<PhotoTabEntity?>() {

        }.getType()
        return gson.toJson(photoEntity, type)
    }


}