package com.polter.mobipolter.activities

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.media.*
import android.media.audiofx.BassBoost
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.polter.mobipolter.R
import com.polter.mobipolter.activities.adapters.ItemListAdapter
import com.polter.mobipolter.activities.adapters.onItemClick
import com.polter.mobipolter.activities.model.AddLogEntity
import com.polter.mobipolter.activities.model.PredefinedListEntity
import com.polter.mobipolter.activities.model.SpeciesEntity
import com.polter.mobipolter.activities.utility.Utilities
import com.polter.mobipolter.activities.viewmodel.PredefinedListViewModel
import kotlinx.android.synthetic.main.activity_add_log.*
import kotlinx.android.synthetic.main.activity_add_log.edtKind
import kotlinx.android.synthetic.main.activity_add_log.edtLogDiameter
import kotlinx.android.synthetic.main.activity_add_log.edtLogLengthNr
import kotlinx.android.synthetic.main.activity_add_log.edtLogNr
import kotlinx.android.synthetic.main.activity_add_log.edtLogOversize
import kotlinx.android.synthetic.main.activity_add_log.edtLogPlate
import kotlinx.android.synthetic.main.activity_add_log.edtLogQuality
import kotlinx.android.synthetic.main.activity_add_log.edtLogSize
import kotlinx.android.synthetic.main.activity_add_log.edtLogVolumeM3
import kotlinx.android.synthetic.main.activity_add_log.edtSpecies
import kotlinx.android.synthetic.main.activity_add_log.imgBack
import kotlinx.android.synthetic.main.activity_add_log.imgDone
import java.lang.Exception
import android.widget.Toast
import com.google.gson.JsonParser
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.io.Serializable


class AddLogActivity : AppCompatActivity(), View.OnClickListener, onItemClick {

    var viewModel: PredefinedListViewModel? = null
    lateinit var predefinedSpeicesListEntity: ArrayList<PredefinedListEntity>
    lateinit var predefinedKindListEntity: ArrayList<PredefinedListEntity>
    lateinit var predefinedQualityListEntity: ArrayList<PredefinedListEntity>
    lateinit var predefinedSizeListEntity: ArrayList<PredefinedListEntity>


    var mSpeciesAdapter: ItemListAdapter? = null
    var mKindAdapter: ItemListAdapter? = null
    var mQualityAdapter: ItemListAdapter? = null
    var mSizeAdapter: ItemListAdapter? = null
    var currentItemName: String? = null
    var mSpeciesList: List<SpeciesEntity>? = null
    var currentEditext: String? = null
    var mLogList: ArrayList<AddLogEntity>? = null
    var currentLogIndex = 0
    var index = 0
    var mCurrentLogEntity: AddLogEntity? = null
    var mCurrentKindEntity: PredefinedListEntity? = null
    var mCurrentBarkCmValue: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_log)
        currentItemName = getString(R.string.species)
        currentEditext = "Nr"
        var mp:MediaPlayer


        index = intent.getIntExtra("logIndex", 0)

        if (intent.getStringExtra("logKind") != null && !TextUtils.isEmpty(intent.getStringExtra("logKind"))) {
            edtKind.setText(intent.getStringExtra("logKind"))
        }

        if (intent.getSerializableExtra("logList") != null) {
            mLogList = intent.getSerializableExtra("logList") as? ArrayList<AddLogEntity>
            currentLogIndex = mLogList?.size!!
            edtLogNr.setText(intent.getIntExtra("logIndex", 0).toString())
        }

        viewModel = ViewModelProviders.of(this, ViewModelProviders.DefaultFactory(this.application)).get(PredefinedListViewModel::class.java!!)
        predefinedSpeicesListEntity = ArrayList()
        predefinedKindListEntity = ArrayList()
        predefinedQualityListEntity = ArrayList()
        predefinedSizeListEntity = ArrayList()
