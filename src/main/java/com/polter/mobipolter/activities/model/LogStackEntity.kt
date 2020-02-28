package com.polter.mobipolter.activities.model

import android.arch.persistence.room.*
import com.polter.mobipolter.activities.converters.*


@Entity(tableName = "LogEntryItems")
data class LogStackEntity(@PrimaryKey
                          var logId : Int,
                          var logTitle:String,
                          var logDate: String ? = null,
                          var logSpeciesCount: String ?= null,
                          var logKind: String ?= null,
                          var logQuality: String ?= null,
                          var logLength:Double,
                          var logVolume:Double,
                          var logForestOwner:String,
                          var recentTab:String,
                          @TypeConverters(BasicTabDetailConverter::class)
                          var logBasicEntity: BasicTabEntity ? = null,
                          @TypeConverters(MeasurementTabConverter::class)
                          var logMeasurementEntity: MeasurementTabEntity ? = null,
                          @TypeConverters(PhotoEntityConverter::class)
                          var logPhotoEntity: PhotoTabEntity ? = null,
                          @TypeConverters(LocationEntityConverter::class)
                          var logLocationEntity: LocationEntity ? = null,
                          var surveyingType:String,
                          var logCount:Int,
                          var logVolumeM3:Double,
                          var synced: Int)