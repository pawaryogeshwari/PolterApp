package com.polter.mobipolter.activities.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.polter.mobipolter.R
import com.polter.mobipolter.activities.LogEntryDetailActivity
import com.polter.mobipolter.activities.SelectSurveyingActivity
import com.polter.mobipolter.activities.adapters.AddLogEntityAdapter
import com.polter.mobipolter.activities.adapters.HeightEntryAdapter
import com.polter.mobipolter.activities.model.*
import com.polter.mobipolter.activities.utility.SwipeController
import com.polter.mobipolter.activities.utility.SwipeControllerActions
import com.polter.mobipolter.activities.utility.Utilities
import kotlinx.android.synthetic.main.fragment_measurement.*
import kotlinx.android.synthetic.main.layout_estimation_survey.view.*
import kotlinx.android.synthetic.main.layout_estimation_survey.view.edtKind
import kotlinx.android.synthetic.main.layout_estimation_survey.view.edtLength
import kotlinx.android.synthetic.main.layout_estimation_survey.view.edtQuality
import kotlinx.android.synthetic.main.layout_estimation_survey.view.edtSpecies
import kotlinx.android.synthetic.main.layout_estimation_survey.view.edtWidth
import kotlinx.android.synthetic.main.layout_logs_survey.*
import kotlinx.android.synthetic.main.layout_section_survey.*
import kotlinx.android.synthetic.main.layout_section_survey.view.*
import kotlinx.android.synthetic.main.layout_logs_survey.view.*
import android.support.v7.widget.DividerItemDecoration
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.polter.mobipolter.activities.AddLogActivity
import com.polter.mobipolter.activities.ItemListingActivity
import com.polter.mobipolter.activities.adapters.SizeClassEntityAdapter
import com.polter.mobipolter.activities.adapters.onLogItemClick
import com.polter.mobipolter.activities.viewmodel.ExportLogStackActivity
import kotlinx.android.synthetic.main.layout_estimation_survey.*
import kotlinx.android.synthetic.main.layout_logdetail_item.*
import java.lang.Exception


class MeasurementFragment : Fragment(), View.OnClickListener, onLogItemClick {


    var list : ArrayList<MeasureHeightListEntity> ? = null
    var heightEntryAdapter : HeightEntryAdapter ? = null
    var logId : Int = 0
    var measurementTabEntity : MeasurementTabEntity ? = null
    var selectedSurveyType : String ? = null

    // Add Log Entity Adapter
    var addLogAdapter : AddLogEntityAdapter ?= null
    lateinit var addLogList : ArrayList<AddLogEntity>

    // Size Class Entity Adapter
    var sizeClassAdapter : SizeClassEntityAdapter ?= null
    lateinit var sizeClassList : ArrayList<SizeClassEntity>
    internal val REQ_CODE_GET_ADDLOG_ENTITY = 105
    internal val REQ_CODE_MODIFY_LOG_ENTITY = 106
    var heightSectionValue = 0.00
    var quota = 100.00
    var kind : String ?= null
    var mCurrentEntityIndex = -1



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_measurement, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        logId =(this.activity as LogEntryDetailActivity).logID
        addLogList = ArrayList()
        sizeClassList = ArrayList()
        list = ArrayList()

        txtexportPolter.setOnClickListener {

            val intent = Intent(activity,ExportLogStackActivity::class.java)
            startActivity(intent)

        }

        selectedSurveyType = getString(R.string.logs_surveying)

        rvAddLog.layoutManager = LinearLayoutManager(activity) as RecyclerView.LayoutManager?
//        rvAddLog.layoutManager = LinearLayoutManager(activity) as RecyclerView.LayoutManager?
        rvLogSize.layoutManager = LinearLayoutManager(activity) as RecyclerView.LayoutManager?

     //   val addLogEntity = AddLogEntity(1,1,1,"Spec","kin",1.00,
     //           2.00,1.00,"qua",2.00,2.50,
      //          0,2.00,1)
     //   addLogList.add(addLogEntity)

    //    addLogList.add(addLogEntity)

      //  val sizeClassEntity = SizeClassEntity("L0B",2,20.00,2.33)
      //  sizeClassList.add(sizeClassEntity)
      //  sizeClassList.add(sizeClassEntity)

        sizeClassAdapter = SizeClassEntityAdapter(sizeClassList,activity!!)
        rvLogSize.adapter = sizeClassAdapter


        measurementTabEntity = ((activity as LogEntryDetailActivity)).viewModel.findLogMeasureTabById(logId)

