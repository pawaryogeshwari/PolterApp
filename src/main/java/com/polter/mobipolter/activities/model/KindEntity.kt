package com.polter.mobipolter.activities.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Kind")
data class KindEntity (
        val kindAbbr : String,
        val kindName : String,
        @PrimaryKey
        val kindNo : String,
        val isKindSpecial : Boolean = false

)