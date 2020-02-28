package com.polter.mobipolter.activities.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Species")
data class SpeciesEntity (
        val speciesAbbr : String,
        @PrimaryKey
        val speciesName : String,
        val speciesLatName : String
)