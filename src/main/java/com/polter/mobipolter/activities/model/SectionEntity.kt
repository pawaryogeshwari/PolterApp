package com.polter.mobipolter.activities.model

import android.arch.persistence.room.TypeConverters
import com.polter.mobipolter.activities.converters.HeightListConverter

data class SectionEntity (
        val species : String ? = null,
        val kind : String ? = null,
        val quality : String ? = null,
        val length : Double,
        val width : Double,
        val section_length : Double,
        @TypeConverters(HeightListConverter::class)
        val height_list : List<MeasureHeightListEntity> ? = null,
        val sum_of_sections: Double,
        val height_count : Double,
        val avg_height : Double,
        val volume_bark : Double,
        val st : Double,
        val distr : Double,
        val m3 : Double,
        val factor : Double

)