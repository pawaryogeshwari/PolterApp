package com.polter.mobipolter.activities.converters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.polter.mobipolter.activities.model.MeasureHeightListEntity
import com.polter.mobipolter.activities.model.SectionEntity

class HeightListConverter {

    @TypeConverter
    fun stringToEntity(json: String): List<MeasureHeightListEntity>? {
        val gson = Gson()
        val type = object : TypeToken<List<MeasureHeightListEntity>?>() {

        }.type
        return gson.fromJson<List<MeasureHeightListEntity>?>(json, type)
    }

    @TypeConverter
    fun entityToString(entityList:List<MeasureHeightListEntity>?): String {
        val gson = Gson()
        val type = object : TypeToken<SectionEntity?>() {

        }.getType()
        return gson.toJson(entityList, type)
    }


}