package com.polter.mobipolter.activities.model

data class LogsEntity (

        val log_count : Int,
        val add_log_entity_list : List<AddLogEntity> ? = null,
        val size_class_entity_list : List<SizeClassEntity> ? = null,
        val total_volume_m3 : Double
)