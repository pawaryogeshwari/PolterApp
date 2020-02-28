package com.polter.mobipolter.activities

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.polter.mobipolter.R
import com.polter.mobipolter.activities.model.BankEntity
import kotlinx.android.synthetic.main.activity_show_details.*
import com.google.gson.Gson



class ShowDetailsActivity : Activity(){

    lateinit var mAppSharedPreferences : SharedPreferences
    lateinit var mAppSharedPreferencesEditor : SharedPreferences.Editor
    var mBankEntity : BankEntity ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_details)

        mAppSharedPreferences = getSharedPreferences("App_Pref",0)
        mAppSharedPreferencesEditor = mAppSharedPreferences.edit()

        if(mAppSharedPreferences.getString("address",null)!= null){

            edtAddress.setText(mAppSharedPreferences.getString("address",null))
        }

        if(mAppSharedPreferences.getString("bank",null)!= null){
            val gson = Gson()
            val json = mAppSharedPreferences.getString("bank", "")
            val obj = gson.fromJson<BankEntity>(json, BankEntity::class.java)
            etAccountNo.setText(obj.bankAccountNo)
            etBankName.setText(obj.bankAccountName)
            etIfscCode.setText(obj.ifscBankCode)
            etOtherInfo.setText(obj.bankOtherInfo)
        }
        if(intent.getStringExtra("type")!= null){
            if(intent.getStringExtra("type").equals("addr")){
                txtTitle.setText(getString(R.string.address))
                edtAddress.visibility = View.VISIBLE
                llBankLayout.visibility = View.GONE
            }
            else{

                txtTitle.setText(getString(R.string.bank))
                edtAddress.visibility = View.GONE
                llBankLayout.visibility = View.VISIBLE
               // edtBank.visibility = View.VISIBLE
            }
        }
        txtDone.setOnClickListener {
            if(intent.getStringExtra("type").equals("bank")){
                if(TextUtils.isEmpty(etBankName.text.toString())){
                    etBankName.setError(getString(R.string.alert_field_required))
                    return@setOnClickListener
                }
                if(TextUtils.isEmpty(etAccountNo.text.toString())){
                    etAccountNo.setError(getString(R.string.alert_field_required))
                    return@setOnClickListener
                }
                if(TextUtils.isEmpty(etIfscCode.text.toString())){

                    etIfscCode.setError(getString(R.string.alert_field_required))
                    return@setOnClickListener
                }
            }else
            {
                if(TextUtils.isEmpty(edtAddress.text.toString())){

                 //   edtAddress.setError(getString(R.string.alert_field_required))
                    Toast.makeText(this,"Please enter address",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            saveDetails()
            finish()
        }
        imgBack.setOnClickListener {
          //  saveDetails()
            finish()
        }
    }

    override fun onBackPressed() {

       // saveDetails()
        super.onBackPressed()
    }

    fun saveDetails(){
        if(intent.getStringExtra("type").equals("bank")){
            val bankEntity = BankEntity(etBankName.text.toString(),
                    etAccountNo.text.toString(),
                    etIfscCode.text.toString(),
                    etOtherInfo.text.toString())
            val gson = Gson()
            val json = gson.toJson(bankEntity)
            mAppSharedPreferencesEditor.putString("bank", json)
        }
        else{
            mAppSharedPreferencesEditor.putString("address", edtAddress.text.toString())
        }

        mAppSharedPreferencesEditor.commit()

    }

}