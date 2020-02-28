package com.polter.mobipolter.activities.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "LogDetailPhotosTab")
data class PhotoTabEntity (@PrimaryKey
        var photoLogDetailID : Int,
                           var imageCount : Int,
                           var imageURLs : List<String> ? = null,
                           var imageUriList : List<String> ? = null,
                           var synced : Int
)