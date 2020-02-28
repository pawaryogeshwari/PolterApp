package com.polter.mobipolter.activities.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "CurrentLogEntry")
data class CurrentLogEntry(@PrimaryKey
                             var logId : Int,
                             var synced : Int)