        if(measurementTabEntity == null)
        {

          //  list = ArrayList()

            val newMeasurementEntity = MeasurementTabEntity(logId,getString(R.string.section_surveying),
                                        null,null, null,0,0.0,0.0,0.0,0)
            ((activity as LogEntryDetailActivity)).viewModel.insertMeasureTabDetail(newMeasurementEntity)
        }else{

            // Set recent surveying type
            if(measurementTabEntity?.surveyingType != null){
                txtSurveyType.text = measurementTabEntity?.surveyingType
                showSurveyingTypeData()

            }else{
                txtSurveyType.text = selectedSurveyType
            }

            //Set Sections Surveying Layout Data
            setSectionSurveyingData(measurementTabEntity!!)

            //Set Estimation Surveying Layout Data
            setEstimationLayoutData(measurementTabEntity!!)

            //Set Logs Surveying Layout Data
            setLogsLayoutData(measurementTabEntity!!)

            //Set Additional Information
            edtLogsCount.setText(measurementTabEntity?.logsCount.toString())
            edtMinTop.setText(measurementTabEntity?.minTopCm.toString())
            edtMaxbase.setText(measurementTabEntity?.maxBaseCm.toString())
            edtOversize.setText(measurementTabEntity?.oversizeM.toString())


            if(measurementTabEntity?.sectionEntity != null){
                list = measurementTabEntity?.sectionEntity?.height_list as ArrayList<MeasureHeightListEntity>
                addLogList = measurementTabEntity?.logsEntity?.add_log_entity_list as ArrayList<AddLogEntity>
                sizeClassList = measurementTabEntity?.logsEntity?.size_class_entity_list as ArrayList<SizeClassEntity>
            }


            includeSectionSurvey.edtNr.setText((list!!.size+1).toString())

        }

        heightEntryAdapter  = HeightEntryAdapter(list!!,activity!!) //HeightList Adapter
        addLogAdapter = AddLogEntityAdapter(this,addLogList,activity!!) // AddLog List Adapter

        setupHeightListView()
        setupAddLogListView()

        txtChangeSurvey.setOnClickListener {
            startActivityForResult(Intent(activity,SelectSurveyingActivity::class.java),Utilities.SURVEYING_REQ_CODE)
        }

        txtSectionSpecies.setOnClickListener(this)
        txtSectionKind.setOnClickListener(this)
        txtSectionQuality.setOnClickListener(this)
        txtEstimationSize.setOnClickListener(this)

        txtEstimationSpecies.setOnClickListener(this)
        txtEstimationKind.setOnClickListener(this)
        txtEstimationQuality.setOnClickListener(this)
        txtEstimationSize.setOnClickListener(this)

        includeSectionSurvey.edtSecLength.addTextChangedListener(edtSecLengthEditor)
       // includeEstimationSurvey.edtLength.addTextChangedListener(edtLengthEditor)
        includeSectionSurvey.edtSectionWidth.addTextChangedListener(edtSecWidthEditor)
       // includeEstimationSurvey.edtWidth.addTextChangedListener(edtWidthEditor)

    //    includeLogsSurvey.txtTotalVolumeM3.text = getString(R.string.log_volume,"0.00")
       /* txtSectionSpecies.setOnClickListener {
            startActivityForResult(Intent(activity,ItemListingActivity::class.java)
                    .putExtra(Utilities.CURRENT_ITEM_NAME,getString(R.string.species))
                    ,Utilities.ITEM_LIST_REQ_CODE)
        }

        txtSectionKind.setOnClickListener {
            startActivityForResult(Intent(activity,ItemListingActivity::class.java)
                    .putExtra(Utilities.CURRENT_ITEM_NAME,getString(R.string.kind))
                    ,Utilities.ITEM_LIST_REQ_CODE)
        }

        txtSectionQuality.setOnClickListener {
            startActivityForResult(Intent(activity,ItemListingActivity::class.java)
                    .putExtra(Utilities.CURRENT_ITEM_NAME,getString(R.string.quality))
                    ,Utilities.ITEM_LIST_REQ_CODE)
        }*/

