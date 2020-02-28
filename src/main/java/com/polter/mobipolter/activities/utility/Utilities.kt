package com.polter.mobipolter.activities.utility

import android.text.TextUtils
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utilities {

    val sysdate: String
        get() = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())

    val sysDateTime: String
        get() = SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.getDefault()).format(Date())

    val SURVEYING_TYPE = "surveying"
    val SURVEYING_REQ_CODE = 100
    val CURRENT_ITEM_NAME = "itemname"
    val CURRENT_ITEM_VALUE = "itemvalue"
    val CURRENT_KIND_ENTITY = "kindentity"


    val ITEM_LIST_REQ_CODE = 101
    val BUNDLE_OUTPUT_IMAGE_FILE = "imageFile"
    val BUNDLE_OUTPUT_IMAGE_URI = "imageUri"
    val EXPORT_CREATE_PDF = 1
    val EXPORT_SEND_PDF = 2
    val EXPORT_CREATE_CSV = 3
    val EXPORT_SEND_CSV = 4
    const val EXPORT_CREATE_EXCEL = 5
    val EXPORT_SEND_EXCEL = 6

    fun inputTextValidation(str : String) : String?{

        try{
            if(TextUtils.isEmpty(str)){
                return ""
            }else{
                return str
            }
        }catch (e: Exception){
            e.printStackTrace()
            return null
        }

    }

    fun inputNumberValidation(str : String) : Double{

        try{
            if(TextUtils.isEmpty(str)){
                return 0.00
            }else{
                return str.toDouble()
            }
        }catch (e:Exception){
            e.printStackTrace()
            return 0.00
        }

    }

    fun inputIntValidation(str : String) : Int{

        try{
            if(TextUtils.isEmpty(str)){
                return 0
            }else{
                return str.toInt()
            }
        }catch (e:Exception){
            e.printStackTrace()
            return 0
        }

    }
}

