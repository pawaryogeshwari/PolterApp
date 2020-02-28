package com.polter.mobipolter.activities.model

data class EstimationEntity (
         var species : String ? = null,
         var kind : String ? = null,
         var quality : String ? = null,
         var size : String ?= null,
         var length : Double = 0.00,
         var width : Double = 0.00,
         var unit : String ?= null,
         var volume : Double = 0.00
)