package com.polter.mobipolter.activities.model

import java.io.Serializable

data class AddLogEntity (
        var index : Int = 0,
        var log_nr : Int = 0,
        var plate : Int = 0,
        var species : String ?= null,
        var kind : String ?= null,
        var length_m : Double,
        var oversize_m : Double,
        var diameter_cm : Double,
        var quality: String ?= null,
        var klasse : String ?= null,
        var volume_m3 : Double,
        var bark_cm : Double,
        var isSelected: Boolean

) : Serializable