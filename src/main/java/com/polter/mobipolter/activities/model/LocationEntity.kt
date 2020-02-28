package com.polter.mobipolter.activities.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "LogDetailLocationTab")
data class LocationEntity (@PrimaryKey
                           var locationLogDetailID : Int,
                           var locationLogTitle : String ?= null,
                           var latitude : Double = 0.0,
                           var longtitude : Double = 0.0,
                           var synced : Int
)