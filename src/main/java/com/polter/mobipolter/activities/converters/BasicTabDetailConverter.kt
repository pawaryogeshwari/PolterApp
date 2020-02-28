package com.polter.mobipolter.activities.converters

import android.arch.persistence.room.TypeConverter
import com.polter.mobipolter.activities.model.BasicTabEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BasicTabDetailConverter {

    @TypeConverter
    fun stringToBasicDetailEntity(json: String): BasicTabEntity? {
        val gson = Gson()
        val type = object : TypeToken<BasicTabEntity?>() {

        }.type
        return gson.fromJson<BasicTabEntity?>(json, type)
    }

    @TypeConverter
    fun basicDetailToString(basicTabEntity:BasicTabEntity?): String {
        val gson = Gson()
        val type = object : TypeToken<BasicTabEntity?>() {

        }.getType()
        return gson.toJson(basicTabEntity, type)
    }


}