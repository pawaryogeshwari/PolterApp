package com.polter.mobipolter.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.polter.mobipolter.R
import com.polter.mobipolter.activities.utility.Utilities
import kotlinx.android.synthetic.main.activity_surveying.*

class SelectSurveyingActivity : Activity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surveying)

        llSection.setOnClickListener {
            var intent = Intent()
            intent.putExtra(Utilities.SURVEYING_TYPE,tvSection.text)
            setResult(Activity.RESULT_OK,intent)
            finish()
        }

        llEstimation.setOnClickListener {
            var intent = Intent()
            intent.putExtra(Utilities.SURVEYING_TYPE,tvEstimation.text)
            setResult(Activity.RESULT_OK,intent)
            finish()
        }

        llLogs.setOnClickListener {
            var intent = Intent()
            intent.putExtra(Utilities.SURVEYING_TYPE,tvLogs.text)
            setResult(Activity.RESULT_OK,intent)
            finish()
        }

        imgBack.setOnClickListener {

            var intent = Intent()
            setResult(Activity.RESULT_CANCELED,intent)
            finish()
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent()
        setResult(Activity.RESULT_CANCELED,intent)
        finish()
    }
}