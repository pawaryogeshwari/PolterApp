package com.polter.mobipolter.activities.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.polter.mobipolter.R
import com.polter.mobipolter.activities.LogEntryDetailActivity
import com.polter.mobipolter.activities.model.BasicTabEntity
import com.polter.mobipolter.activities.utility.Utilities
import kotlinx.android.synthetic.main.fragment_basic.*
import java.lang.Exception

@SuppressLint("ValidFragment")
class BasicFragment(lis: saveListener) : Fragment() {

    var logId : Int = 0
    var txtIosID: String ? = null
    var txtStackNR : String ? = null
    var txtForeignNR : String ? = null
    var txtDate : String ? = null
    var txtPrice : Double = 0.00
    var txtLocation : String ? = null
    var txtDistrict : String ? = null
    var txtForestOwner : String ? = null
    var txtForester : String ? = null
    var txtForestry : String ? = null
    var txtFeller : String ? = null
    var txtClearer : String ? = null
    var txtSkidder: String ? = null
    var txtComment: String ? = null
    var isPaused = false
    lateinit var listener : saveListener



    var basicTabEntity : BasicTabEntity ? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =// Inflate the layout for this fragment
            inflater.inflate(R.layout.fragment_basic, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        logId = (this.activity as LogEntryDetailActivity).logID
        txtStackNR = (this.activity as LogEntryDetailActivity).stackNR
        Log.d("LogId",""+logId)

        basicTabEntity = ((activity as LogEntryDetailActivity)).viewModel?.findLogBasicTabById(logId)
        if(basicTabEntity == null){
            var insertEntity = BasicTabEntity(logId,null,txtStackNR,
                    null,Utilities.sysDateTime,0.00,
                    0,0,null,null,null,null,null,null,null,null,null,0)
            ((activity as LogEntryDetailActivity)).viewModel?.insertBasicTabDetail(insertEntity)
            edtStackNr.setText(txtStackNR)
            edtDate.setText(Utilities.sysDateTime)
        }else {

            if(basicTabEntity?.iosID != null){
                edtIosId.setText(basicTabEntity?.iosID.toString())
            }

            if(basicTabEntity?.stackNR != null){
                edtStackNr.setText(basicTabEntity?.stackNR.toString())
            }
            if(basicTabEntity?.foreignNR != null){
                edtForeignNr.setText(basicTabEntity?.foreignNR.toString())
            }
            if(basicTabEntity?.date != null){
                edtDate.setText(basicTabEntity?.date.toString())
            }
            if(basicTabEntity?.price != null){
                edtPrice.setText(basicTabEntity?.price.toString())
            }
            if(basicTabEntity?.location != null){
                edtLocation.setText(basicTabEntity?.location.toString())
            }
            if(basicTabEntity?.district != null){
                edtDistrict.setText(basicTabEntity?.district.toString())
            }
            if(basicTabEntity?.forestOwner != null){
                edtForestOwner.setText(basicTabEntity?.forestOwner.toString())
            }
            if(basicTabEntity?.forester != null){
                edtForester.setText(basicTabEntity?.forester.toString())
            }
            if(basicTabEntity?.forestry != null){
                edtForestry.setText(basicTabEntity?.forestry.toString())
            }
            if(basicTabEntity?.feller != null){
                edtFeller.setText(basicTabEntity?.feller.toString())
            }
            if(basicTabEntity?.clearer != null){
                edtFeller.setText(basicTabEntity?.clearer.toString())
            }
            if(basicTabEntity?.skidder != null){
                edtSkidder.setText(basicTabEntity?.skidder.toString())
            }
            if(basicTabEntity?.comment != null){
                edtComment.setText(basicTabEntity?.comment.toString())
            }

            toggleFsc.isChecked = basicTabEntity?.fsc ==1
            toggle_pefc.isChecked = basicTabEntity?.pefc ==1

        }


      //  ((activity as LogEntryDetailActivity)).viewModel.insertBasicTabDetail()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        isPaused = true
        Log.d("","");
        txtIosID = edtIosId.text.toString()
        txtStackNR = edtStackNr.text.toString()
        txtForeignNR = edtForeignNr.text.toString()
        txtDate = edtDate.text.toString()
        txtPrice = edtPrice.text.toString().toDouble()
        txtLocation = edtLocation.text.toString()
        txtDistrict = edtDistrict.text.toString()
        txtForestOwner = edtForestOwner.text.toString()
        txtForester = edtForester.text.toString()
        txtForestry = edtForestry.text.toString()
        txtFeller = edtFeller.text.toString()
        txtClearer = edtClearer.text.toString()
        txtSkidder = edtSkidder.text.toString()
        txtComment = edtComment.text.toString()

        var fsc = 0
        if(toggleFsc.isChecked){
            fsc = 1
        }

        var pefc = 0
        if(toggle_pefc.isChecked){
            pefc = 1
        }

        var testabEntity = BasicTabEntity(logId, txtIosID,txtStackNR, txtForeignNR
                , txtDate, txtPrice, fsc, pefc,txtLocation,
                txtDistrict, txtForestOwner, txtForester,
                txtForestry, txtClearer, txtFeller,
                txtSkidder, txtComment,0)


        ((activity as? LogEntryDetailActivity))?.viewModel?.updateBasicTabDetail(testabEntity)
        ((activity as? LogEntryDetailActivity))?.viewModel?.updateLogBasicEntityById(testabEntity, logId)
        ((activity as? LogEntryDetailActivity))?.viewModel?.updateForestOwnerById(edtForestOwner.text.toString(),logId)

    }


    fun onChange(){

        try {


         //   listener.saveToDBDone()

            /* if(basicTabEntity == null){
                 //insert first time in DB
                 ((activity as LogEntryDetailActivity)).viewModel?.insertBasicTabDetail(basicEntity)
             }else{
                 // Already available in DB. Just update
                 ((activity as LogEntryDetailActivity)).viewModel?.updateBasicTabDetail(basicEntity)
             }*/

        }catch(e:Exception){
            e.stackTrace
        }

    }

    interface saveListener{

        fun saveToDBDone()
    }

    override fun onStop() {

        var fsc = 0
        if(toggleFsc.isChecked){
            fsc = 1
        }

        var pefc = 0
        if(toggle_pefc.isChecked){
            pefc = 1
        }

        var testabEntity = BasicTabEntity(logId, edtIosId.text.toString(),edtStackNr.text.toString(),edtForeignNr.text.toString()
                , edtDate.text.toString(), edtPrice.text.toString().toDouble(), fsc,pefc, edtLocation.text.toString(),
                edtDistrict.text.toString(), edtForestOwner.text.toString(), edtForester.text.toString(),
                edtForestry.text.toString(), edtClearer.text.toString(), edtFeller.text.toString(),
                edtSkidder.text.toString(), edtComment.text.toString(),0)


        ((activity as? LogEntryDetailActivity))?.viewModel?.updateBasicTabDetail(testabEntity)
        ((activity as? LogEntryDetailActivity))?.viewModel?.updateLogBasicEntityById(testabEntity, logId)
        ((activity as? LogEntryDetailActivity))?.viewModel?.updateForestOwnerById(edtForestOwner.text.toString(),logId)
        super.onStop()

    }


}// Required empty public constructor