//        edtLogCount.setText(intent.getIntExtra("logIndex",0).toString())

        //     txtLogSpecies.setOnClickListener(this)
        //      txtLogKind.setOnClickListener(this)
        //     txtLogQuality.setOnClickListener(this)
        //     txtLogSize.setOnClickListener(this)

        if (Build.VERSION.SDK_INT >= 21) {
            edtLogLengthNr.showSoftInputOnFocus = false
            edtLogDiameter.showSoftInputOnFocus = false
            edtLogVolumeM3.showSoftInputOnFocus = false
            edtLogOversize.showSoftInputOnFocus = false
            edtLogSize.showSoftInputOnFocus = false
            edtLogPlate.showSoftInputOnFocus = false
            edtLogNr.showSoftInputOnFocus = false
            edtSpecies.showSoftInputOnFocus = false
            edtKind.showSoftInputOnFocus = false


        } else if (Build.VERSION.SDK_INT >= 11) {
            edtLogLengthNr.setRawInputType(InputType.TYPE_CLASS_NUMBER)
            edtLogLengthNr.setTextIsSelectable(true)
            edtLogDiameter.setRawInputType(InputType.TYPE_CLASS_NUMBER)
            edtLogDiameter.setTextIsSelectable(true)
            edtLogVolumeM3.setRawInputType(InputType.TYPE_CLASS_NUMBER)
            edtLogVolumeM3.setTextIsSelectable(true)
            edtLogPlate.setRawInputType(InputType.TYPE_CLASS_NUMBER)
            edtLogPlate.setTextIsSelectable(true)
            edtLogOversize.setRawInputType(InputType.TYPE_CLASS_NUMBER)
            edtLogOversize.setTextIsSelectable(true)
            edtLogSize.setRawInputType(InputType.TYPE_CLASS_TEXT)
            edtLogSize.setTextIsSelectable(true)
            edtLogNr.setRawInputType(InputType.TYPE_CLASS_NUMBER)
            edtLogNr.setTextIsSelectable(true)
            edtSpecies.setRawInputType(InputType.TYPE_CLASS_NUMBER)
            edtSpecies.setTextIsSelectable(true)
            edtKind.setRawInputType(InputType.TYPE_CLASS_NUMBER)
            edtKind.setTextIsSelectable(true)
        } else {
            edtLogLengthNr.setRawInputType(InputType.TYPE_NULL)
            edtLogLengthNr.isFocusable = true
            edtLogDiameter.setRawInputType(InputType.TYPE_NULL)
            edtLogDiameter.isFocusable = true
            edtLogVolumeM3.setRawInputType(InputType.TYPE_NULL)
            edtLogVolumeM3.isFocusable = true
            edtLogPlate.setRawInputType(InputType.TYPE_NULL)
            edtLogPlate.isFocusable = true
            edtLogOversize.setRawInputType(InputType.TYPE_NULL)
            edtLogOversize.isFocusable = true
            edtLogSize.setRawInputType(InputType.TYPE_NULL)
            edtLogSize.isFocusable = true
            edtLogNr.setRawInputType(InputType.TYPE_NULL)
            edtLogNr.isFocusable = true
            edtSpecies.setRawInputType(InputType.TYPE_NULL)
            edtSpecies.isFocusable = true
            edtKind.setRawInputType(InputType.TYPE_NULL)
            edtKind.isFocusable = true

        }

        rvSpecies.layoutManager = GridLayoutManager(this, 2) as RecyclerView.LayoutManager?
        rvKind.layoutManager = GridLayoutManager(this, 2) as RecyclerView.LayoutManager?
        rvQuality.layoutManager = GridLayoutManager(this, 2) as RecyclerView.LayoutManager?
        rvSize.layoutManager = GridLayoutManager(this, 2) as RecyclerView.LayoutManager?

        //   rvSpecies.addItemDecoration(DividerItemDecorations(ContextCompat.getDrawable(getApplicationContext(),
        //         R.drawable.item_decorator)!!))
        edtLogLengthNr.setOnTouchListener { arg0, arg1 ->
            currentEditext = "Length"
            false
        }

        edtLogDiameter.setOnTouchListener { arg0, arg1 ->
            currentEditext = "Diameter"
            false
        }

        edtLogPlate.setOnTouchListener { arg0, arg1 ->
            currentEditext = "Plate"
            false
        }

        edtLogVolumeM3.setOnTouchListener { arg0, arg1 ->
            currentEditext = "Volume"
            false
        }

        edtLogOversize.setOnTouchListener { arg0, arg1 ->
            currentEditext = "Oversize"
            false
        }

        edtLogSize.setOnTouchListener { arg0, arg1 ->
            currentEditext = "Klasse"
            false
        }

        edtLogNr.setOnTouchListener { arg0, arg1 ->
            currentEditext = "Nr"
            false
        }



        viewModel?.allSpecies?.observe(this, Observer<List<SpeciesEntity>> { speciesListDB ->

            mSpeciesList = speciesListDB!!
            if (mSpeciesList!!.size > 0) {
                if (currentItemName.equals(getString(R.string.species))) {
                    //   itemTitle.text = getString(R.string.species)
                    //   itemName.text = getString(R.string.species)


                    // val myArrayList = resources.getStringArray(R.array.species_list)
                    if (mSpeciesList == null)
                        mSpeciesList = ArrayList()




                    for (speciesEntity: SpeciesEntity in mSpeciesList!!) {

                        val predefinedEntity = PredefinedListEntity(speciesEntity.speciesAbbr,
                                speciesEntity.speciesName, speciesEntity.speciesLatName,
                                getString(R.string.species))

                        predefinedSpeicesListEntity.add(predefinedEntity)
                    }

                    if (mSpeciesAdapter == null) {
                        mSpeciesAdapter = ItemListAdapter(predefinedSpeicesListEntity, true, getString(R.string.species), this, this)
                        rvSpecies.adapter = mSpeciesAdapter
                    } else {
                        mSpeciesAdapter!!.notifyDataSetChanged()
                        rvSpecies.scrollToPosition(mSpeciesList!!.size - 1)
                    }

                }

            }

        })

        /*  val kindList = resources.getStringArray(R.array.kind_list)
          for(name in kindList){

              val predefinedEntity = PredefinedListEntity(name,"","",getString(R.string.kind))
              predefinedKindListEntity.add(predefinedEntity)
          }
          mKindAdapter = ItemListAdapter(predefinedKindListEntity,true,getString(R.string.kind),this,this)
          rvKind.adapter = mKindAdapter*/

        val qualityList = resources.getStringArray(R.array.quality_list)
        for (name in qualityList) {

            val predefinedEntity = PredefinedListEntity(name, "", "", getString(R.string.quality))
            predefinedQualityListEntity.add(predefinedEntity)
        }
        mQualityAdapter = ItemListAdapter(predefinedQualityListEntity, true, getString(R.string.quality), this, this)
        rvQuality.adapter = mQualityAdapter
        rvQuality.hasFixedSize()


        val sizeList = resources.getStringArray(R.array.size_list)
        for (name in sizeList) {

            val predefinedEntity = PredefinedListEntity(name, "", "", getString(R.string.size))
            predefinedSizeListEntity.add(predefinedEntity)
        }
        mSizeAdapter = ItemListAdapter(predefinedSizeListEntity, true, getString(R.string.size), this, this)
        rvSize.adapter = mSizeAdapter


        imgDone.setOnClickListener {


            if (mCurrentKindEntity != null && !mCurrentKindEntity!!.isKindSpecial) {

                if (TextUtils.isEmpty(edtLogLengthNr.text.toString())
                        && TextUtils.isEmpty(edtLogDiameter.text.toString())) {
                    edtLogLengthNr.setError(getString(R.string.alert_field_required))
                    edtLogDiameter.setError(getString(R.string.alert_field_required))
                    return@setOnClickListener


                }

                if (TextUtils.isEmpty(edtLogLengthNr.text.toString())) {
                    edtLogLengthNr.setError(getString(R.string.alert_field_required))
                    return@setOnClickListener
                }

                if (TextUtils.isEmpty(edtLogDiameter.text.toString())) {
                    edtLogDiameter.setError(getString(R.string.alert_field_required))
                    return@setOnClickListener
                }
            }

            if (TextUtils.isEmpty(edtSpecies.text.toString())) {
                edtSpecies.setError(getString(R.string.alert_field_required))
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(edtLogPlate.text.toString())) {
                edtLogPlate.setError(getString(R.string.alert_field_required))
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(edtKind.text.toString())) {
                edtKind.setError(getString(R.string.alert_field_required))
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(edtLogQuality.text.toString())) {
                edtLogQuality.setError(getString(R.string.alert_field_required))
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(edtLogVolumeM3.text.toString())) {
                edtLogVolumeM3.setError(getString(R.string.alert_field_required))
                return@setOnClickListener
            }


            try {

                val addLogEntity = AddLogEntity(index,
                        edtLogNr.text.toString().toInt(),
                        edtLogPlate.text.toString().toInt(),
                        Utilities.inputTextValidation(edtSpecies.text.toString()),
                        Utilities.inputTextValidation(edtKind.text.toString()),
                        Utilities.inputNumberValidation(edtLogLengthNr.text.toString()),
                        Utilities.inputNumberValidation(edtLogOversize.text.toString()),
                        Utilities.inputNumberValidation(edtLogDiameter.text.toString()),
                        Utilities.inputTextValidation(edtLogQuality.text.toString()),
                        Utilities.inputTextValidation(edtLogSize.text.toString()),
                        Utilities.inputNumberValidation(edtLogVolumeM3.text.toString()),
                        mCurrentBarkCmValue,false)

                mCurrentBarkCmValue = 0.0


                if (!intent.getBooleanExtra("isFromModify", false)) {
                    index++
                    txtAddLogLength.setText(edtLogLengthNr.text.toString())
                    txtAddLogDm.setText(edtLogDiameter.text.toString())
                    txtAddLogVolume.setText(edtLogVolumeM3.text.toString())
                    txtAddLogOversize.setText(edtLogOversize.text.toString())
                    txtAddLogSize.setText(edtLogSize.text.toString())

                    var nrCount = Utilities.inputIntValidation(edtLogNr.text.toString())
                    nrCount++
                    edtLogNr.setText(nrCount.toString())

                    var plateCount = Utilities.inputIntValidation(edtLogPlate.text.toString())
                    plateCount++
                    edtLogPlate.setText(plateCount.toString())

                    //  edtLogLengthNr.text.clear()
                    edtLogDiameter.text.clear()
                    edtLogSize.text.clear()
                    edtLogVolumeM3.text.clear()
                    edtLogOversize.text.clear()
                    mLogList?.add(addLogEntity)
                    currentLogIndex++
                    currentEditext = "Diameter"
                    edtLogDiameter.requestFocus()
                } else {
                    mCurrentLogEntity = addLogEntity
                    edtLogNr.setText(mCurrentLogEntity?.log_nr.toString())
                    val intent = Intent()
                    intent.putExtra("modifiedEntity", mCurrentLogEntity!! as Serializable)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }

//
//                var builder = NotificationCompat.Builder(this)
//                val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
//                val r = RingtoneManager.getRingtone(getApplicationContext(), notification)
//                r.play()


                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(500)
                val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150)

                /* val intent = Intent()
                 intent.putExtra("AddLogEntity",addLogEntity)
                 setResult(Activity.RESULT_OK,intent)
                 finish()*/
            } catch (e: Exception) {
                e.printStackTrace()
                //   val intent = Intent()
                //  setResult(Activity.RESULT_CANCELED,intent)
                //   finish()
            }

        }

        imgBack.setOnClickListener {

            if (!intent.getBooleanExtra("isFromModify", false)) {
                val intent = Intent()
                intent.putExtra("logList", mLogList as Serializable)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                val intent = Intent()
                intent.putExtra("modifiedEntity", mCurrentLogEntity as Serializable)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

        }



        edtLogLengthNr.addTextChangedListener(lengthEditor)
        edtLogDiameter.addTextChangedListener(diameterEditor)

        imgSpecieTool.setOnClickListener {


            startActivityForResult(Intent(this, ItemListingActivity::class.java)
                    .putExtra(Utilities.CURRENT_ITEM_NAME, getString(R.string.species))
                    , Utilities.ITEM_LIST_REQ_CODE)
        }

        llKind.setOnClickListener {


            startActivityForResult(Intent(this, ItemListingActivity::class.java)
                    .putExtra(Utilities.CURRENT_ITEM_NAME, getString(R.string.kind))
                    , Utilities.ITEM_LIST_REQ_CODE)
        }



        txt0.setOnClickListener(this)
        txt1.setOnClickListener(this)
        txt2.setOnClickListener(this)
        txt3.setOnClickListener(this)
        txt4.setOnClickListener(this)
        txt5.setOnClickListener(this)
        txt6.setOnClickListener(this)
        txt7.setOnClickListener(this)
        txt8.setOnClickListener(this)
        txt9.setOnClickListener(this)
        txtDot.setOnClickListener(this)
        txtAddLogDone.setOnClickListener(this)
        imgDel.setOnClickListener(this)
        imgShowPrevLog.setOnClickListener(this)
        imgShowNextLog.setOnClickListener(this)
        edtSpecies.addTextChangedListener(speciesEditor)
        toggleBark.isChecked = true

        // Set an checked change listener for switch button
        toggleBark.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // The switch is enabled/checked
                val radius = (inputNumberValidation(edtLogDiameter.text.toString()) / 2) / 100
                val volumeM3 = 3.14 * radius * radius * inputNumberValidation(edtLogLengthNr.text.toString())
                val digit = Math.round(volumeM3 * 1000.0).toDouble() / 1000.0

                Log.d("volume value", "digit" + digit)
                // val formatString = String.format("%.3f", digit)
                edtLogVolumeM3.setText(digit.toString())

                try {
                    calculateVolumeUsingBark()
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            } else {

                val radius = (inputNumberValidation(edtLogDiameter.text.toString()) / 2) / 100
                val volumeM3 = 3.14 * radius * radius * inputNumberValidation(edtLogLengthNr.text.toString())
                val digit = Math.round(volumeM3 * 1000.0).toDouble() / 1000.0
                // val formatString = String.format("%.3f", digit)
                edtLogVolumeM3.setText(digit.toString())
                mCurrentBarkCmValue = 0.0
            }
        }


        if (intent.getBooleanExtra("isFromModify", false)) {

            edtLogNr.isEnabled = false
            edtLogNr.setBackgroundResource(R.drawable.bg_disabled)
            imgShowNextLog.visibility = View.INVISIBLE
            imgShowPrevLog.visibility = View.INVISIBLE
            txtTitle.text = "Edit Log"
            val currentEntity = intent.getSerializableExtra("modifiedEntity") as AddLogEntity
            mCurrentLogEntity = currentEntity
            edtLogNr.setText(mCurrentLogEntity!!.log_nr.toString())
            edtSpecies.setText(mCurrentLogEntity?.species)
            edtKind.setText(mCurrentLogEntity?.kind)
            edtLogQuality.setText(mCurrentLogEntity?.quality)
            edtLogLengthNr.setText(mCurrentLogEntity?.length_m.toString())
            edtLogDiameter.setText(mCurrentLogEntity?.diameter_cm.toString())
            edtLogVolumeM3.setText(mCurrentLogEntity?.volume_m3.toString())
            edtLogOversize.setText(mCurrentLogEntity?.oversize_m.toString())
            edtLogPlate.setText(mCurrentLogEntity?.plate.toString())
            edtLogSize.setText(mCurrentLogEntity?.klasse.toString())
            currentEditext = "Plate"

        } else {

            edtLogNr.isEnabled = true
            edtLogNr.setBackgroundResource(R.drawable.bg_dialog)
            imgShowNextLog.visibility = View.VISIBLE
            imgShowPrevLog.visibility = View.VISIBLE
            txtTitle.text = "Add Log"
        }


    }

    override fun onBackPressed() {

        if (!intent.getBooleanExtra("isFromModify", false)) {
            val intent = Intent()
            intent.putExtra("logList", mLogList as Serializable)
            setResult(Activity.RESULT_OK, intent)
        } else {
            val intent = Intent()
            intent.putExtra("modifiedEntity", mCurrentLogEntity!! as Serializable)
            setResult(Activity.RESULT_OK, intent)
        }

        super.onBackPressed()
    }

    fun inputNumberValidation(str: String): Double {

        try {
            if (TextUtils.isEmpty(str)) {
                return 0.00
            } else {
                return str.toDouble()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return 0.00
        }

    }

    fun inputTextValidation(str: String): String? {

        try {
            if (TextUtils.isEmpty(str)) {
                return null
            } else {
                return str
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.txtLogSpecies -> {
                startActivityForResult(Intent(this, ItemListingActivity::class.java)
                        .putExtra(Utilities.CURRENT_ITEM_NAME, getString(R.string.species))
                        , Utilities.ITEM_LIST_REQ_CODE)
            }
            R.id.txtLogKind -> {
                startActivityForResult(Intent(this, ItemListingActivity::class.java)
                        .putExtra(Utilities.CURRENT_ITEM_NAME, getString(R.string.kind))
                        , Utilities.ITEM_LIST_REQ_CODE)
            }
            R.id.txtLogQuality -> {
                startActivityForResult(Intent(this, ItemListingActivity::class.java)
                        .putExtra(Utilities.CURRENT_ITEM_NAME, getString(R.string.quality))
                        , Utilities.ITEM_LIST_REQ_CODE)
            }
            R.id.txtLogSize -> {
                startActivityForResult(Intent(this, ItemListingActivity::class.java)
                        .putExtra(Utilities.CURRENT_ITEM_NAME, getString(R.string.size))
                        , Utilities.ITEM_LIST_REQ_CODE)
            }
            R.id.txt0 -> {
                setValue("0")
            }
            R.id.txt1 -> {
                setValue("1")
            }
            R.id.txt2 -> {
                setValue("2")
            }
            R.id.txt3 -> {
                setValue("3")
            }
            R.id.txt4 -> {
                setValue("4")
            }
            R.id.txt5 -> {
                setValue("5")
            }
            R.id.txt6 -> {
                setValue("6")
            }
            R.id.txt7 -> {
                setValue("7")
            }
            R.id.txt8 -> {
                setValue("8")
            }
            R.id.txt9 -> {
                setValue("9")
            }
            R.id.txtDot -> {
                setValue(".")
            }
            R.id.imgDel -> {
                deleteText()
            }
            R.id.txtAddLogDone -> {

                if (!intent.getBooleanExtra("isFromModify", false)) {
                    val intent = Intent()
                    intent.putExtra("logList", mLogList as Serializable)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    val intent = Intent()
                    intent.putExtra("modifiedEntity", mCurrentLogEntity as Serializable)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }

            }
            R.id.imgShowPrevLog -> {

                // edtLogLengthNr.text.clear()
                edtLogDiameter.text.clear()
                edtLogSize.text.clear()
                edtLogVolumeM3.text.clear()
                edtLogOversize.text.clear()



                if (currentLogIndex - 1 > -1) {
                    // imgBackwd.visibility = View.VISIBLE
                    val addLogEntity = mLogList?.get(currentLogIndex - 1)
                    currentLogIndex = currentLogIndex - 1
                    txtAddLogLength.setText(addLogEntity?.length_m.toString())
                    txtAddLogDm.setText(addLogEntity?.diameter_cm.toString())
                    txtAddLogVolume.setText(addLogEntity?.volume_m3.toString())
                    txtAddLogOversize.setText(addLogEntity?.oversize_m.toString())
                    txtAddLogSize.setText(addLogEntity?.klasse.toString())
                    edtLogNr.setText(addLogEntity?.log_nr.toString())
                    edtLogQuality.setText(addLogEntity?.quality)
                    edtLogPlate.setText(addLogEntity?.plate.toString())
                    if (addLogEntity?.species != null)
                        edtSpecies.setText(addLogEntity?.species.toString())
                    if (addLogEntity?.kind != null)
                        edtKind.setText(addLogEntity?.kind.toString())

                } else {
                    Toast.makeText(this, getString(R.string.no_more_prev_stack), Toast.LENGTH_SHORT).show()
                    //imgBackwd.visibility = View.GONE
                }

            }
            R.id.imgShowNextLog -> {

                //  edtLogLengthNr.text.clear()
                edtLogDiameter.text.clear()
                edtLogSize.text.clear()
                edtLogVolumeM3.text.clear()
                edtLogOversize.text.clear()


                if (currentLogIndex + 1 < mLogList?.size!!) {
                    //   imgForwd.visibility = View.VISIBLE
                    val addLogEntity = mLogList?.get(currentLogIndex + 1)
                    currentLogIndex = currentLogIndex + 1
                    if (addLogEntity != null) {
                        txtAddLogLength.setText(addLogEntity?.length_m.toString())
                        txtAddLogDm.setText(addLogEntity?.diameter_cm.toString())
                        txtAddLogVolume.setText(addLogEntity?.volume_m3.toString())
                        txtAddLogOversize.setText(addLogEntity?.oversize_m.toString())
                        if (addLogEntity?.kind != null)
                            txtAddLogSize.setText(addLogEntity?.klasse.toString())
                        edtLogNr.setText(addLogEntity?.log_nr.toString())
                        edtLogPlate.setText(addLogEntity?.plate.toString())
                        if (addLogEntity?.species != null)
                            edtSpecies.setText(addLogEntity?.species.toString())
                        if (addLogEntity?.kind != null)
                            edtKind.setText(addLogEntity?.kind.toString())
                        if (addLogEntity?.quality!=null)
                        edtLogQuality.setText(addLogEntity?.quality)
                    }

                } else {
                    // edtLogLengthNr.text.clear()
                    edtLogDiameter.text.clear()
                    edtLogSize.text.clear()
                    edtLogVolumeM3.text.clear()
                    edtLogOversize.text.clear()
                    edtLogQuality.text.clear()
                    Toast.makeText(this, getString(R.string.no_more_next_stack), Toast.LENGTH_SHORT).show()
                    //  imgForwd.visibility = View.GONE
                }//

            }
            else -> {
                // else condition
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Utilities.ITEM_LIST_REQ_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                val currentItemName = data?.getStringExtra(Utilities.CURRENT_ITEM_NAME)
                val currentItemValue = data?.getStringExtra(Utilities.CURRENT_ITEM_VALUE)

                if (currentItemName.equals(getString(R.string.species))) {
                    edtSpecies.setText(currentItemValue)
                    //  includeLogsSurvey.edtSpecies.setText(data?.getStringExtra(Utilities.CURRENT_ITEM_VALUE))
                } else if (currentItemName.equals(getString(R.string.kind))) {
                    mCurrentKindEntity = data?.getSerializableExtra((Utilities.CURRENT_KIND_ENTITY)) as PredefinedListEntity
                    edtKind.setText(currentItemValue)

                } else if (currentItemName.equals(getString(R.string.quality))) {
                    edtLogQuality.setText(currentItemValue)
                } else {
                    edtLogSize.setText(currentItemValue)
                    // includeSectionSurvey.edtSectionQuality.setText(currentItemValue)
                    // includeEstimationSurvey.edt.setText(currentItemValue)
                }

            }
        }
    }


    private val speciesEditor = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            if (TextUtils.isEmpty(s)) {
                //edtLogLengthNr.setText("0")
                return
            }


            /*if(toggleBark.isChecked){

                try{
                    calculateVolumeUsingBark()
                }catch(e: Exception){
                    e.printStackTrace()
                }

            }*/

            if (inputNumberValidation(edtLogDiameter.text.toString()) != 0.0
                    && inputNumberValidation(edtLogLengthNr.text.toString()) != 0.0) {

                val radius = (inputNumberValidation(edtLogDiameter.text.toString()) / 2) / 100
                val volumeM3 = 3.14 * (radius * radius) * inputNumberValidation(edtLogLengthNr.text.toString())
                val digit = Math.round(volumeM3 * 1000.0).toDouble() / 1000.0
                //  val formatString = String.format("%.3f", digit)
                edtLogVolumeM3.setText(digit.toString())

                if (toggleBark.isChecked) {
                    try {
                        calculateVolumeUsingBark()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

            }

        }

        override fun afterTextChanged(etValue: Editable?) {


        }
    }

    private val lengthEditor = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            if (TextUtils.isEmpty(s)) {
                //edtLogLengthNr.setText("0")
                edtLogVolumeM3.text.clear()
                return
            }


            if (inputNumberValidation(edtLogDiameter.text.toString()) != 0.0
                    && inputNumberValidation(s.toString()) != 0.0) {

                val radius = (inputNumberValidation(edtLogDiameter.text.toString()) / 2) / 100
                val volumeM3 = 3.14 * (radius * radius) * inputNumberValidation(s.toString())
                val digit = Math.round(volumeM3 * 1000.0).toDouble() / 1000.0
                //  val formatString = String.format("%.3f", digit)
                edtLogVolumeM3.setText(digit.toString())

                if (toggleBark.isChecked) {
                    try {
                        calculateVolumeUsingBark()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

            }

        }

        override fun afterTextChanged(etValue: Editable?) {


        }
    }

    private val diameterEditor = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            if (TextUtils.isEmpty(s)) {
                //edtLogDiameter.setText("0")
                edtLogVolumeM3.text.clear()
                return
            }

            if (inputNumberValidation(edtLogLengthNr.text.toString()) != 0.0
                    && inputNumberValidation(s.toString()) != 0.0) {

                val radius = (inputNumberValidation(s.toString()) / 2) / 100
                val volumeM3 = 3.14 * radius * radius * inputNumberValidation(edtLogLengthNr.text.toString())
                val digit = Math.round(volumeM3 * 1000.0).toDouble() / 1000.0
                // val formatString = String.format("%.3f", digit)
                edtLogVolumeM3.setText(digit.toString())

                if (toggleBark.isChecked) {
                    try {
                        calculateVolumeUsingBark()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                /*if(!toggleBark.isChecked){
                    val radius = (inputNumberValidation(s.toString())/2)/100
                    val volumeM3 = 3.14 * radius * radius * inputNumberValidation(edtLogLengthNr.text.toString())
                    val digit = Math.round(volumeM3 * 1000.0).toDouble() / 1000.0
                    // val formatString = String.format("%.3f", digit)
                    edtLogVolumeM3.setText(digit.toString())
                }else{

                    try{

                        val radius = (inputNumberValidation(s.toString())/2)/100
                        val volumeM3 = 3.14 * radius * radius * inputNumberValidation(edtLogLengthNr.text.toString())
                        val digit = Math.round(volumeM3 * 1000.0).toDouble() / 1000.0
                        // val formatString = String.format("%.3f", digit)
                        edtLogVolumeM3.setText(digit.toString())
                        calculateVolumeUsingBark()
                    }catch(e: Exception){
                        e.printStackTrace()
                    }


                }*/


            }

            val diameter = inputNumberValidation(edtLogDiameter.text.toString())
            /* if(diameter in 15..19){
                 txtAddLogSize.setText("1b")
             }else if(diameter in 20..24){
                 txtAddLogSize.setText("2a")
             }else if(diameter in 25..29){
                 txtAddLogSize.setText("2b")
             }else if(diameter in 30..39){
                 txtAddLogSize.setText("3")
             }else if(diameter in 40..49){
                 txtAddLogSize.setText("3")
             }*/

            when (diameter) {
                in 0.00..9.9 -> edtLogSize.setText("0")
                in 10.00..14.9 -> edtLogSize.setText("1a")
                in 15.00..19.9 -> edtLogSize.setText("1b")
                in 20.00..24.9 -> edtLogSize.setText("2a")
                in 25.00..29.9 -> edtLogSize.setText("2b")
                in 30.00..34.9 -> edtLogSize.setText("3a")
                in 35.00..39.9 -> edtLogSize.setText("3b")
                in 40.00..44.9 -> edtLogSize.setText("4a")
                in 45.00..49.9 -> edtLogSize.setText("4b")
                in 50.00..54.9 -> edtLogSize.setText("5a")
                in 55.00..59.9 -> edtLogSize.setText("5b")
                in 60.00..69.9 -> edtLogSize.setText("6")
                in 70.00..999.00 -> edtLogSize.setText("6+")
            }

        }

        override fun afterTextChanged(etValue: Editable?) {


        }


    }

    override fun itemClick(entity: PredefinedListEntity) {
        System.out.println("Log " + entity.itemAbbr)
        if (entity.itemType.equals(getString(R.string.species))) {
            edtSpecies.setText(entity.itemAbbr)
        } else if (entity.itemType.equals(getString(R.string.kind))) {
            edtKind.setText(entity.itemLatName)
        } else if (entity.itemType.equals(getString(R.string.quality))) {
            edtLogQuality.setText(entity.itemAbbr)
        } else {
            edtLogSize.setText(entity.itemAbbr)
        }
    }


    fun setValue(s: String) {

        if (currentEditext.equals("Length")) {
            val value = edtLogLengthNr.text.toString() + s
            edtLogLengthNr.setText(value)
            edtLogLengthNr.setSelection(edtLogLengthNr.text.length)
        } else if (currentEditext.equals("Diameter")) {
            val value = edtLogDiameter.text.toString() + s
            edtLogDiameter.setText(value)
            edtLogDiameter.setSelection(edtLogDiameter.text.length)
        } else if (currentEditext.equals("Plate")) {
            val value = edtLogPlate.text.toString() + s
            edtLogPlate.setText(value)
            edtLogPlate.setSelection(edtLogPlate.text.length)
        } else if (currentEditext.equals("Oversize")) {
            val value = edtLogOversize.text.toString() + s
            edtLogOversize.setText(value)
            edtLogOversize.setSelection(edtLogOversize.text.length)
        } else if (currentEditext.equals("Volume")) {
            val value = edtLogVolumeM3.text.toString() + s
            edtLogVolumeM3.setText(value)
            edtLogVolumeM3.setSelection(edtLogVolumeM3.text.length)
        } else if (currentEditext.equals("Klasse")) {
            val value = edtLogSize.text.toString() + s
            edtLogSize.setText(value)
            edtLogSize.setSelection(edtLogSize.text.length)
        } else if (currentEditext.equals("Nr")) {
            val value = edtLogNr.text.toString() + s
            edtLogNr.setText(value)
            edtLogNr.setSelection(edtLogNr.text.length)
        }
    }

    fun deleteText() {

        if (currentEditext.equals("Length")) {
            val length = edtLogLengthNr.getText().length
            if (length > 0) {
                edtLogLengthNr.getText().delete(length - 1, length)
                edtLogLengthNr.setSelection(edtLogLengthNr.text.length)
            }


        } else if (currentEditext.equals("Diameter")) {
            val length = edtLogDiameter.getText().length
            if (length > 0) {
                edtLogDiameter.getText().delete(length - 1, length)
                edtLogDiameter.setSelection(edtLogDiameter.text.length)
            }

        } else if (currentEditext.equals("Volume")) {
            val length = edtLogVolumeM3.getText().length
            if (length > 0) {
                edtLogVolumeM3.getText().delete(length - 1, length)
                edtLogVolumeM3.setSelection(edtLogVolumeM3.text.length)
            }

        } else if (currentEditext.equals("Plate")) {
            val length = edtLogPlate.getText().length
            if (length > 0) {
                edtLogPlate.getText().delete(length - 1, length)
                edtLogPlate.setSelection(edtLogPlate.text.length)
            }

        } else if (currentEditext.equals("Oversize")) {
            val length = edtLogOversize.getText().length
            if (length > 0) {
                edtLogOversize.getText().delete(length - 1, length)
                edtLogOversize.setSelection(edtLogOversize.text.length)
            }

        } else if (currentEditext.equals("Klasse")) {
            val length = edtLogSize.getText().length
            if (length > 0) {
                edtLogSize.getText().delete(length - 1, length)
                edtLogSize.setSelection(edtLogSize.text.length)
            }

        } else if (currentEditext.equals("Nr")) {
            val length = edtLogNr.getText().length
            if (length > 0) {
                edtLogNr.getText().delete(length - 1, length)
                edtLogNr.setSelection(edtLogNr.text.length)
            }

        }
    }


    fun readJSONFromAsset(): String? {
        var json: String? = null
        try {
            val inputStream: InputStream = assets.open("bark.json")
            val parser = JsonParser()
            //  json = parser.parse(InputStreamReader(inputStream)).asJsonArray.toString()
            json = inputStream.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    /* @RequiresApi(api = Build.VERSION_CODES.KITKAT)
     fun getJSONResource(context : Context) : String{
     try (val iss = context.getAssets().open("resource.json")) {
         val parser = JsonParser()
         return parser.parse(new InputStreamReader(is)).getAsJsonObject();
     } catch(e: Exception) {
        // Log.e(TAG, e.getMessage(), e);
     }
     return null;
 }*/
    fun calculateVolumeUsingBark() {

        if (edtSpecies.text.toString() != null && !TextUtils.isEmpty(edtSpecies.text.toString())
                && edtLogDiameter.text.toString() != null && !TextUtils.isEmpty(edtLogDiameter.text.toString())) {


            val json = JSONArray(readJSONFromAsset())

            if (json != null) {
                var b: Boolean = false
                for (i in 0..(json.length() - 1)) {
                    val item = json.getJSONObject(i)
                    //   Log.d("******",""+item.keys().toString())
                    val keys = item.keys()

                    val diameterValue = item.getInt("M")



                    if (diameterValue.toDouble() == edtLogDiameter.text.toString().toDouble()) {

                        for (key in keys) {
                            Log.d("******", key)


                            if (edtSpecies.text.toString().toUpperCase().equals(key)) {
                                b = true
                                break
                            }


                        }

                        if (!b)
                        {
                            showError.error = getString(R.string.alert_field_required)
                            toggleBark.isChecked = false
                        }

                        else
                        {
                            showError.error = null
                        }
//                       if(keys.contains(edtSpecies.text.toString().toUpperCase())){
//
//                       }

                        val speicesValue = item.getString(edtSpecies.text.toString().toUpperCase())


                        System.out.println("Bark cm" + speicesValue)
                        mCurrentBarkCmValue = speicesValue.toDouble()
                        val updatedDiameter = edtLogDiameter.text.toString().toDouble() - speicesValue.toDouble()
                        val radius = (updatedDiameter / 2) / 100
                        val volumeM3 = 3.14 * radius * radius * inputNumberValidation(edtLogLengthNr.text.toString())
                        val digit = Math.round(volumeM3 * 1000.0).toDouble() / 1000.0
                        // val formatString = String.format("%.3f", digit)
                        edtLogVolumeM3.setText(digit.toString())
                    }
                    // Your code here
                }
            }
        }

    }


}