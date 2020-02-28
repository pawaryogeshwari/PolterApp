package com.polter.mobipolter.activities.model

import java.io.Serializable

data class PredefinedListEntity (
        val itemAbbr : String ? = null,
        val itemName : String ?= null,
        val itemLatName : String ? = null,
        val itemType : String ? = null,
        val isKindSpecial : Boolean = false
): Serializable