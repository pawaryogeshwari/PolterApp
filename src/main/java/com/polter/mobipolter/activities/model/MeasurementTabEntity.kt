package com.polter.mobipolter.activities.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import com.polter.mobipolter.activities.converters.EstimationConverter
import com.polter.mobipolter.activities.converters.LogsConverter
import com.polter.mobipolter.activities.converters.SectionConverter

@Entity(tableName = "LogDetailMeasurementTab")
data class MeasurementTabEntity (@PrimaryKey
                           var measureLogDetailID : Int,
                           var surveyingType : String,
                           @TypeConverters(EstimationConverter::class)
                           var estimationEntity: EstimationEntity ? = null,
                           @TypeConverters(SectionConverter::class)
                           var sectionEntity: SectionEntity ? = null,
                           @TypeConverters(LogsConverter::class)
                            var logsEntity: LogsEntity ? = null,
                            var logsCount : Int,
                            var minTopCm : Double,
                            var maxBaseCm: Double,
                            var oversizeM : Double,
                            var synced : Int
)