package com.polter.mobipolter.activities.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "LogDetailBasicTab")
data class BasicTabEntity(@PrimaryKey
                          var basicLogDetailID : Int = 0,
                          var iosID : String?,
                          var stackNR : String?,
                          var foreignNR :String?,
                          var date: String ?,
                          var price: Double = 0.00,
                          var fsc: Int = 0,
                          var pefc: Int = 0,
                          var location: String?,
                          var district: String?,
                          var forestOwner:String?,
                          var forester:String? ,
                          var forestry:String?,
                          var clearer:String?,
                          var feller: String? ,
                          var skidder: String?,
                          var comment : String?,
                          var synced : Int){




   /* constructor(id: Int,stackNr: String?,foreignNr: String?,date: String?, price: Double, fsc : Int, location: String?, district : String?,
                forestOwner: String?, forester: String?,forestry: String?,clearer: String?,feller: String?,
                skidder: String?, comment : String?) : this(id,stackNr) {

        this.basicLogDetailID = id
        this.stackNR = stackNr
        this.foreignNR = foreignNr;
        this.date = date
        this.price = price
        this.fsc = fsc
        this.location = location
        this.district = district
        this.forestOwner = forestOwner
        this.forester = forester
        this.forestry = forestry
        this.clearer = clearer
        this.feller = feller
        this.skidder = skidder
        this.comment = comment

    }*/

}