        includeSectionSurvey.imgAddMeasureEntry.setOnClickListener {

            if(activity!!.currentFocus != null){
                hideKeyboard(activity!!.currentFocus!!)
            }

            if(includeSectionSurvey.edtHeight.text.isEmpty()){
                Toast.makeText(activity!!,getString(R.string.alert_enter_height),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(includeSectionSurvey.edtSection.text.isEmpty()){
                Toast.makeText(activity!!,getString(R.string.alert_enter_section),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var measureHeightEntry = MeasureHeightListEntity(list!!.size+1
                    ,includeSectionSurvey.edtHeight.text.toString().toDouble()
                    ,includeSectionSurvey.edtSection.text.toString().toDouble())
            list?.add(measureHeightEntry)
            heightEntryAdapter?.heightEntryList = list!!
            heightEntryAdapter?.notifyDataSetChanged()
            includeSectionSurvey.edtNr.setText((list!!.size+1).toString())
            calculateMeasures()

        }

        txtLogAdd.setOnClickListener {

            startActivityForResult(Intent(activity,AddLogActivity::class.java).putExtra("logIndex",addLogList.size+1)
                    .putExtra("logKind",kind)
                    .putExtra("isFromModify",false)
                    .putExtra("logList",addLogList),REQ_CODE_GET_ADDLOG_ENTITY)
        }


        txtexportPolter.setOnClickListener {

            startActivityForResult(Intent(activity,ExportLogStackActivity::class.java).putExtra("logIndex",addLogList.size+1)
                    .putExtra("logKind",kind)
                    .putExtra("isFromModify",false)
                    .putExtra("logList",addLogList),REQ_CODE_GET_ADDLOG_ENTITY)

        }



        // Changing ST and Volume Values when Distr % value change
        edtDistr.addTextChangedListener(distrEditor)
        edtFactor.addTextChangedListener(factorEditor)



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Utilities.SURVEYING_REQ_CODE){
            if(resultCode == Activity.RESULT_OK){
                val surveying_type = data?.getStringExtra(Utilities.SURVEYING_TYPE)
                txtSurveyType.text = surveying_type

               // (activity as LogEntryDetailActivity).viewModel.updateLogRecentSurveyingType(surveying_type!!,logId)
                val measurementTabEntity = MeasurementTabEntity(logId,txtSurveyType.text.toString(),getEstimationLayoutTexts(), getSectionLayoutTexts(), getLogsLayoutTexts(),
                        Utilities.inputIntValidation(edtLogsCount.text.toString()),
                        Utilities.inputNumberValidation(edtMinTop.text.toString()),
                        Utilities.inputNumberValidation(edtMaxbase.text.toString()),
                        Utilities.inputNumberValidation(edtOversize.text.toString()),
                        0
                        )
                ((activity as LogEntryDetailActivity)).viewModel.updateMeasureTabDetail(measurementTabEntity)
                ((activity as LogEntryDetailActivity)).viewModel.updateLogMeasurementEntityById(measurementTabEntity,logId)
                showSurveyingTypeData()
            }

        }

        if(requestCode == REQ_CODE_GET_ADDLOG_ENTITY) {

            if (resultCode == Activity.RESULT_OK) {
                System.out.print("Loggg")
                addLogList = data?.getSerializableExtra("logList")as ArrayList<AddLogEntity>
                //addLogList.add(addLogEntity)
                addLogAdapter?.addLogEntityList = addLogList
                addLogAdapter?.notifyDataSetChanged()
                includeLogsSurvey.txtLogCounter.text = addLogList.size.toString()
                var totalVolumeM3 = 0.0
                for (addLogEntity: AddLogEntity in addLogList) {
                    totalVolumeM3 = totalVolumeM3 + addLogEntity.volume_m3
                }
                val digit = Math.round(totalVolumeM3 * 1000.0).toDouble() / 1000.0
             //   val formatString = String.format("%.3f", digit)
                includeLogsSurvey.txtTotalVolumeM3.setText(digit.toString())

                ((activity as LogEntryDetailActivity)).txtVolume.setText(getString(R.string.log_volume, digit.toString()))

                edtLogsCount.setText(addLogList.size.toString())

                /* if(addLogList.size > 1){

                     for(i in 0..sizeClassList.size -1){


                         if(addLogEntity.size!!.equals(sizeClassList.get(i).size)){

                            val prevSizeClassEntity = sizeClassList.get(i)
                             sizeClassList.remove(prevSizeClassEntity)
                             sizeClassAdapter?.notifyDataSetChanged()
                             var newSizeClassEntity = prevSizeClassEntity
                             newSizeClassEntity.count = newSizeClassEntity.count + 1
                             newSizeClassEntity.quota = quota/newSizeClassEntity.count
                             newSizeClassEntity.volume_m3 = newSizeClassEntity.volume_m3 * 2
                             sizeClassList.add(newSizeClassEntity)

                         }else{

                            val newEntity = SizeClassEntity(addLogEntity.size.toString(),addLogEntity.count,
                                    quota,addLogEntity.volume_m3)
                             newEntity.quota = quota

                             sizeClassList.add(newEntity)
                             sizeClassAdapter?.notifyDataSetChanged()

                         }
                     }

                 }else{
                     val sizeClassEntity = SizeClassEntity(addLogEntity.size.toString(),addLogEntity.count,
                             quota,addLogEntity.volume_m3 )

                     sizeClassList.add(sizeClassEntity)
                     sizeClassAdapter?.notifyDataSetChanged()
                 }



             }  */
            }
        }

        if(requestCode == REQ_CODE_MODIFY_LOG_ENTITY) {

            if (resultCode == Activity.RESULT_OK) {
                System.out.print("Loggg")
                val modifiedEntity = data?.getSerializableExtra("modifiedEntity")as AddLogEntity
                addLogList.set(mCurrentEntityIndex,modifiedEntity)
                addLogAdapter?.addLogEntityList = addLogList
                addLogAdapter?.notifyDataSetChanged()
                includeLogsSurvey.txtLogCounter.text = addLogList.size.toString()
                var totalVolumeM3 = 0.0
                for (addLogEntity: AddLogEntity in addLogList) {

                    totalVolumeM3 = totalVolumeM3 + addLogEntity.volume_m3
                }
                val digit = Math.round(totalVolumeM3 * 1000.0).toDouble() / 1000.0
                //   val formatString = String.format("%.3f", digit)
                includeLogsSurvey.txtTotalVolumeM3.setText(digit.toString())

                ((activity as LogEntryDetailActivity)).txtVolume.setText(getString(R.string.log_volume, digit.toString()))

                edtLogsCount.setText(addLogList.size.toString())

                /* if(addLogList.size > 1){

                     for(i in 0..sizeClassList.size -1){


                         if(addLogEntity.size!!.equals(sizeClassList.get(i).size)){

                            val prevSizeClassEntity = sizeClassList.get(i)
                             sizeClassList.remove(prevSizeClassEntity)
                             sizeClassAdapter?.notifyDataSetChanged()
                             var newSizeClassEntity = prevSizeClassEntity
                             newSizeClassEntity.count = newSizeClassEntity.count + 1
                             newSizeClassEntity.quota = quota/newSizeClassEntity.count
                             newSizeClassEntity.volume_m3 = newSizeClassEntity.volume_m3 * 2
                             sizeClassList.add(newSizeClassEntity)

                         }else{

                            val newEntity = SizeClassEntity(addLogEntity.size.toString(),addLogEntity.count,
                                    quota,addLogEntity.volume_m3)
                             newEntity.quota = quota

                             sizeClassList.add(newEntity)
                             sizeClassAdapter?.notifyDataSetChanged()

                         }
                     }

                 }else{
                     val sizeClassEntity = SizeClassEntity(addLogEntity.size.toString(),addLogEntity.count,
                             quota,addLogEntity.volume_m3 )

                     sizeClassList.add(sizeClassEntity)
                     sizeClassAdapter?.notifyDataSetChanged()
                 }



             }  */
            }
        }


        if(requestCode == Utilities.ITEM_LIST_REQ_CODE){

            if(resultCode == Activity.RESULT_OK){
                val currentItemName = data?.getStringExtra(Utilities.CURRENT_ITEM_NAME)
                val currentItemValue = data?.getStringExtra(Utilities.CURRENT_ITEM_VALUE)
                if(currentItemName.equals(getString(R.string.species))){
                    includeSectionSurvey.edtSectionSpecies.setText(currentItemValue)
                    includeEstimationSurvey.edtSpecies.setText(currentItemValue)
                  //  includeLogsSurvey.edtSpecies.setText(data?.getStringExtra(Utilities.CURRENT_ITEM_VALUE))
                }else if(currentItemName.equals(getString(R.string.kind))){
                    includeSectionSurvey.edtSectionKind.setText(currentItemValue)
                    includeEstimationSurvey.edtKind.setText(currentItemValue)
                    kind = currentItemValue
                }else if(currentItemName.equals(getString(R.string.quality))){
                    includeSectionSurvey.edtSectionQuality.setText(currentItemValue)
                    includeEstimationSurvey.edtQuality.setText(currentItemValue)
                }else{
                    edtSize.setText(currentItemValue)
                   // includeSectionSurvey.edtSectionQuality.setText(currentItemValue)
                   // includeEstimationSurvey.edt.setText(currentItemValue)
                }

            }
        }


    }

    // Save entered details into database
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveDataIntoDatabase()

    }

    // Save entered details into database
    override fun onStop() {
        saveDataIntoDatabase()
        super.onStop()
    }

    fun  setEstimationLayoutData(measurementEntity: MeasurementTabEntity){

        includeEstimationSurvey.edtSpecies.setText(measurementEntity.estimationEntity?.species)
        includeEstimationSurvey.edtKind.setText(measurementEntity.estimationEntity?.kind)
        includeEstimationSurvey.edtQuality.setText(measurementEntity.estimationEntity?.quality)
        includeEstimationSurvey.edtSize.setText(measurementEntity.estimationEntity?.size.toString())
        includeEstimationSurvey.edtLength.setText(measurementEntity.estimationEntity?.length.toString())
        includeEstimationSurvey.edtWidth.setText(measurementEntity.estimationEntity?.width.toString())
        includeEstimationSurvey.edtUnit.setText(measurementEntity.estimationEntity?.unit.toString())
        includeEstimationSurvey.edtVolume.setText(measurementEntity.estimationEntity?.volume.toString())
    }


    fun  setLogsLayoutData(measurementEntity: MeasurementTabEntity){

        includeLogsSurvey.txtLogCounter.setText(measurementEntity.logsEntity?.log_count.toString())
        includeLogsSurvey.txtTotalVolumeM3.setText(measurementEntity.logsEntity?.total_volume_m3.toString())

    }


    fun  showSurveyingTypeData(){
        if(txtSurveyType.text.equals(getString(R.string.logs_surveying))){
            includeSectionSurvey.visibility = View.GONE
            includeEstimationSurvey.visibility = View.GONE
            includeLogsSurvey.visibility = View.VISIBLE
            imgSurveyType.setImageDrawable(activity?.resources?.getDrawable(R.drawable.ic_log))
        }else if(txtSurveyType.text.equals(getString(R.string.estimation_surveying))){
            includeSectionSurvey.visibility = View.GONE
            includeEstimationSurvey.visibility = View.VISIBLE
            includeLogsSurvey.visibility = View.GONE
            imgSurveyType.setImageDrawable(activity?.resources?.getDrawable(R.drawable.ic_estimation))
        }else{
            includeSectionSurvey.visibility = View.VISIBLE
            includeEstimationSurvey.visibility = View.GONE
            includeLogsSurvey.visibility = View.GONE
            imgSurveyType.setImageDrawable(activity?.resources?.getDrawable(R.drawable.ic_section))
        }
    }

    fun  setSectionSurveyingData(measurementEntity: MeasurementTabEntity){

        includeSectionSurvey.edtSectionSpecies.setText(measurementEntity.sectionEntity?.species)
        includeSectionSurvey.edtSectionKind.setText(measurementEntity.sectionEntity?.kind)
        includeSectionSurvey.edtSectionQuality.setText(measurementEntity.sectionEntity?.quality)
        if(measurementEntity.sectionEntity?.length != null)
        includeSectionSurvey.edtSecLength.setText(measurementEntity.sectionEntity?.length.toString())
        if(measurementEntity.sectionEntity?.width != null)
        includeSectionSurvey.edtSectionWidth.setText(measurementEntity.sectionEntity?.width.toString())
        if(measurementEntity.sectionEntity?.section_length != null)
        includeSectionSurvey.edtSectionLength.setText(measurementEntity.sectionEntity?.section_length.toString())
        if(measurementEntity.sectionEntity?.sum_of_sections != null)
        includeSectionSurvey.txtSumOfSections.setText(measurementEntity.sectionEntity?.sum_of_sections.toString())
        if(measurementEntity.sectionEntity?.height_count != null)
        includeSectionSurvey.txtHeightCount.setText(measurementEntity.sectionEntity?.height_count.toString())
        if(measurementEntity.sectionEntity?.avg_height != null)
        includeSectionSurvey.txtAvgHeight.setText(measurementEntity.sectionEntity?.avg_height.toString())
        if(measurementEntity.sectionEntity?.volume_bark != null)
        includeSectionSurvey.txtVolumeBark.setText(measurementEntity.sectionEntity?.volume_bark.toString())
        if(measurementEntity.sectionEntity?.st != null)
            includeSectionSurvey.txtST.setText(measurementEntity.sectionEntity?.st.toString())
        if(measurementEntity.sectionEntity?.distr != null)
            includeSectionSurvey.edtDistr.setText(measurementEntity.sectionEntity?.distr.toString())
        if(measurementEntity.sectionEntity?.m3 != null)
            includeSectionSurvey.txtM3.setText(measurementEntity.sectionEntity?.m3.toString())
        if(measurementEntity.sectionEntity?.factor != null)
            includeSectionSurvey.edtFactor.setText(measurementEntity.sectionEntity?.factor.toString())

    }


    fun  getEstimationLayoutTexts(): EstimationEntity{
       val estimationEntity = EstimationEntity(
               inputTextValidation(includeEstimationSurvey.edtSpecies.text.toString()),
               inputTextValidation(includeEstimationSurvey.edtKind.text.toString()),
               inputTextValidation(includeEstimationSurvey.edtQuality.text.toString()),
               inputTextValidation(includeEstimationSurvey.edtSize.text.toString()),
               inputNumberValidation(includeEstimationSurvey.edtLength.text.toString()),
               inputNumberValidation(includeEstimationSurvey.edtWidth.text.toString()),
               inputTextValidation(includeEstimationSurvey.edtUnit.text.toString()),
               inputNumberValidation(includeEstimationSurvey.edtVolume.text.toString()))
       return estimationEntity
   }

    fun  getSectionLayoutTexts(): SectionEntity{
        val sectionEntity = SectionEntity(
                inputTextValidation(includeSectionSurvey.edtSectionSpecies.text.toString()),
                inputTextValidation(includeSectionSurvey.edtSectionKind.text.toString()),
                inputTextValidation(includeSectionSurvey.edtSectionQuality.text.toString()),
                inputNumberValidation(includeSectionSurvey.edtSecLength.text.toString()),
                inputNumberValidation(includeSectionSurvey.edtSectionWidth.text.toString()),
                inputNumberValidation(includeSectionSurvey.edtSectionLength.text.toString()),
                list,
                inputNumberValidation(includeSectionSurvey.txtSumOfSections.text.toString()),
                inputNumberValidation(includeSectionSurvey.txtHeightCount.text.toString()),
                inputNumberValidation(includeSectionSurvey.txtAvgHeight.text.toString()),
                inputNumberValidation(includeSectionSurvey.txtVolumeBark.text.toString()),
                inputNumberValidation(includeSectionSurvey.txtST.text.toString()),
                inputNumberValidation(includeSectionSurvey.edtDistr.text.toString()),
                inputNumberValidation(includeSectionSurvey.txtM3.text.toString()),
                inputNumberValidation(includeSectionSurvey.edtFactor.text.toString()))
        return sectionEntity
    }

    fun  getLogsLayoutTexts(): LogsEntity{
        val logsEntity = LogsEntity(
                Utilities.inputIntValidation(includeLogsSurvey.txtLogCounter.text.toString())
                ,addLogList,sizeClassList, inputNumberValidation(includeLogsSurvey.txtTotalVolumeM3.text.toString()))
        return logsEntity
    }



    fun  saveDataIntoDatabase(){

          val measurementTabEntity = MeasurementTabEntity(logId,txtSurveyType.text.toString(),getEstimationLayoutTexts(), getSectionLayoutTexts(),getLogsLayoutTexts(),
          Utilities.inputIntValidation(edtLogsCount.text.toString()),
          Utilities.inputNumberValidation(edtMinTop.text.toString()),
          Utilities.inputNumberValidation(edtMaxbase.text.toString()),
          Utilities.inputNumberValidation(edtOversize.text.toString()),
              0)
         ((activity as LogEntryDetailActivity)).viewModel.updateMeasureTabDetail(measurementTabEntity)
        ((activity as LogEntryDetailActivity)).viewModel.updateLogMeasurementEntityById(measurementTabEntity,logId)
        ((activity as LogEntryDetailActivity)).viewModel.
                updateMeasureLogStackById(inputTextValidation(includeSectionSurvey.edtSectionSpecies.text.toString()),
                        inputTextValidation(includeSectionSurvey.edtSectionKind.text.toString()),
                        inputTextValidation(includeSectionSurvey.edtSectionQuality.text.toString()),
                        inputNumberValidation(includeEstimationSurvey.edtLength.text.toString()),
                        inputNumberValidation(includeEstimationSurvey.edtVolume.text.toString()),
                        Utilities.inputIntValidation(includeLogsSurvey.txtLogCounter.text.toString()),
                        inputNumberValidation(includeSectionSurvey.txtM3.text.toString()),logId)
    }



    private fun setupHeightListView() {

        rvHeightEntry.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        rvHeightEntry.adapter = heightEntryAdapter

        var swipeController = SwipeController(activity!!,object : SwipeControllerActions() {
            override fun onRightClicked(position: Int) {
                list?.removeAt(position)
                var initIndex = 0

                for(liste : MeasureHeightListEntity in list!!){
                    initIndex ++
                    liste.index = initIndex
                }
                heightEntryAdapter!!.heightEntryList = list!!
                heightEntryAdapter!!.notifyDataSetChanged()
                includeSectionSurvey.edtNr.setText((list!!.size+1).toString())
                calculateMeasures()
            }
        })

        val itemTouchhelper = ItemTouchHelper(swipeController)
        itemTouchhelper.attachToRecyclerView(rvHeightEntry)

        rvHeightEntry.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                swipeController!!.onDraw(c)
            }
        })
    }



    private fun setupAddLogListView() {

        rvAddLog.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        rvAddLog.adapter = addLogAdapter

        var swipeController = SwipeController(activity!!,object : SwipeControllerActions() {
            override fun onRightClicked(position: Int) {
                val addLogEntity = addLogList.get(position)
                addLogList.removeAt(position)
                var initIndex = 0
                for(liste : AddLogEntity in addLogList){
                    initIndex ++
                    liste.index = initIndex
                }
                addLogAdapter!!.addLogEntityList = addLogList
                addLogAdapter!!.notifyDataSetChanged()
                includeLogsSurvey.txtLogCounter.text = addLogList.size.toString()
                val updatedM3Value = (txtTotalVolumeM3.text.toString().toDouble() - addLogEntity.volume_m3)
                val digit = Math.round(updatedM3Value * 1000.0).toDouble() / 1000.0
                txtTotalVolumeM3.text = digit.toString()
                edtLogsCount.setText(addLogList.size.toString())
            }
        })

        val itemTouchhelper = ItemTouchHelper(swipeController)
        itemTouchhelper.attachToRecyclerView(rvAddLog)

        rvAddLog.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                swipeController!!.onDraw(c)
            }
        })

        rvAddLog.addItemDecoration(DividerItemDecoration(rvAddLog.getContext(), DividerItemDecoration.VERTICAL))
    }


    /*fun Fragment.hideKeyboard(view : View) {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard(view : View) {
        hideKeyboard(if (view == null) View(this) else view)
    }*/

    fun hideKeyboard(view: View) {
        val inputMethodManager = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @SuppressLint("StringFormatMatches")
    fun calculateMeasures(){
      //  var index = includeSectionSurvey.edtNr.text.toString().toInt()
      //  index = index + 1
      //  includeSectionSurvey.edtNr.setText(index.toString())
        var sumOfSections = 0.0
        for (measureEntity: MeasureHeightListEntity in list!!) {
            sumOfSections = sumOfSections + measureEntity.section.toDouble()
        }
        includeSectionSurvey.txtSumOfSections.setText(sumOfSections.toString())
        includeSectionSurvey.txtHeightCount.setText(list!!.size.toString())
        var heightSum = 0.00
        var heightSectionValue = 0.00
        for (measureEntity: MeasureHeightListEntity in list!!) {
            heightSum = heightSum + measureEntity.height.toDouble()
            heightSectionValue = heightSectionValue + (measureEntity.height * measureEntity.section)
        }

        if(list!!.size > 0){

            includeSectionSurvey.txtAvgHeight.setText((heightSum/list!!.size).toString())
        }else
        {
            includeSectionSurvey.txtAvgHeight.setText("0")

        }

        val volumeSTDark = inputNumberValidation(edtSecLength.text.toString()) * heightSectionValue
        includeSectionSurvey.txtVolumeBark.text = volumeSTDark.toString()

      //  includeSectionSurvey.txtST.text = volumeSTDark.toString()
        includeSectionSurvey.txtST.text = volumeSTDark.toString()
        val m3Value = volumeSTDark * (inputNumberValidation(edtFactor.text.toString()))
        includeSectionSurvey.txtM3.text = m3Value.toString()
        calculateSTandM3Values(edtDistr.text.toString())
      //
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

    fun inputTextValidation(str : String) : String?{

        try{
            if(TextUtils.isEmpty(str)){
                return ""
            }else{
                return str
            }
        }catch (e:Exception){
            e.printStackTrace()
            return null
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txtSectionSpecies,
            R.id.txtEstimationSpecies -> {
                startActivityForResult(Intent(activity,ItemListingActivity::class.java)
                        .putExtra(Utilities.CURRENT_ITEM_NAME,getString(R.string.species))
                        ,Utilities.ITEM_LIST_REQ_CODE)
            }
            R.id.txtSectionKind,
            R.id.txtEstimationKind-> {
                startActivityForResult(Intent(activity,ItemListingActivity::class.java)
                        .putExtra(Utilities.CURRENT_ITEM_NAME,getString(R.string.kind))
                        ,Utilities.ITEM_LIST_REQ_CODE)
            }
            R.id.txtSectionQuality,
            R.id.txtEstimationQuality-> {
                startActivityForResult(Intent(activity,ItemListingActivity::class.java)
                        .putExtra(Utilities.CURRENT_ITEM_NAME,getString(R.string.quality))
                        ,Utilities.ITEM_LIST_REQ_CODE)
            }
            R.id.txtEstimationSize -> {
                startActivityForResult(Intent(activity,ItemListingActivity::class.java)
                        .putExtra(Utilities.CURRENT_ITEM_NAME,getString(R.string.size))
                        ,Utilities.ITEM_LIST_REQ_CODE)
            }
            else -> {
                // else condition
            }
        }
    }



    private val distrEditor = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            /*val distrWithST = (inputNumberValidation(s.toString())/100) * inputNumberValidation(txtST.text.toString())
            val newSTValue = inputNumberValidation(txtVolumeBark.text.toString()) - distrWithST
            includeSectionSurvey.txtST.text = newSTValue.toString()
            val distrWithM3= (inputNumberValidation(s.toString())/100) * inputNumberValidation(txtM3.text.toString())
            val newM3Value = inputNumberValidation(txtM3.text.toString()) - distrWithM3
            includeSectionSurvey.txtM3.text = newM3Value.toString()*/
            if(TextUtils.isEmpty(s)){
                edtDistr.setText("0.00")
            }

            calculateSTandM3Values(edtDistr.text.toString())

        }

        override fun afterTextChanged(etValue: Editable?) {


        }
    }

    private val factorEditor = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            if(TextUtils.isEmpty(s)){
                edtFactor.setText("0.00")
            }

            val m3 = txtST.text.toString().toDouble() * edtFactor.text.toString().toDouble()
            txtM3.setText(m3.toString())
        }

        override fun afterTextChanged(etValue: Editable?) {



        }
    }

    private val edtLengthEditor = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            if(TextUtils.isEmpty(s)){
                includeSectionSurvey.edtSecLength.setText("0.00")
              //  includeEstimationSurvey.edtLength.setText("0.00")
            }
          //  includeSectionSurvey.edtSecLength.setText(edtSe)
           includeSectionSurvey.edtSecLength.setText(s.toString())

        }

        override fun afterTextChanged(etValue: Editable?) {


        }
    }

    private val edtWidthEditor = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            if(TextUtils.isEmpty(s)){
                includeSectionSurvey.edtSectionWidth.setText("0.00")
                includeEstimationSurvey.edtWidth.setText("0.00")
            }

            includeSectionSurvey.edtSectionWidth.setText(s.toString())

        }

        override fun afterTextChanged(etValue: Editable?) {


        }
    }

    private val edtSecLengthEditor = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            if(TextUtils.isEmpty(s)){
                includeSectionSurvey.edtSecLength.setText("0.00")
                //  includeEstimationSurvey.edtLength.setText("0.00")
            }
            //  includeSectionSurvey.edtSecLength.setText(edtSe)
            includeEstimationSurvey.edtLength.setText(s.toString())

        }

        override fun afterTextChanged(etValue: Editable?) {


        }
    }

    private val edtSecWidthEditor = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            if(TextUtils.isEmpty(s)){
                includeSectionSurvey.edtSectionWidth.setText("0.00")
                includeEstimationSurvey.edtWidth.setText("0.00")
            }

            includeEstimationSurvey.edtWidth.setText(s.toString())

        }

        override fun afterTextChanged(etValue: Editable?) {


        }
    }



    fun calculateSTandM3Values(value: String){

        if(TextUtils.isEmpty(value)){
            includeSectionSurvey.txtST.text = txtVolumeBark.text.toString()
            val m3Value = inputNumberValidation(txtVolumeBark.text.toString()) * (inputNumberValidation(edtFactor.text.toString()))
            includeSectionSurvey.txtM3.text = m3Value.toString()
            //calculateSTandM3Values(edtDistr.text.toString())
        }else{
            val distrWithST = (inputNumberValidation(value.toString())/100) * inputNumberValidation(txtST.text.toString())
            val newSTValue = inputNumberValidation(txtVolumeBark.text.toString()) - distrWithST
            includeSectionSurvey.txtST.text = newSTValue.toString()
            val distrWithM3= (inputNumberValidation(value.toString())/100) * inputNumberValidation(txtM3.text.toString())
            val newM3Value = inputNumberValidation(txtM3.text.toString()) - distrWithM3
            includeSectionSurvey.txtM3.text = newM3Value.toString()
        }

    }


    override fun onLogClick(entity: AddLogEntity) {

        mCurrentEntityIndex = addLogList.indexOf(entity)
        startActivityForResult(Intent(activity,AddLogActivity::class.java).putExtra("logIndex",addLogList.size+1)
                .putExtra("logKind",kind)
                .putExtra("isFromModify",true)
                .putExtra("modifiedEntity",entity),REQ_CODE_MODIFY_LOG_ENTITY)


    }

}// Required empty public constructor