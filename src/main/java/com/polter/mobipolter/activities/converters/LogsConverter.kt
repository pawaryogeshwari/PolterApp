package com.polter.mobipolter.activities.converters

import com.polter.mobipolter.activities.model.LogsEntity

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LogsConverter {

    @TypeConverter
    fun stringToLogsEntity(json: String): LogsEntity? {
        val gson = Gson()
        val type = object : TypeToken<LogsEntity?>() {

        }.type
        return gson.fromJson<LogsEntity?>(json, type)
    }

    @TypeConverter
    fun logsEntityToString(logsEntity:LogsEntity?): String {
        val gson = Gson()
        val type = object : TypeToken<LogsEntity?>() {

        }.getType()
        return gson.toJson(logsEntity, type)
    }


}