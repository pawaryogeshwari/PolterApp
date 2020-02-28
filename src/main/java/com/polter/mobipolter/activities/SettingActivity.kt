package com.polter.mobipolter.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.polter.mobipolter.R
import com.polter.mobipolter.activities.utility.Utilities
import kotlinx.android.synthetic.main.activity_settings.*

class SettingActivity : Activity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        getActionBar()?.setDisplayHomeAsUpEnabled(true)
        getActionBar()?.setTitle(getString(R.string.settings))
        imgBack.setOnClickListener {
            finish()
        }

        speciesList.setOnClickListener {

            startActivity(Intent(this, ItemListingActivity::class.java)
                    .putExtra(Utilities.CURRENT_ITEM_NAME,getString(R.string.species)))
        }

        addLogoLayout.setOnClickListener {

            startActivity(Intent(this, AddLogoActivity::class.java))
        }

        txtVersion.text = getString(R.string.polter_version,"1.5")
        txtWhatsNew.text = getString(R.string.whats_new,"1.5")

        addAddressLayout.setOnClickListener {

            startActivity(Intent(this, ShowDetailsActivity::class.java)
                    .putExtra("type","addr"))
        }

        addBankLayout.setOnClickListener {

            startActivity(Intent(this, ShowDetailsActivity::class.java)
                    .putExtra("type","bank"))
        }

    }
}