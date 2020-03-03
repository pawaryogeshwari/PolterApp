package com.polter.mobipolter

import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.polter.mobipolter.activities.LogEntryDetailActivity


import com.polter.mobipolter.activities.SettingActivity
import com.polter.mobipolter.activities.adapters.DashboardLogAdapter
import com.polter.mobipolter.activities.adapters.DashboardLogListener
import com.polter.mobipolter.activities.viewmodel.LogEntryViewModel
import kotlinx.android.synthetic.main.activity_dashboard.*
import android.net.Uri
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.print.PrintAttributes
import android.support.v4.content.FileProvider
import android.util.Log
import android.webkit.MimeTypeMap
import com.google.gson.Gson
import com.itextpdf.text.*
import com.itextpdf.text.Font
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.polter.mobipolter.activities.model.*
import com.polter.mobipolter.activities.utility.*
import com.polter.mobipolter.activities.utility.Utilities
import jxl.CellView
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.format.UnderlineStyle
import jxl.write.*
import kotlinx.android.synthetic.main.layout_logs_survey.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.Map
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap


class DashboardActivity : AppCompatActivity(), DashboardLogListener {

    var logStackEntityList: ArrayList<LogStackEntity> = ArrayList()
    var logStackEntity : LogStackEntity?= null
    var multi_selection_list : ArrayList<LogStackEntity> = ArrayList()
    public var  isMultiselect = false
    var adapter : DashboardLogAdapter?= null
    var viewModel : LogEntryViewModel?= null
    var logEntry = ArrayList<LogStackEntity>()
    var logStackEntityListDb: ArrayList<LogStackEntity> = ArrayList()
    var currentLogIndex : Int = 0
    lateinit var mAppSharedPreferences : SharedPreferences
    lateinit var mAppSharedPreferencesEditor : SharedPreferences.Editor
    var currentFilePath : String ?= null
    var currentFile : File ? = null
    var ascending = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
      //  viewModel = ViewModelProviders.of(this).get(LogEntryViewModel::class.java!!)

        mAppSharedPreferences = getSharedPreferences("App_Pref",0)
        mAppSharedPreferencesEditor = mAppSharedPreferences.edit()

        viewModel = ViewModelProviders.of(this, ViewModelProviders.DefaultFactory(this.application)).get(LogEntryViewModel::class.java!!)

        recyclerView.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        adapter  = DashboardLogAdapter(logStackEntityListDb,multi_selection_list,this,this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecorations(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.item_decorator)!!))

      //  recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.HORIZONTAL))


        viewModel?.allNotes?.observe(this, Observer<List<LogStackEntity>> { logEntriesListDB ->
            logStackEntityListDb = logEntriesListDB as ArrayList<LogStackEntity>
            if(logStackEntityListDb.size >0){
                txtEdit.setTextColor(resources.getColor(R.color.white_color))
                txtEntryCount.text = getString(R.string.log_stack,""+logStackEntityListDb.size)
                txtEdit.isEnabled = true
            } else{
                txtEdit.setTextColor(resources.getColor(R.color.dark_gray_color))
                txtEntryCount.text = getString(R.string.log_stack,""+0)
                txtEdit.isEnabled = false

            }

            adapter?.logEntryList = logStackEntityListDb
            adapter?.notifyDataSetChanged()
           // recyclerView.scrollToPosition(logStackEntityListDb.size-1);
        })

        imgSortByDate.setOnClickListener{

            if(logStackEntityListDb.size > 0){

               if(!ascending){
                   sortEntitiesByDescendingDate()
                   ascending = true

               }else{
                   ascending = false
                   sortEntitiesByAscendingDate()
               }

            }
            // multi_selection_list = ArrayList<LogStackEntity>()

        }

        txtDelete.setOnClickListener{

            if(multi_selection_list.size > 0){

                delLogsPopup(multi_selection_list.size).show()
            }
           // multi_selection_list = ArrayList<LogStackEntity>()

        }

        txtEdit.setOnClickListener{

            if (!isMultiselect) {
                multi_selection_list = ArrayList()
                isMultiselect = true
                selectionLayout.visibility = View.VISIBLE
                logEntryAddLayout.visibility = View.GONE
                txtDone.visibility = View.VISIBLE
                txtEdit.visibility = View.GONE
                adapter?.multiSelect = true
                //adapter?.show = false
                adapter?.enableSelection = true
                adapter?.notifyDataSetChanged()
                // setListViewParam()
            }

        txtDone.setOnClickListener {
            clearMultiSelection()
        }
            //  multi_select(position)

        }

        imgSettings.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }


        imgAddEntry.setOnClickListener{

           /* var currentLogEntry = viewModel?.getCurrentLogEntryIndex
            if(currentLogEntry != null){
                currentLogIndex = currentLogEntry.logId;
            }*/

            currentLogIndex = mAppSharedPreferences.getInt("currentIndex",1)

            val alertDialog = AlertDialog.Builder(this)
                    .setTitle(getString(R.string.add_log_stack))
                    .setMessage(getString(R.string.copy_log_stack))
                    .setPositiveButton(getString(R.string.yes)) { dialog, whichButton ->
                        //  finish()
                        var copyFromExitingEntity = viewModel?.findLogById(currentLogIndex - 1)
                        copyFromExitingEntity?.logId = currentLogIndex
                        copyFromExitingEntity?.logBasicEntity?.basicLogDetailID = currentLogIndex
                        copyFromExitingEntity?.logBasicEntity?.stackNR = "Polter-"+currentLogIndex
                        copyFromExitingEntity?.logTitle = "Polter-"+currentLogIndex
                        copyFromExitingEntity?.logMeasurementEntity?.measureLogDetailID = currentLogIndex
                        copyFromExitingEntity?.logPhotoEntity = null
                       // copyFromExitingEntity?.logPhotoEntity?.photoLogDetailID = currentLogIndex
                        //copyFromExitingEntity?.logLocationEntity?.locationLogDetailID = currentLogIndex
                       // copyFromExitingEntity?.logLocationEntity?.locationLogTitle = "Polter-"+currentLogIndex
                        copyFromExitingEntity?.logLocationEntity = null
                        copyFromExitingEntity?.logBasicEntity?.date = Utilities.sysDateTime

                        if(copyFromExitingEntity != null)
                        viewModel?.insert(copyFromExitingEntity)
                        if(copyFromExitingEntity?.logBasicEntity != null)
                        viewModel?.insertBasicTabDetail(copyFromExitingEntity?.logBasicEntity!!)
                        /*if(copyFromExitingEntity?.logMeasurementEntity != null)
                        viewModel?.insertMeasureTabDetail(copyFromExitingEntity?.logMeasurementEntity!!)*/
                       /* if(copyFromExitingEntity?.logPhotoEntity != null)
                        viewModel?.insertPhotoTabDetail(copyFromExitingEntity?.logPhotoEntity!!)
                        if(copyFromExitingEntity?.logLocationEntity != null)
                        viewModel?.insertLocationTabDetail(copyFromExitingEntity?.logLocationEntity!!)*/
                        mAppSharedPreferencesEditor.putInt("recentLogId",currentLogIndex)
                        currentLogIndex = currentLogIndex + 1
                        mAppSharedPreferencesEditor.putInt("currentIndex",currentLogIndex)

                        mAppSharedPreferencesEditor.commit()



                    }
                    .setNegativeButton(getString(R.string.no)) { dialog, which ->

                        insertNewLogStack()

                        dialog.dismiss() }
                    .create()

            if(logStackEntityListDb.size == 0){

               insertNewLogStack()

            }else{
                alertDialog.show()
            }

        }

        txtExport.setOnClickListener {

            registerForContextMenu(txtExport)
            openContextMenu(txtExport)
        }


        // -------Add Predefined List into Database

        if(mAppSharedPreferences.getBoolean("isFirstTime",true)){
            val specieAbbrList = resources.getStringArray(R.array.species_abbr_list)
            val specieNameList = resources.getStringArray(R.array.species_name_list)
            val specieLatNameList = resources.getStringArray(R.array.species_latname_list)

            val list = ArrayList<SpeciesEntity>()
            for(i in 0..specieAbbrList.size-1){
                val speciesEntity = SpeciesEntity(specieAbbrList.get(i),
                        specieNameList.get(i) ,specieLatNameList.get(i))
                list.add(speciesEntity)
            }

            viewModel?.insertSpecies(list)
            val kindNoList = resources.getStringArray(R.array.kind_no_list)
            val kindNameList = resources.getStringArray(R.array.kind_name_list)
            val kindAbbrList = resources.getStringArray(R.array.kind_abbr_list)

            val kindlist = ArrayList<KindEntity>()
            for(i in 0..kindNoList.size-1){
                val kindEntity = KindEntity(kindAbbrList.get(i),
                        kindNameList.get(i) ,kindNoList.get(i),false)
                kindlist.add(kindEntity)
            }
            viewModel?.insertKind(kindlist)
            mAppSharedPreferencesEditor.putBoolean("isFirstTime",false)
            mAppSharedPreferencesEditor.commit()


        }


    }


    override fun onResume() {
        super.onResume()
       // selectionLayout.visibility = View.GONE
       // logEntryAddLayout.visibility = View.VISIBLE
        System.out.println("Log onResume Dashboard")

      //  adapter?.notifyDataSetChanged()
       // adapter?.activateContainer(true)
    }

     fun refreshAdapter(){
        adapter?.selected_logentry_list = multi_selection_list
        adapter?.logEntryList = logStackEntityListDb
        adapter?.showSelectedItems = true
        adapter?.notifyDataSetChanged()
    }

    fun multi_select(position: Int) {

        try {
            if (multi_selection_list.contains(logStackEntityListDb[position]))
                multi_selection_list.remove(logStackEntityListDb[position])
            else
                multi_selection_list.add(logStackEntityListDb[position])

            if(multi_selection_list.size == 0){

                txtDelete.setTextColor(resources.getColor(R.color.dark_gray_color))
                txtExport.setTextColor(resources.getColor(R.color.dark_gray_color))
               // isMultiselect = false
            }else{
                txtDelete.setTextColor(resources.getColor(R.color.white_color))
                txtExport.setTextColor(resources.getColor(R.color.white_color))
            }

//            if (multi_selection_list.size == 0) {
//                selectionLayout.visibility = View.GONE
//                logEntryAddLayout.visibility = View.VISIBLE
//                isMultiselect = false
//            } else {
//                selectionLayout.visibility = View.VISIBLE
//                logEntryAddLayout.visibility = View.GONE
//            }

            refreshAdapter()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun refreshSelectionList(){
        multi_selection_list = ArrayList<LogStackEntity>()
        adapter?.selected_logentry_list = multi_selection_list
       // adapter?.logStackEntityList = logStackEntityListDb
        adapter?.notifyDataSetChanged()
    }

    private fun delLogsPopup(count : Int): AlertDialog {
        return AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete))
                .setMessage(getString(R.string.delete)+" "+ count+" "+getString(R.string.log_stack_entry)+" ?")
                .setPositiveButton(getString(R.string.yes)) { dialog, whichButton ->
                  //  finish()

                    isMultiselect = false
                    if(multi_selection_list.size > 0 ){
                        viewModel?.deleteList(multi_selection_list)
                    }
                    selectionLayout.visibility = View.GONE
                    logEntryAddLayout.visibility = View.VISIBLE
                    adapter?.multiSelect = false;
                    adapter?.enableSelection = false
                    adapter?.showSelectedItems = false
                    adapter?.notifyDataSetChanged()
                    refreshSelectionList()
                    txtDone.visibility = View.GONE
                    txtEdit.visibility = View.VISIBLE
                }
                .setNegativeButton(getString(R.string.no)) { dialog, which ->
                    dialog.dismiss() }
                .create()
    }


    override fun onCheckboxImageClick(position: Int) {

        multi_select(position)
    }

    override fun onContainerClick(logEntry: LogStackEntity) {
        mAppSharedPreferencesEditor.putInt("recentLogId",logEntry.logId)
        mAppSharedPreferencesEditor.commit()
        startActivity(Intent(this, LogEntryDetailActivity::class.java)
                .putExtra("logId",logEntry.logId)
                .putExtra("stackNR",logEntry.logTitle)
                .putExtra("recentTab",logEntry.recentTab))


    }









    fun insertNewLogStack(){

        val logTitle = "Polter-"+currentLogIndex
        val basicEntity = BasicTabEntity(currentLogIndex,"",logTitle,
                null,Utilities.sysDateTime,0.00,
                0,0,null,null,null,null,null,null,null,null,null,0)
        val newLogEntry = LogStackEntity(currentLogIndex, logTitle,
                Utilities.sysDateTime,"","","",
                0.00,0.00,"",
                getString(R.string.tab_basic),
                basicEntity,
                null,null,null,
                getString(R.string.section_surveying),
                0,0.00,0)



        // if(viewModel?\\\\\\)
        viewModel?.insert(newLogEntry)
        viewModel?.insertBasicTabDetail(basicEntity)
        mAppSharedPreferencesEditor.putInt("recentLogId",currentLogIndex)
        currentLogIndex = currentLogIndex + 1
        mAppSharedPreferencesEditor.putInt("currentIndex",currentLogIndex)
        mAppSharedPreferencesEditor.commit()

    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.setHeaderTitle(getString(R.string.log_stack_options))
        menu?.add(Menu.NONE, Utilities.EXPORT_CREATE_PDF, Menu.NONE, getString(R.string.export_create_pdf))
        menu?.add(Menu.NONE, Utilities.EXPORT_SEND_PDF, Menu.NONE, getString(R.string.export_send_pdf_mail))
        menu?.add(Menu.NONE, Utilities.EXPORT_CREATE_CSV, Menu.NONE, getString(R.string.export_create_csv))
        menu?.add(Menu.NONE, Utilities.EXPORT_SEND_CSV, Menu.NONE, getString(R.string.export_send_csv_mail))
        menu?.add(Menu.NONE, Utilities.EXPORT_CREATE_EXCEL, Menu.NONE, getString(R.string.export_create_excel))
        menu?.add(Menu.NONE, Utilities.EXPORT_SEND_EXCEL, Menu.NONE, getString(R.string.export_send_excel_mail))

    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        // TODO Auto-generated method stub
        when (item?.getItemId()) {
            Utilities.EXPORT_CREATE_PDF -> {

                if(BuildConfig.PAID_VERSION){
                    //createExcelFile(multi_selection_list)

                    clearMultiSelection()
                    val progressDialog = ProgressDialog.show(this,"Exporting..","")
                    val isSuccess = createAndDisplayPdf(multi_selection_list)
                    progressDialog.dismiss()
                    if(isSuccess){
                        Toast.makeText(this,getString(R.string.pdf_success),Toast.LENGTH_LONG).show()
                        viewPdf(currentFile!!,"","")
                    }else{
                        Toast.makeText(this,getString(R.string.error),Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(this,getString(R.string.buy_polter_pro_hint),Toast.LENGTH_LONG).show()

                }
                //Toast.makeText(this,getString(R.string.buy_polter_pro_hint), Toast.LENGTH_LONG).show()
            }
            Utilities.EXPORT_SEND_PDF -> {
                if(BuildConfig.PAID_VERSION){
                    clearMultiSelection()
                    val isSuccess = createAndDisplayPdf(multi_selection_list)
                    if(isSuccess){
                        val contentUri = FileProvider.getUriForFile(this, "com.mobipolter.provider",currentFile!!)

                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Log Stack")
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Attached LogStack PDF")
                        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        shareIntent.setType("application/pdf")
                        startActivity(shareIntent)

                    }else{
                        Toast.makeText(this,getString(R.string.error), Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(this,getString(R.string.buy_polter_pro_hint), Toast.LENGTH_LONG).show()

                }

                   // Toast.makeText(this,getString(R.string.buy_polter_pro_hint), Toast.LENGTH_LONG).show()



            }
            Utilities.EXPORT_CREATE_CSV -> {
                if(BuildConfig.PAID_VERSION){
                    clearMultiSelection()
                    val isSuccess = createCSVFile(multi_selection_list)
                    if(isSuccess){
                        Toast.makeText(this,getString(R.string.csv_success),Toast.LENGTH_LONG).show()
                        viewCSV(currentFile!!,"","")

                    }else{
                        Toast.makeText(this,getString(R.string.error),Toast.LENGTH_LONG).show()
                    }
                }else{

                    Toast.makeText(this,getString(R.string.buy_polter_pro_hint), Toast.LENGTH_LONG).show()

                }




            }
            Utilities.EXPORT_SEND_CSV -> {
              //  Toast.makeText(this,getString(R.string.buy_polter_pro_hint), Toast.LENGTH_LONG).show()
                if(BuildConfig.PAID_VERSION){
                    clearMultiSelection()
                    val isSuccess = createCSVFile(multi_selection_list)
                    if(isSuccess){
                        val contentUri = FileProvider.getUriForFile(this, "com.mobipolter.provider",currentFile!!)

                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Log Stack")
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Attached LogStack CSV")
                        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        shareIntent.setType("application/csv")
                        startActivity(shareIntent)

                    }else{
                        Toast.makeText(this,getString(R.string.error), Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(this,getString(R.string.buy_polter_pro_hint), Toast.LENGTH_LONG).show()

                }

            }
            Utilities.EXPORT_CREATE_EXCEL -> {
                if(BuildConfig.PAID_VERSION){
                    clearMultiSelection()
                    val isSuccess = createExcelFile(multi_selection_list)
                    if(isSuccess){
                        Toast.makeText(this,getString(R.string.excel_success),Toast.LENGTH_LONG).show()
                        viewExcel(currentFile!!,"","")

                    }else{
                        Toast.makeText(this,getString(R.string.error),Toast.LENGTH_LONG).show()
                    }
                }else{

                    Toast.makeText(this,getString(R.string.buy_polter_pro_hint), Toast.LENGTH_LONG).show()

                }




            }
            Utilities.EXPORT_SEND_EXCEL -> {
                //  Toast.makeText(this,getString(R.string.buy_polter_pro_hint), Toast.LENGTH_LONG).show()
                if(BuildConfig.PAID_VERSION){
                    clearMultiSelection()
                    val isSuccess = createExcelFile(multi_selection_list)
                    if(isSuccess){
                        val contentUri = FileProvider.getUriForFile(this, "com.mobipolter.provider",currentFile!!)

                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Log Stack")
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Attached LogStack Excel")
                        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        shareIntent.setType("application/xls")



                        startActivity(shareIntent)

                    }else{
                        Toast.makeText(this,getString(R.string.error), Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(this,getString(R.string.buy_polter_pro_hint), Toast.LENGTH_LONG).show()

                }

            }
        }

        return super.onContextItemSelected(item)


    }

    override fun onDestroy() {
        super.onDestroy()
        System.out.println("destroyed")
    }


    fun createCSVFile(entityList: List<LogStackEntity>) : Boolean{

        try {
            val content = "1,2"
            var title = ""
            if(entityList.size == 1){
                title = entityList.get(0).logBasicEntity?.stackNR!!
            }else{
                title = "LogStack"
            }
            val file = create_CSV_File(title)
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile()
            }
            currentFile = file
            currentFilePath = file.absolutePath
            val fw = FileWriter(file.getAbsoluteFile())
            val bw = BufferedWriter(fw)

            for(entity in entityList){
                val vallues = entity?.logBasicEntity.toString()
                val gson = Gson()
                val logBasicEntityJson = gson.toJson(entity.logBasicEntity)


                if(entity.logBasicEntity != null){
                    bw.newLine()
                    bw.newLine()
                    bw.write(getString(R.string.TYP)+getString(R.string.comma) + getString(R.string.nr)+getString(R.string.comma)
                            +getString(R.string.los_id).substring(0,getString(R.string.los_id).length-1)+getString(R.string.comma)
                          +getString(R.string.foreign_nr).substring(0,getString(R.string.foreign_nr).length-1)+getString(R.string.comma)
                            +getString(R.string.date)+getString(R.string.comma)
                           +getString(R.string.location)+" E"+getString(R.string.comma)
                            +getString(R.string.location)+" N"+getString(R.string.comma)
                            +getString(R.string.comment)
                             )
                    bw.newLine()

                    var nr = ""
                    if(entity.logBasicEntity?.stackNR != null){
                       nr = entity.logBasicEntity?.stackNR!!
                    }

                    var losId = " "
                    if(entity.logBasicEntity?.iosID != null){

                        losId = entity.logBasicEntity?.iosID!!
                    }

                    var foreignNr = " "
                    if(entity.logBasicEntity?.foreignNR != null){
                        foreignNr = entity.logBasicEntity?.foreignNR!!
                    }
                    var date = " "
                    if(entity.logBasicEntity?.date != null){
                        date = entity.logBasicEntity?.date!!
                    }
                    var locationE = 0.0
                    if(entity?.logLocationEntity != null){
                        locationE = entity.logLocationEntity?.longtitude!!
                    }
                    var locationN = 0.0
                    if(entity?.logLocationEntity != null){
                        locationN = entity.logLocationEntity?.latitude!!
                    }
                    var comment = " "
                    if(entity?.logBasicEntity?.comment != null){
                        comment = entity?.logBasicEntity?.comment!!
                    }

                    bw.write(getString(R.string.polter_info)+getString(R.string.comma)+ nr+getString(R.string.comma)
                            +losId+getString(R.string.comma)
                            +foreignNr+getString(R.string.comma)
                            +date+getString(R.string.comma)
                            +locationE+getString(R.string.comma)
                            +locationN+getString(R.string.comma)
                            +comment
                    )
                    bw.newLine()
                    bw.newLine()

                    // FSC, PEFC values
                    bw.write(getString(R.string.PFC)+ getString(R.string.comma)+getString(R.string.fsc).substring(0,getString(R.string.fsc).length-1)+getString(R.string.comma) +getString(R.string.pefc).substring(0,getString(R.string.pefc).length-1)
                            +getString(R.string.comma)+getString(R.string.price)

                    )
                    bw.newLine()

                    var fsc = getString(R.string.no)
                    if(entity.logBasicEntity?.fsc == 1){
                        fsc = getString(R.string.yes)
                    }
                    var pefc = getString(R.string.no)
                    if(entity.logBasicEntity?.pefc == 1){
                        pefc = getString(R.string.yes)
                    }

                    var price = 0.00
                    if(entity.logBasicEntity?.price != null){
                        price = entity.logBasicEntity?.price!!
                    }


                    bw.write(" "+getString(R.string.comma)+fsc+getString(R.string.comma)
                            +pefc+getString(R.string.comma)
                            +price.toString()
                    )
                    bw.newLine()
                    bw.newLine()
                    // Location,District values
                    bw.write(getString(R.string.Lagerort)+getString(R.string.comma)+getString(R.string.location).substring(0,getString(R.string.location).length-1)+getString(R.string.comma)
                          +getString(R.string.district)+getString(R.string.comma) +getString(R.string.forest_owner).substring(0,getString(R.string.forest_owner).length-1)+getString(R.string.comma)
                           +getString(R.string.forester)+getString(R.string.comma)
                            +getString(R.string.forestry)
                            +getString(R.string.comma)+getString(R.string.clearer)+getString(R.string.comma)
                            +getString(R.string.felier)+getString(R.string.comma)
                            +getString(R.string.skidder)
                    )
                    var location = ""
                    if(entity.logBasicEntity?.location != null){
                        location = entity.logBasicEntity?.location!!
                    }
                    var district = " "
                    if(entity.logBasicEntity?.district != null){
                        district = entity.logBasicEntity?.district!!
                    }
                    var forestOwner = " "
                    if(entity.logBasicEntity?.forestOwner != null){
                        forestOwner = entity.logBasicEntity?.forestOwner!!
                    }
                    var forester = " "
                    if(entity?.logBasicEntity?.forester != null){
                        forester = entity?.logBasicEntity?.forester!!
                    }
                    var forestry = " "
                    if(entity?.logBasicEntity?.forestry != null){
                        forestry = entity?.logBasicEntity?.forestry!!
                    }
                    var clearer = " "
                    if(entity?.logBasicEntity?.clearer != null){
                        clearer = entity?.logBasicEntity?.clearer!!
                    }
                    var feller = " "
                    if(entity?.logBasicEntity?.feller != null){
                        feller = entity?.logBasicEntity?.feller!!
                    }
                    var skidder = " "
                    if(entity?.logBasicEntity?.skidder != null){
                        skidder = entity?.logBasicEntity?.skidder!!
                    }

                    bw.newLine()
                    bw.write("  "+getString(R.string.comma)+location+getString(R.string.comma)
                            +district+getString(R.string.comma)
                            +forestOwner+getString(R.string.comma)
                            +forester+getString(R.string.comma)
                            +forestry+getString(R.string.comma)
                            +clearer+getString(R.string.comma)
                            +feller+getString(R.string.comma)
                            +skidder
                    )
                    bw.newLine()

                }

                if(entity.logMeasurementEntity != null){
                    bw.newLine()
                    bw.write("  "+getString(R.string.comma)+getString(R.string.tab_measurement))
                    bw.newLine()


                    if(entity.logMeasurementEntity?.surveyingType != null) {

                        when (entity.logMeasurementEntity?.surveyingType) {


                            getString(R.string.section_surveying) -> {

                                if(entity.logMeasurementEntity?.sectionEntity != null){

                                    bw.write("  "+getString(R.string.comma)+getString(R.string.section_surveying))
                                    bw.newLine()


                                    bw.write("  "+getString(R.string.comma)+getString(R.string.csv_length_m)+getString(R.string.comma)
                                            +getString(R.string.csv_width_m)+getString(R.string.comma)+getString(R.string.logs_count).substring(0,getString(R.string.logs_count).length-1)+getString(R.string.comma)+getString(R.string.min_top_cm).substring(0,getString(R.string.min_top_cm).length-1)+getString(R.string.comma)
                                            +getString(R.string.max_base_cm).substring(0,getString(R.string.max_base_cm).length-1)+getString(R.string.comma)+getString(R.string.oversize)

                                    )
                                    bw.newLine()


                                    var length = ""

                                    if(entity.logMeasurementEntity?.sectionEntity?.length != null)
                                        length = entity.logMeasurementEntity?.sectionEntity!!.length.toString()

                                    var width = ""

                                    if(entity.logMeasurementEntity?.sectionEntity?.width != null)
                                        width = entity.logMeasurementEntity?.sectionEntity?.width.toString()

                                    var logsCount = ""

                                    if(entity.logMeasurementEntity?.logsCount!= null)
                                        logsCount = entity.logMeasurementEntity?.logsCount.toString()

                                    var minTopCm = ""

                                    if(entity.logMeasurementEntity?.minTopCm != null)
                                        minTopCm = entity.logMeasurementEntity?.minTopCm.toString()

                                    var maxBaseCm = ""

                                    if(entity.logMeasurementEntity?.maxBaseCm != null)
                                        maxBaseCm = entity.logMeasurementEntity?.maxBaseCm.toString()

                                    var oversizeM = ""


                                    if(entity.logMeasurementEntity?.oversizeM != null)
                                        oversizeM = entity.logMeasurementEntity?.oversizeM.toString()

                                    bw.write("  "+getString(R.string.comma)+length+getString(R.string.comma)
                                            +width+getString(R.string.comma)+logsCount+getString(R.string.comma)+minTopCm
                                            +getString(R.string.comma)+maxBaseCm+getString(R.string.comma)+oversizeM

                                    )
                                    bw.newLine()
                                    bw.newLine()


                                }

                                if(entity.logMeasurementEntity?.sectionEntity?.height_list != null &&
                                        entity.logMeasurementEntity?.sectionEntity?.height_list!!.size > 0){

                                    bw.write("  "+getString(R.string.comma)+getString(R.string.nr)+getString(R.string.comma)
                                            +getString(R.string.csv_height_m)+getString(R.string.comma)+getString(R.string.csv_section_m)

                                    )
                                    bw.newLine()

                                    for(i in 0..entity.logMeasurementEntity?.sectionEntity?.height_list!!.size-1){
                                        val heightListEntity = entity.logMeasurementEntity?.sectionEntity?.height_list!!.get(i)
                                        bw.write("  "+getString(R.string.comma)+heightListEntity.index.toString()+getString(R.string.comma)+heightListEntity.height.toString()
                                                +getString(R.string.comma)+
                                        heightListEntity.section.toString())
                                        bw.newLine()
                                    }

                                    bw.newLine()

                                    bw.write("  "+getString(R.string.comma)+getString(R.string.csv_sum_of_sections)+getString(R.string.comma)+getString(R.string.height_count).subSequence(0,getString(R.string.height_count).length-1).toString()
                                            +getString(R.string.comma)+
                                            getString(R.string.csv_avg_height)+getString(R.string.comma)+getString(R.string.csv_volume)
                                            +getString(R.string.comma)+
                                            getString(R.string.st).subSequence(0,getString(R.string.st).length-1).toString() +getString(R.string.comma)+ getString(R.string.csv_distr) +getString(R.string.comma)+
                                            "m3" +getString(R.string.comma)+getString(R.string.factor).subSequence(0,getString(R.string.factor).length-1).toString())
                                    bw.newLine()


                                    var sumValue = "0"
                                    if(entity.logMeasurementEntity?.sectionEntity!!.sum_of_sections.toString() != null){
                                        sumValue = entity.logMeasurementEntity?.sectionEntity!!.sum_of_sections.toString()
                                    }

                                    var heightCountValue = "0"
                                    if(entity.logMeasurementEntity?.sectionEntity!!.height_count.toString() != null){
                                        heightCountValue = entity.logMeasurementEntity?.sectionEntity!!.height_count.toString()
                                    }

                                    var avgHeightValue = "0.0"
                                    if(entity.logMeasurementEntity?.sectionEntity!!.avg_height.toString() != null){
                                        avgHeightValue = entity.logMeasurementEntity?.sectionEntity!!.avg_height.toString()
                                    }

                                    var volumeBarkValue = "0.0"
                                    if(entity.logMeasurementEntity?.sectionEntity!!.volume_bark.toString() != null){
                                        volumeBarkValue = entity.logMeasurementEntity?.sectionEntity!!.volume_bark.toString()
                                    }

                                    var stValue = "0.0"
                                    if(entity.logMeasurementEntity?.sectionEntity!!.st.toString() != null){
                                        stValue = entity.logMeasurementEntity?.sectionEntity!!.st.toString()
                                    }


                                    var distrValue = "0.0"
                                    if(entity.logMeasurementEntity?.sectionEntity!!.distr.toString() != null){
                                        distrValue = entity.logMeasurementEntity?.sectionEntity!!.distr.toString()
                                    }


                                    var m3Value = "0.0"
                                    if(entity.logMeasurementEntity?.sectionEntity!!.m3.toString() != null){
                                        m3Value = entity.logMeasurementEntity?.sectionEntity!!.m3.toString()

                                    }


                                    var factorValue = "0.0"
                                    if(entity.logMeasurementEntity?.sectionEntity!!.factor.toString() != null){
                                        factorValue = entity.logMeasurementEntity?.sectionEntity!!.factor.toString()

                                    }

                                    bw.write("  "+getString(R.string.comma)+sumValue+getString(R.string.comma)+heightCountValue
                                            +getString(R.string.comma)+avgHeightValue
                                            +getString(R.string.comma)+volumeBarkValue
                                            +getString(R.string.comma)+ stValue
                                            +getString(R.string.comma)+ distrValue +getString(R.string.comma)+
                                            m3Value +getString(R.string.comma)+factorValue)
                                    bw.newLine()


                                }


                            }

                            getString(R.string.estimation_surveying) -> {

                                if(entity.logMeasurementEntity?.estimationEntity != null){

                                    bw.newLine()

                                    bw.write("  "+getString(R.string.comma)+getString(R.string.estimation_surveying))
                                    bw.newLine()


                                    bw.write("  "+getString(R.string.comma)+getString(R.string.species)+getString(R.string.comma)+getString(R.string.kind)
                                            +getString(R.string.comma)+getString(R.string.quality)
                                            +getString(R.string.comma)+getString(R.string.size)
                                            +getString(R.string.comma)+ getString(R.string.csv_length_m)
                                            +getString(R.string.comma)+ getString(R.string.csv_width_m) +getString(R.string.comma)+
                                            getString(R.string.unit).subSequence(0,getString(R.string.unit).length-1).toString() +getString(R.string.comma)+getString(R.string.volume_value).subSequence(0,getString(R.string.volume_value).length-1).toString())
                                    bw.newLine()



                                    var specievalue = ""
                                    if(entity.logMeasurementEntity?.estimationEntity?.species != null)
                                        specievalue = entity.logMeasurementEntity?.estimationEntity!!.species.toString()

                                    var kindValue = ""

                                    if(entity.logMeasurementEntity?.estimationEntity?.kind != null)
                                       kindValue = entity.logMeasurementEntity?.estimationEntity!!.kind.toString()

                                    var qualityValue = ""

                                    if(entity.logMeasurementEntity?.estimationEntity?.quality != null)
                                        qualityValue = entity.logMeasurementEntity?.estimationEntity!!.quality.toString()

                                    var sizeValue = ""

                                    if(entity.logMeasurementEntity?.estimationEntity?.size != null)
                                        sizeValue = entity.logMeasurementEntity?.estimationEntity!!.size.toString()

                                    var lengthValue = ""

                                    if(entity.logMeasurementEntity?.estimationEntity?.length != null)
                                        lengthValue = entity.logMeasurementEntity?.estimationEntity!!.length.toString()

                                    var widthValue = ""

                                    if(entity.logMeasurementEntity?.estimationEntity?.width != null)
                                       widthValue = entity.logMeasurementEntity?.estimationEntity!!.width.toString()

                                    var unitValue = ""

                                    if(entity.logMeasurementEntity?.estimationEntity?.unit != null)
                                       unitValue = entity.logMeasurementEntity?.estimationEntity!!.unit.toString()

                                    var volumeValue = ""

                                    if(entity.logMeasurementEntity?.estimationEntity?.volume != null)
                                        volumeValue = entity.logMeasurementEntity?.estimationEntity!!.volume.toString()


                                    bw.write("  "+getString(R.string.comma)+specievalue+getString(R.string.comma)+kindValue
                                            +getString(R.string.comma)+qualityValue
                                            +getString(R.string.comma)+sizeValue
                                            +getString(R.string.comma)+ lengthValue
                                            +getString(R.string.comma)+ widthValue +getString(R.string.comma)+
                                            unitValue +getString(R.string.comma)+volumeValue)
                                    bw.newLine()

                                }


                            }


                            getString(R.string.logs_surveying) -> {


                                bw.write("  "+getString(R.string.comma)+getString(R.string.logs_surveying))
                                bw.newLine()


                                // Log  MinCm MaxCm OversizeCm
                                // FSC, PEFC values
                                bw.write("  "+getString(R.string.comma)+getString(R.string.logs_count).substring(0,getString(R.string.logs_count).length-1)+getString(R.string.comma)
                                        +getString(R.string.min_top_cm).substring(0,getString(R.string.min_top_cm).length-1)+getString(R.string.comma)
                                       +getString(R.string.max_base_cm).substring(0,getString(R.string.max_base_cm).length-1)+getString(R.string.comma)
                                       +getString(R.string.oversize)

                                )
                                bw.newLine()

                                var logsCount = 0
                                if(entity.logMeasurementEntity?.logsEntity != null){
                                    logsCount = entity.logMeasurementEntity?.logsEntity?.log_count!!
                                }
                                var minTopCm = 0.0
                                if(entity.logMeasurementEntity?.minTopCm != null){
                                    minTopCm = entity.logMeasurementEntity?.minTopCm!!
                                }

                                var maxBaseCm = 0.0
                                if(entity.logMeasurementEntity?.maxBaseCm != null){
                                    maxBaseCm = entity.logMeasurementEntity?.maxBaseCm!!
                                }

                                var oversize = 0.0
                                if(entity.logMeasurementEntity?.oversizeM != null){
                                    oversize = entity.logMeasurementEntity?.oversizeM!!
                                }


                                bw.write("  "+getString(R.string.comma)+logsCount+getString(R.string.comma)
                                        +""+minTopCm+getString(R.string.comma)
                                        +""+maxBaseCm.toString()+getString(R.string.comma)
                                        +""+oversize.toString()
                                )
                                bw.newLine()

                                bw.newLine()

                                if(entity.logMeasurementEntity?.logsEntity?.add_log_entity_list !=  null &&
                                        entity.logMeasurementEntity?.logsEntity?.add_log_entity_list?.size!! > 0){


                                    val addLogList = ArrayList<AddLogEntity>()
                                    addLogList.addAll(entity.logMeasurementEntity?.logsEntity?.add_log_entity_list!!)


                                    Collections.sort(addLogList, object : Comparator<AddLogEntity> {
                                        override fun compare(lhs: AddLogEntity, rhs: AddLogEntity): Int {

                                            // return Integer.valueOf(lhs.kind).compareTo(Integer.valueOf(rhs.kind))
                                            val value1 = Integer.valueOf(lhs.kind).compareTo(Integer.valueOf(rhs.kind))
                                            if (value1 == 0) {
                                                val value2 = Integer.valueOf(lhs.plate).compareTo(Integer.valueOf(rhs.plate))
                                                return value2
                                            }
                                            return value1


                                        }
                                    })


                                    // Log  MinCm MaxCm OversizeCm
                                    // FSC, PEFC values

                                    bw.write("  "+getString(R.string.comma)+getString(R.string.nr)+getString(R.string.comma)
                                         +getString(R.string.plate)+getString(R.string.comma)
                                           +getString(R.string.species)+getString(R.string.comma)
                                           +getString(R.string.kind)+getString(R.string.comma)
                                           +getString(R.string.quality)+getString(R.string.comma)+
                                            getString(R.string.csv_length_m)+getString(R.string.comma)+
                                           getString(R.string.csv_diameter_cm)+getString(R.string.comma)+
                                            getString(R.string.csv_volume_m3)+getString(R.string.comma)
                                            +getString(R.string.oversize)
                                            +getString(R.string.comma)+getString(R.string.klasse)+getString(R.string.comma)+"RIN"+getString(R.string.comma)+"DE"

                                    )
                                    bw.newLine()

                                    for(i in 0..addLogList.size!!-1) {

                                        var addLogEntity = addLogList.get(i)

                                        if(addLogEntity != null){

                                            var nr = 0
                                            if(addLogEntity?.log_nr != null){
                                                nr = addLogEntity?.log_nr
                                            }
                                            var plate = 0
                                            if(addLogEntity?.plate != null){
                                                plate = addLogEntity?.plate!!
                                            }

                                            var species = " "
                                            if(addLogEntity?.species != null){
                                                species = addLogEntity?.species!!
                                            }

                                            var kind = " "
                                            if(addLogEntity?.kind != null){
                                                kind = addLogEntity?.kind!!
                                            }

                                            var quality = " "
                                            if(addLogEntity?.quality != null){
                                                quality = addLogEntity?.quality!!
                                            }
                                            var length = 0.0
                                            if(addLogEntity?.length_m != null){
                                                length = addLogEntity?.length_m!!
                                            }

                                            var diameter = 0.0
                                            if(addLogEntity?.diameter_cm != null){
                                                diameter = addLogEntity?.diameter_cm!!
                                            }

                                            var volume = 0.0
                                            if(addLogEntity?.volume_m3 != null){
                                                volume = addLogEntity?.volume_m3!!
                                            }

                                            var oversize = 0.0
                                            if(addLogEntity?.oversize_m != null){
                                                oversize = addLogEntity?.oversize_m!!
                                            }

                                            var klasse = "0"
                                            if(addLogEntity?.klasse != null){
                                                klasse = addLogEntity?.klasse!!
                                            }

                                            var barkcm = "0.0"
                                            if(addLogEntity?.klasse != null){
                                                barkcm = addLogEntity?.bark_cm.toString()!!
                                            }


                                            bw.write(getString(R.string.LogInfo)+getString(R.string.comma)+""+nr+getString(R.string.comma)
                                                    +""+plate+getString(R.string.comma)
                                                    +species+getString(R.string.comma)
                                                    +kind+getString(R.string.comma)
                                                    +quality+getString(R.string.comma)
                                                    +length.toString()+getString(R.string.comma)
                                                    +diameter.toString()+getString(R.string.comma)
                                                    +volume.toString()+getString(R.string.comma)
                                                    +oversize.toString()+getString(R.string.comma)
                                                    +klasse+getString(R.string.comma)+barkcm+getString(R.string.comma)+"cm"
                                            )
                                            bw.newLine()
                                        }

                                    }

                                    bw.write(""+getString(R.string.comma)
                                            +""+getString(R.string.comma)
                                            +""+getString(R.string.comma)
                                            +""+getString(R.string.comma)
                                            +""+getString(R.string.comma)
                                            +""+getString(R.string.comma)
                                            +"".toString()+getString(R.string.comma)
                                            +"Total "+getString(R.string.comma)
                                            +entity.logMeasurementEntity?.logsEntity?.total_volume_m3.toString()+getString(R.string.comma)
                                            +""+getString(R.string.comma)
                                            +""+getString(R.string.comma)+""+getString(R.string.comma)+""
                                    )

                                    bw.newLine()

                                    bw.write(" "+getString(R.string.comma)+getString(R.string.kind)+getString(R.string.comma)
                                            +getString(R.string.total_volume)

                                    )
                                    bw.newLine()


//                                    bw.write(" "+getString(R.string.comma))
                                    val hash  = HashMap<Int,String>()

                                    for(i in 0..addLogList.size-1){

                                        if(!hash.containsKey(Integer.parseInt(addLogList.get(i).kind!!))){

                                            hash.put((Integer.parseInt(addLogList.get(i).kind!!)),addLogList.get(i).volume_m3.toString())
                                        }else{
                                            var volume = hash.get(Integer.parseInt(addLogList.get(i).kind))!!.toDouble()
                                            volume = volume + addLogList.get(i).volume_m3
                                            hash.put(Integer.parseInt(addLogList.get(i).kind!!),volume.toString())

                                        }
                                    }

                                    //  val sortedMap = hash.toSortedMap(compareBy { it })
                                    //  val keys = TreeSet<String>(hash.keys)
                                    val entrySet = hash.entries

                                    //Creating an ArrayList of Entry objects

                                    //   val keyList = ArrayList(hash.keys)
                                    //   val valueList = ArrayList(hash.values)
                                    //    val sortedMap = sortHashMapByValues(hash)
//                                    bw.write(" "+getString(R.string.comma))
                                    val set = hash.entries
                                    val iterator = set.iterator()
                                    while (iterator.hasNext())
                                    {
                                        val me = iterator.next() as Map.Entry<Int,String>
                                        System.out.println("Logg "+me.key+": "+me.value)
                                        //System.out.println(me.getValue())
                                    }
                                    val map = TreeMap<Int, String>(hash)
                                    println("After Sorting:")
                                    val set2 = map.entries
                                    val iterator2 = set2.iterator()
                                    while (iterator2.hasNext())
                                    {
                                        val me2 = iterator2.next() as Map.Entry<Int,String>
                                        System.out.println("<Logg"+me2.key+": "+me2.value)
                                        bw.write(" "+getString(R.string.comma)+me2.key+getString(R.string.comma)
                                                +""+me2.value)
                                        bw.newLine()
                                    }

                                    bw.newLine()
                                    bw.write(" "+getString(R.string.comma)+getString(R.string.species)+getString(R.string.comma)+getString(R.string.quality)+getString(R.string.comma)+getString(R.string.klasse)+getString(R.string.comma)
                                            +"Stuck"+ getString(R.string.comma)+getString(R.string.add_log_volume)
                                    )
                                    bw.newLine()

                                    // Overview Table Data

                                    var overviewList = ArrayList<AddLogEntity>()
                                    var specieList = HashMap<String, String>()


                                    for (i in 0..addLogList.size - 1) {

                                        val key = "" + addLogList[i].species + " " + addLogList[i].quality +
                                                " " + addLogList[i].klasse
                                        if (specieList.containsKey(key)) {
                                            val newVolume = specieList.get(key).toString() + " " + addLogList[i].volume_m3.toString()
                                            specieList.put(key, "" + newVolume)
                                        } else {
                                            specieList.put(key, addLogList[i].volume_m3.toString())
                                        }
                                    }

                                    System.out.println("Hash" + specieList.size)

                                    val entrySets = specieList.entries

                                    //Creating an ArrayList of Entry objects

                                    //   val keyList = ArrayList(hash.keys)
                                    //   val valueList = ArrayList(hash.values)
                                    //    val sortedMap = sortHashMapByValues(hash)

                                    val sets = specieList.entries
                                    val iterators = sets.iterator()

                                    val overviewLogList = ArrayList<OverviewLogEntity>()

                                    var totalSumStuck = 0
                                    var totalSumVolume = 0.0

                                    while (iterators.hasNext()) {
                                        val me = iterators.next() as Map.Entry<String, String>
                                        //   rowIndex = rowIndex + 1
                                        //  System.out.println("Hash "+me.key.toString()+" : "+me.value)
                                        if (me.value != null) {

                                            val splited = me.value.toString().split(" ")
                                            if (splited.size > 1) {

                                                val count = splited.size
                                                var volume = 0.0
                                                for (i in 0..splited.size - 1) {
                                                    volume = volume + splited[i].toDouble()
                                                }

                                                val spiltKey = me.key.split(" ")

                                                for (i in 0..spiltKey.size - 1) {

                                                    //       logSpecieDetailTable.addCell(spiltKey.get(i))
                                                    // sheet.addCell(Label(i, rowIndex, spiltKey.get(i)))

                                                }
                                                //  logSpecieDetailTable.addCell(count.toString())

                                                try{
                                                    val updatedVolume = String.format("%.3f", volume)
                                                    //     logSpecieDetailTable.addCell(updatedVolume.toString())
                                                    // sheet.addCell(Label(spiltKey.size + 1, rowIndex, updatedVolume.toString()))

                                                }catch(e : Exception){
                                                    e.printStackTrace()
                                                    //     logSpecieDetailTable.addCell(volume.toString())

                                                    //  sheet.addCell(Label(spiltKey.size + 1, rowIndex, volume.toString()))
                                                }


                                                //sheet.addCell(Label(spiltKey.size + 1, rowIndex, volume.toString()))

                                                overviewLogList.add(OverviewLogEntity(spiltKey.get(0),
                                                        spiltKey.get(1), spiltKey.get(2), count.toString(),
                                                        volume.toString()))



                                                System.out.println("Hash " + me.key.toString() + " : " + me.value.toString() + " " + count + " " + volume)
                                            } else {

                                                val spiltKey = me.key.split(" ")
                                                for (i in 0..spiltKey.size - 1) {

                                                    //   logSpecieDetailTable.addCell(spiltKey.get(i))

                                                    //   sheet.addCell(Label(i, rowIndex, spiltKey.get(i)))

                                                }
                                                //  logSpecieDetailTable.addCell("1")
                                                //   logSpecieDetailTable.addCell(me.value.toString())
                                                //   //   sheet.addCell(Label(spiltKey.size, rowIndex, "1"))
                                                //   sheet.addCell(Label(spiltKey.size + 1, rowIndex, me.value.toString()))
                                                System.out.println("Hash " + me.key.toString() + " : " + me.value)
                                                overviewLogList.add(OverviewLogEntity(spiltKey.get(0),
                                                        spiltKey.get(1), spiltKey.get(2), "1",
                                                        me.value.toString()))

                                            }





                                        }

                                    }

                                    // document.add(logSpecieDetailTable)

                                    val groupOverviewMap = HashMap<String, ArrayList<OverviewLogEntity>>()
                                    var sortedList = ArrayList<OverviewLogEntity>()

                                    for (overview in overviewLogList) {
                                        val key = overview.specie
                                        if (groupOverviewMap.containsKey(key)) {
                                            sortedList = groupOverviewMap.get(key)!!
                                            sortedList.add(overview)

                                        } else {
                                            sortedList = ArrayList<OverviewLogEntity>()
                                            sortedList.add(overview)
                                            groupOverviewMap.put(key, sortedList)

                                        }

                                    }


                                    val overviewset = groupOverviewMap.entries
                                    val overiterator = overviewset.iterator()
                                    while (overiterator.hasNext()) {
                                        val overallTotal = overiterator.next() as Map.Entry<String,ArrayList<OverviewLogEntity>>
                                        System.out.println("SortedHashmap "+overallTotal.key+": "+overallTotal.value)

                                        var totalStuck = 0
                                        var totalVolume = 0.0



                                        val qualityMap = HashMap<String, ArrayList<OverviewLogEntity>>()
                                        var sortedQualityList = ArrayList<OverviewLogEntity>()

                                        for (overview in overallTotal.value) {
                                            val key = overview.quality
                                            if (qualityMap.containsKey(key)) {
                                                sortedQualityList = qualityMap.get(key)!!
                                                sortedQualityList.add(overview)

                                            } else {
                                                sortedQualityList = ArrayList<OverviewLogEntity>()
                                                sortedQualityList.add(overview)
                                                qualityMap.put(key, sortedQualityList)

                                            }

                                        }

                                        //  rowIndex = rowIndex + 1

                                        val overviewQualityset = qualityMap.entries
                                        val overQualityiterator = overviewQualityset.iterator()

                                        while(overQualityiterator.hasNext()){

                                            val me = overQualityiterator.next() as Map.Entry<String,ArrayList<OverviewLogEntity>>
                                            System.out.println("SortedQualityHashmap "+me.key+": "+me.value)

                                            var totalStuck = 0
                                            var totalVolume = 0.0
                                            bw.newLine()

                                            for(i in 0..me.value.size-1){

                                                val overviewEntity = me.value.get(i)

                                                bw.write(" "+getString(R.string.comma)+overviewEntity.specie+getString(R.string.comma)+
                                                        overviewEntity.quality+getString(R.string.comma)+
                                                        overviewEntity.klasse+getString(R.string.comma)+
                                                        overviewEntity.stuck+getString(R.string.comma)+
                                                        overviewEntity.volume+getString(R.string.comma))

                                                bw.newLine()
                                                //   sheet.addCell(Label(0,rowIndex,overviewEntity.specie))
                                                //   sheet.addCell(Label(1,rowIndex,overviewEntity.quality))
                                                //    sheet.addCell(Label(2,rowIndex,overviewEntity.klasse))
                                                //   sheet.addCell(Label(3,rowIndex,overviewEntity.stuck))
                                                //    sheet.addCell(Label(4,rowIndex,overviewEntity.volume))

                                                //   rowIndex = rowIndex + 1

                                                totalStuck = totalStuck + overviewEntity.stuck.toInt()
                                                totalVolume = totalVolume + overviewEntity.volume.toDouble()

                                                System.out.println("Total "+totalStuck+ " "+totalVolume)

                                                if(i == me.value.size-1){

                                                    // logSpecieDetailTable.addCell(makeParagraphBold(me.value.get(0).specie))
                                                    // logSpecieDetailTable.addCell(makeParagraphBold(me.value.get(0).quality))
                                                    //  logSpecieDetailTable.addCell(makeParagraphBold(" "))
                                                    //  logSpecieDetailTable.addCell(makeParagraphBold(totalStuck.toString()))
                                                    //    sheet.addCell(Label(0,rowIndex,me.value.get(0).specie,cellFormatBold))
                                                    //     sheet.addCell(Label(1,rowIndex,me.value.get(0).quality,cellFormatBold))
                                                    //    sheet.addCell(Label(2,rowIndex," ",cellFormatBold))
                                                    //    sheet.addCell(Label(3,rowIndex,totalStuck.toString(),cellFormatBold))

                                                    try{
                                                        val updatedVolume = String.format("%.3f", totalVolume)
                                                        //  logSpecieDetailTable.addCell(makeParagraphBold(updatedVolume.toString()))
                                                        bw.write(" "+getString(R.string.comma) +me.value.get(0).specie+getString(R.string.comma)+
                                                                me.value.get(0).quality+getString(R.string.comma)+
                                                                " "+getString(R.string.comma)+
                                                                totalStuck.toString()+getString(R.string.comma)+
                                                                updatedVolume.toString()+getString(R.string.comma))
                                                        bw.newLine()
                                                        //      sheet.addCell(Label(4,rowIndex,updatedVolume.toString(),cellFormatBold))

                                                    }catch(e : Exception){
                                                        e.printStackTrace()
                                                        // logSpecieDetailTable.addCell(makeParagraphBold(totalVolume.toString()))
                                                        bw.write(" "+getString(R.string.comma)+me.value.get(0).specie+getString(R.string.comma)+
                                                                me.value.get(0).quality+getString(R.string.comma)+
                                                                " "+getString(R.string.comma)+
                                                                totalStuck.toString()+getString(R.string.comma)+
                                                                totalVolume.toString()+getString(R.string.comma))
                                                        bw.newLine()
                                                        //        sheet.addCell(Label(4,rowIndex,totalVolume.toString(),cellFormatBold))
                                                    }

                                                    //  sheet.addCell(Label(4,rowIndex,totalVolume.toString(),cellFormatBold))

                                                    //    rowIndex = rowIndex + 1
                                                }

                                            }

                                        }



                                        for(i in 0..overallTotal.value.size-1){

                                            val overviewEntity = overallTotal.value.get(i)

                                            /*   sheet.addCell(Label(0,rowIndex,overviewEntity.specie))
                                               sheet.addCell(Label(1,rowIndex,overviewEntity.quality))
                                               sheet.addCell(Label(2,rowIndex,overviewEntity.klasse))
                                               sheet.addCell(Label(3,rowIndex,overviewEntity.stuck))
                                               sheet.addCell(Label(4,rowIndex,overviewEntity.volume))*/

                                            totalStuck = totalStuck + overviewEntity.stuck.toInt()
                                            totalVolume = totalVolume + overviewEntity.volume.toDouble()

                                            System.out.println("Totals "+totalStuck+ " "+totalVolume)

                                            if(i == overallTotal.value.size-1){

                                                //   logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(overallTotal.value.get(0).specie))
                                                //   logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(" "))
                                                //   logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(" "))
                                                //  logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(totalStuck.toString()))
                                                //    sheet.addCell(Label(0,rowIndex,overallTotal.value.get(0).specie,boldWithBorder))
                                                //    sheet.addCell(Label(1,rowIndex," ",boldWithBorder))
                                                //    sheet.addCell(Label(2,rowIndex," ",boldWithBorder))
                                                //    sheet.addCell(Label(3,rowIndex,totalStuck.toString(),boldWithBorder))

                                                try{
                                                    val updatedVolume = String.format("%.3f", totalVolume)
                                                    //     logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(updatedVolume.toString()))
                                                    bw.write(" "+getString(R.string.comma)+overallTotal.value.get(0).specie+getString(R.string.comma)+
                                                            " "+getString(R.string.comma)+
                                                            " "+getString(R.string.comma)+
                                                            totalStuck.toString()+getString(R.string.comma)+
                                                            updatedVolume.toString()+getString(R.string.comma))
                                                    bw.newLine()
                                                    //       sheet.addCell(Label(4,rowIndex,updatedVolume.toString(),boldWithBorder))

                                                }catch(e : Exception){
                                                    e.printStackTrace()
                                                    //logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(totalVolume.toString()))
                                                    bw.write(" "+getString(R.string.comma)+overallTotal.value.get(0).specie+getString(R.string.comma)+
                                                            " "+getString(R.string.comma)+
                                                            " "+getString(R.string.comma)+
                                                            totalStuck.toString()+getString(R.string.comma)+
                                                            totalVolume.toString()+getString(R.string.comma))
                                                    bw.newLine()
                                                    //      sheet.addCell(Label(4,rowIndex,totalVolume.toString(),boldWithBorder))
                                                }
                                                //   sheet.addCell(Label(4,rowIndex,totalVolume.toString(),cellFormatBold))

                                                totalSumStuck = totalSumStuck + totalStuck
                                                totalSumVolume = totalSumVolume + totalVolume

                                                //  rowIndex = rowIndex + 1
                                            }

                                        }

                                        //System.out.println(me.getValue())
                                    }


                                    // rowIndex = rowIndex + 1

                                    //  logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(("Total ")))
                                    //   logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(" "))
                                    //   logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(" "))
                                    //   logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(totalSumStuck.toString()))



                                    //  sheet.addCell(Label(0,rowIndex,"Total ",boldWithBorder))
                                    //   sheet.addCell(Label(1,rowIndex," ",boldWithBorder))
                                    //   sheet.addCell(Label(2,rowIndex," ",boldWithBorder))
                                    //   sheet.addCell(Label(3,rowIndex,totalSumStuck.toString(),boldWithBorder))
                                    try{
                                        val updatedVolume = String.format("%.3f", totalSumVolume)
                                        //  logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(updatedVolume.toString()))
                                        bw.write(" "+getString(R.string.comma)+"Total "+getString(R.string.comma)+
                                                " "+getString(R.string.comma)+
                                                " "+getString(R.string.comma)+
                                                totalSumStuck.toString()+getString(R.string.comma)+
                                                updatedVolume.toString()+getString(R.string.comma))
                                        bw.newLine()



                                    }catch(e : Exception){                                        //  sheet.addCell(Label(4,rowIndex,updatedVolume.toString(),boldWithBorder))

                                        e.printStackTrace()
                                        // logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(totalSumVolume.toString().toString()))
                                        bw.write(" "+getString(R.string.comma)+"Total "+getString(R.string.comma)+
                                                " "+getString(R.string.comma)+
                                                " "+getString(R.string.comma)+
                                                totalSumStuck.toString()+getString(R.string.comma)+
                                                totalSumVolume.toString()+getString(R.string.comma))
                                        bw.newLine()
                                        bw.newLine()



                                        //  sheet.addCell(Label(4,rowIndex,totalSumVolume.toString(),boldWithBorder))
                                    }

                                    //  document.add(logSpecieDetailTable)

                                    //  document.add(Paragraph(" "))

                                    // Average of Length, Dimeter and Volume

                                    var totalLength = 0.0
                                    var totalVolumeList = 0.0
                                    var totalDiameter = 0.0
                                    for(i in 0..addLogList.size-1){
                                        totalLength = totalLength + addLogList[i].length_m
                                        totalVolumeList = totalVolumeList + addLogList[i].volume_m3
                                        totalDiameter = totalDiameter + addLogList[i].diameter_cm
                                    }
                                    var avgLength = totalLength/addLogList.size
                                    var avgVolume = totalVolumeList/addLogList.size
                                    var avgDiameter = totalDiameter/addLogList.size

                                    try{
                                        val updatedAvgLength= String.format("%.3f", avgLength)
                                        //   val avgLengthParagraph = addParagraph("D-Lange(m): ",updatedAvgLength.toString())

                                        bw.write(" "+getString(R.string.comma)+"D-Lange(m): "+getString(R.string.comma)+
                                                updatedAvgLength.toString())
                                        bw.newLine()
                                    }catch(e: Exception){
                                        e.printStackTrace()
                                        bw.write(" "+getString(R.string.comma)+"D-Lange(m): "+getString(R.string.comma)+
                                                avgLength.toString())
                                        bw.newLine()
                                    }

                                    try{
                                        val updatedAvgDiameter = String.format("%.3f", avgDiameter)
                                        //  val avgDiaParagraph = addParagraph("D-Durchmesser(cm): ",updatedAvgDiameter.toString())
                                        bw.write(" "+getString(R.string.comma)+"D-Durchmesser(cm): "+getString(R.string.comma)+
                                                updatedAvgDiameter.toString())
                                        bw.newLine()
                                    }catch(e: Exception){
                                        e.printStackTrace()
                                        bw.write(" "+getString(R.string.comma)+"D-Durchmesser(cm): "+getString(R.string.comma)+
                                                avgDiameter.toString())
                                        bw.newLine()
                                    }

                                    try{
                                        val updatedAvgVolume = String.format("%.3f", avgVolume)
                                        // val avgVolParagraph = addParagraph("D-Kubatur(Fm/Stk): ",updatedAvgVolume.toString())
                                        bw.write(" "+getString(R.string.comma)+"D-Kubatur(Fm/Stk): "+getString(R.string.comma)+
                                                updatedAvgVolume.toString())
                                        bw.newLine()
                                    }catch(e: Exception){
                                        e.printStackTrace()
                                        bw.write(" "+getString(R.string.comma)+"D-Kubatur(Fm/Stk): "+getString(R.string.comma)+
                                                avgVolume.toString())
                                        bw.newLine()
                                    }


                                }

                            }


                        }

                    }


                }

            }



            bw.close()
            return true

        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

    }

    // Method for opening a pdf file
    private fun viewPdf(files : File,file: String, directory: String) {

        val pdfFile = File(getExternalStorageDirectory() ,"/" + directory + "/" + file)
        val contentUri = FileProvider.getUriForFile(this, "com.mobipolter.provider",files)
        //val path = contentUri.path

        // Setting the intent for pdf reader
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(contentUri, "application/pdf")
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pdfIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)


        try {
            startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No app found for opening pdf file", Toast.LENGTH_SHORT).show()
        }

    }


    private fun create_PDF_File(logTitle: String): File {

        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        // String imageFileName = type + "_JPG_" + timeStamp + "_";
        val imageFileName = logTitle +"_"+ SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())


        val file = File(storageDir,imageFileName+".pdf")
        return file
        /*return File.createTempFile(
                imageFileName,
                ".pdf",
                storageDir
        )*/
    }

    private fun create_CSV_File(logTitle: String): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        // String imageFileName = type + "_JPG_" + timeStamp + "_";
        val imageFileName = logTitle + "_"+ timeStamp
        val directory = getDir("Images", Context.MODE_PRIVATE)
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(storageDir,imageFileName+".csv")
        return file
        /*return File.createTempFile(
                imageFileName,
                ".csv",
                storageDir
        )*/
    }

    private fun create_Excel_File(logTitle: String): File {

        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        // String imageFileName = type + "_JPG_" + timeStamp + "_";
        val imageFileName = logTitle +"_"+ SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())


        val file = File(storageDir,imageFileName+".xls")
        return file
        /*return File.createTempFile(
                imageFileName,
                ".pdf",
                storageDir
        )*/
    }


    fun clearMultiSelection(){

        isMultiselect = false;
        selectionLayout.visibility = View.GONE
        logEntryAddLayout.visibility = View.VISIBLE
        txtDone.visibility = View.GONE
        txtEdit.visibility = View.VISIBLE
        txtDelete.setTextColor(resources.getColor(R.color.dark_gray_color))
        txtExport.setTextColor(resources.getColor(R.color.dark_gray_color))
        adapter?.multiSelect = false
        adapter?.showSelectedItems = false
        adapter?.enableSelection = false
        adapter?.notifyDataSetChanged()
    }


    // Method for creating a pdf file from text, saving it then opening it for display
    fun createAndDisplayPdf(entityList : List<LogStackEntity>) : Boolean{

        val doc = Document()
        try {

            var filename = ""
            if(entityList.size == 1){
                filename = entityList.get(0).logBasicEntity?.stackNR!!
            }else{
                filename = "LogStack"
            }
            val file = create_PDF_File(filename)
            currentFile = file
            currentFilePath = file.absolutePath
            val fOut = FileOutputStream(file)

            val document = Document()
            val pdfwriter = PdfWriter.getInstance(document, fOut)
            document.open()


            val cb = pdfwriter.getDirectContent()
          //  cb.saveState()
          ///  cb.setColorStroke(BaseColor.BLACK)
         //   cb.rectangle(15.0,10.0,document.pageSize.width.toDouble()-35.0,document.pageSize.height.toDouble()-25.0)
          //  cb.stroke()
          //  cb.restoreState()

            val createdHint = Paragraph(getString(R.string.created_with))
            createdHint.font.setStyle(Font.BOLD)
            createdHint.alignment = Paragraph.ALIGN_LEFT
            document.add(createdHint)
            document.add(Paragraph(" "))


            val inventoryTitle = Paragraph(getString(R.string.inventory_report))
            inventoryTitle.font.setStyle(Font.BOLD)
            inventoryTitle.alignment = Paragraph.ALIGN_CENTER
            document.add(inventoryTitle)
            document.add(Paragraph(" "))

            var p = Paragraph()
           // p.alignment = Paragraph.ALIGN_RIGHT
            if(mAppSharedPreferences.getString("address",null)!= null){

               // val address = Paragraph(mAppSharedPreferences.getString("address",null))
              //  address.alignment = Paragraph.ALIGN_LEFT
               // document.add(address)
                val address = addParagraph(getString(R.string.address)+" : ",mAppSharedPreferences.getString("address",null))
                document.add(address)
                document.add(Paragraph(" "))
                //  val address = Phrase(mAppSharedPreferences.getString("address",null))
                // ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,address, 280f, 20f, 0f)

                //  val ct = ColumnText(canvas)
                //    ct.setSimpleColumn(address, 30f, 5f, 500f, 60f, 15f, Element.ALIGN_LEFT)
                //   ct.go()
                /*  ColumnText.fitText(Font(Font.FontFamily.HELVETICA),mAppSharedPreferences.getString("address",null),
                          Rectangle(10f,10f,200f,100f),12f,1)*/


            }


            if(mAppSharedPreferences.getString("bank",null)!= null){

                val gson = Gson()
                val json = mAppSharedPreferences.getString("bank", "")
                val obj = gson.fromJson<BankEntity>(json, BankEntity::class.java)
                val bankAccountNo = Paragraph(obj.bankAccountNo)
                bankAccountNo.alignment = Paragraph.ALIGN_LEFT
                val bankAccountName = Paragraph(obj.bankAccountName)
                bankAccountName.alignment = Paragraph.ALIGN_LEFT
                val bankAccountIfscCode = Paragraph(obj.ifscBankCode)
                bankAccountIfscCode.alignment = Paragraph.ALIGN_LEFT
               // document.add(bankAccountNo)
              //  document.add(bankAccountName)
              //  document.add(bankAccountIfscCode)

                val bankDetails = addParagraph(getString(R.string.bank)+"  : ","")
                document.add(bankDetails)
                document.add(Paragraph("Account No - "+obj.bankAccountNo+"\n"+
                        "Account Name - "+obj.bankAccountName+"\n"+
                        "IFSC Code - "+obj.ifscBankCode+"\n"+
                        "Other Info - "+obj.bankOtherInfo+"\n"))



                document.add(Paragraph(" "))

             //   val phraseNo = Phrase("Bank Account No - "+obj.bankAccountNo)
             //   val phraseName = Phrase("Account Name - "+obj.bankAccountName)
             //   val phraseCode = Phrase("Bank IFSC Code - "+obj.ifscBankCode)
             //   val phraseOtherInfo = Phrase("Other Info - "+obj.bankOtherInfo)


              //  ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT,phraseNo, 550f, 130f, 0f)
            //    ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT,phraseName, 550f, 110f, 0f)
            //    ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT,phraseCode, 550f, 90f, 0f)
            //    ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT,phraseOtherInfo, 550f, 70f, 0f)

                // phrase1.

            }


            val ims = assets.open("nonwebp.jpg")
            var bmp = BitmapFactory.decodeStream(ims)
          //  bmp = getResizedBitmap(bmp,72,72)
            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)

            if(mAppSharedPreferences.getString("imgLogo",null)!= null){
                        var image = Image.getInstance(mAppSharedPreferences.getString("imgLogo",null))
                        image.alignment = Image.ALIGN_RIGHT
                        image.scaleAbsoluteWidth(72f)
                        image.scaleAbsoluteHeight(72f)
                        image.setAbsolutePosition(500f,750f)

                        document.add(image)
            }


            //val indentation = 0
//whateve   //width = 595 height = 842

          //  val scaler = (document.pageSize.width - document.leftMargin()
          //          - document.rightMargin() - indentation) / image.width * 100

          //  image.scalePercent(scaler)
          //  p.add(image)
         //   var c1 = Chunk(image, 355f, 800f)
          //  p.add(c1)

            document.add(p)

            document.add(Paragraph(" "))
            val canvas = pdfwriter.directContentUnder


            document.addAuthor("Amol Kolhe")
            document.addCreationDate()
            document.addCreator("Amol")
            document.addHeader("Header","Hi")
            document.addSubject("Helloo subject")
            document.addTitle("Title")


            for(entity in entityList) {

                // document.add(Paragraph(text))
                // var document = Document()
                document.add(Paragraph(" "))



               // var tb = PdfPTable(2)
               // tb.tableEvent = BorderEvent()
                var tableNr = PdfPTable(7)
                // table.setWidthPercentage(floatArrayOf(598f), PageSize.LETTER)
                tableNr.totalWidth = document.pageSize.width - 45
                tableNr.getDefaultCell().setHorizontalAlignment(Element.ALIGN_JUSTIFIED)

                tableNr.addCell(getString(R.string.nr))
                //  val pdfpcell1 = PdfPCell(Phrase(getString(R.string.kind)))
                tableNr.addCell(getString(R.string.los_id))
                tableNr.addCell(getString(R.string.foreign_nr))
                tableNr.addCell(getString(R.string.date))
                tableNr.addCell(getString(R.string.location)+" E")
                tableNr.addCell(getString(R.string.location)+" N")
                tableNr.addCell(getString(R.string.comment))
                // tableNr.addCell(getString(R.string.date))

                //  table.addCell(getString(R.string.kind))
                tableNr.setHeaderRows(1)
                tableNr.isLockedWidth = true
                var cellss = tableNr.getRow(0).getCells()
                for (i in 0..cellss.size - 1) {

                    cellss[i].setPadding(5f)

                    if(i == 0){

                        cellss[i].enableBorderSide(Rectangle.TOP)
                        cellss[i].enableBorderSide(Rectangle.LEFT)
                        cellss[i].disableBorderSide(Rectangle.BOTTOM)
                        cellss[i].disableBorderSide(Rectangle.RIGHT)
                        cellss[i].setBackgroundColor(BaseColor.WHITE)
                    }else if(i == cellss.size-1){
                        cellss[i].enableBorderSide(Rectangle.TOP)
                        cellss[i].enableBorderSide(Rectangle.RIGHT)
                        cellss[i].disableBorderSide(Rectangle.BOTTOM)
                        cellss[i].disableBorderSide(Rectangle.LEFT)
                        // cellss[i].setBorder(Rectangle.TOP)
                        cellss[i].setBackgroundColor(BaseColor.WHITE)
                    }else{
                        cellss[i].enableBorderSide(Rectangle.TOP)
                        cellss[i].disableBorderSide(Rectangle.RIGHT)
                        cellss[i].disableBorderSide(Rectangle.BOTTOM)
                        cellss[i].disableBorderSide(Rectangle.LEFT)
                        cellss[i].setBackgroundColor(BaseColor.WHITE)
                    }

                    //   cells[i].setBorder(Rectangle.UNDEFINED);
                    //   cells[i].setBackgroundColor(BaseColor.GRAY);
                }

                if (entity.logBasicEntity?.stackNR != null){
                 //   val logTitle = addParagraph(getString(R.string.nr)+": ",entity.logBasicEntity?.stackNR!!)
                   // document.add(logTitle)
                    val stackCell = getPdfpCell(entity.logBasicEntity?.stackNR!!)
                    stackCell.enableBorderSide(Rectangle.BOTTOM)
                    stackCell.enableBorderSide(Rectangle.LEFT)
                    stackCell.disableBorderSide(Rectangle.TOP)
                    stackCell.disableBorderSide(Rectangle.RIGHT)
                    stackCell.setPadding(2f)
                    tableNr.addCell(stackCell)
                }

                if (entity.logBasicEntity?.iosID != null){
                    //   val logTitle = addParagraph(getString(R.string.nr)+": ",entity.logBasicEntity?.stackNR!!)
                    // document.add(logTitle)
                    val stackCell = getPdfpCell(entity.logBasicEntity?.iosID!!)
                    tableNr.addCell(stackCell)
                }

                if (entity.logBasicEntity?.foreignNR != null){
                    //   val logTitle = addParagraph(getString(R.string.nr)+": ",entity.logBasicEntity?.stackNR!!)
                    // document.add(logTitle)
                    val stackCell = getPdfpCell(entity.logBasicEntity?.foreignNR!!)
                    stackCell.setPadding(2f)
                    tableNr.addCell(stackCell)
                }


                /*if (entity.logBasicEntity?.iosID != null){
                    val logIosId = addParagraph(getString(R.string.los_id),entity.logBasicEntity?.iosID!!)
                    document.add(logIosId)
                }
                if (entity.logBasicEntity?.foreignNR != null){
                    val foreignNR = addParagraph(getString(R.string.foreign_nr),entity.logBasicEntity?.foreignNR!!)
                    document.add(foreignNR)
                }*/

                if (entity.logBasicEntity?.date != null){
                   // val date = addParagraph(getString(R.string.date) + ": ",entity.logBasicEntity?.date!!)
                   // document.add(date)
                    val cell = getPdfpCell(entity.logBasicEntity?.date!!)
                    cell.setPadding(2f)
                    tableNr.addCell(cell)
                }

                if (entity.logLocationEntity != null){
                    val location = addParagraph(getString(R.string.tab_location)+": ",
                    " E: "+entity.logLocationEntity?.longtitude+ " N: "+entity.logLocationEntity?.latitude+
                    "(http://www.google.com/maps/place/"+entity.logLocationEntity?.latitude+","
                            +entity.logLocationEntity?.longtitude+")")


                   /* val paragraph = Paragraph()
                    paragraph.add(Phrase(getString(R.string.tab_location)+
                            " E: "+entity.logLocationEntity?.longtitude+ " N: "+entity.logLocationEntity?.latitude))


                    val anchor = Anchor("http://www.google.com/maps/place/"+entity.logLocationEntity?.latitude+","
                            +entity.logLocationEntity?.longtitude)

                    anchor.setReference(
                            "http://www.google.com/maps/place/"+entity.logLocationEntity?.latitude+","
                                    +entity.logLocationEntity?.longtitude)


                    paragraph.add(anchor)


                    document.add(paragraph)*/


                 //  document.add(location)
                    val cell = getPdfpCell(entity.logLocationEntity?.longtitude.toString()!!)
                    cell.setPadding(2f)
                    tableNr.addCell(cell)

                    val cell1 = getPdfpCell(entity.logLocationEntity?.latitude.toString()!!)
                    cell1.setPadding(2f)

                    tableNr.addCell(cell1)


                }else{
                    val location = addParagraph(getString(R.string.tab_location),
                            " E: 0.000000 N: 0.000000 (http://www.google.com/maps/place/0.000000,0.000000)")
                  //  document.add(location)
                    val cell = getPdfpCell("0.0")
                    cell.setPadding(2f)

                    tableNr.addCell(cell)

                    val cell1 = getPdfpCell("0.0")
                    cell1.setPadding(2f)

                    tableNr.addCell(cell1)
                }

                if (entity.logBasicEntity?.comment != null){
                 //   val comment = addParagraph(getString(R.string.comment) + ": ",entity.logBasicEntity?.comment!!)
                 //   document.add(comment)
                    val cell = getPdfpCell(entity.logBasicEntity?.comment!!)
                    cell.enableBorderSide(Rectangle.BOTTOM)
                    cell.enableBorderSide(Rectangle.RIGHT)
                    cell.disableBorderSide(Rectangle.TOP)
                    cell.disableBorderSide(Rectangle.LEFT)
                    cell.setPadding(2f)

                    tableNr.addCell(cell)

                }else{

                    val cell = getPdfpCell(" ")
                    cell.enableBorderSide(Rectangle.BOTTOM)
                    cell.enableBorderSide(Rectangle.RIGHT)
                    cell.disableBorderSide(Rectangle.TOP)
                    cell.disableBorderSide(Rectangle.LEFT)
                    cell.setPadding(2f)
                    tableNr.addCell(cell)
                }

                document.add(Paragraph(" "))
                document.add(tableNr)
              //  tb.addCell(" ")

              //  document.add(tb)
                document.add(Paragraph(" "))

                var table = PdfPTable(7)
               // table.setWidthPercentage(floatArrayOf(598f), PageSize.LETTER)
                table.totalWidth = document.pageSize.width - document.leftMargin()
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_JUSTIFIED)

                table.addCell(getString(R.string.species))
              //  val pdfpcell1 = PdfPCell(Phrase(getString(R.string.kind)))

                table.addCell(getString(R.string.kind))


              //  table.addCell(getString(R.string.kind))
                table.addCell(getString(R.string.quality))
                table.addCell("FSC")
                table.addCell("PEFC")
                table.addCell(getString(R.string.vol_m3))
                table.addCell(getString(R.string.price))
                table.setHeaderRows(1)
                table.isLockedWidth = true
                var cells = table.getRow(0).getCells()
                for (i in 0..cells.size - 1) {
                    cells[i].setBorder(Rectangle.NO_BORDER)
                    cells[i].setBackgroundColor(BaseColor.WHITE)
                 //   cells[i].setBorder(Rectangle.UNDEFINED);
                 //   cells[i].setBackgroundColor(BaseColor.GRAY);
                }

                if(entity.logSpeciesCount != null)
                    table.addCell(entity.logSpeciesCount)

                if(entity.logKind != null)
                    table.addCell(entity.logKind)

                if(entity.logQuality != null)
                    table.addCell(entity.logQuality)

                if (entity?.logBasicEntity?.fsc == 0) {
                    table.addCell(getString(R.string.no))
                }else{
                    table.addCell(getString(R.string.yes))
                }

                if (entity?.logBasicEntity?.pefc == 0) {
                    table.addCell(getString(R.string.no))
                }else{
                    table.addCell(getString(R.string.yes))
                }

                table.addCell("" + entity.logVolumeM3)
                if(entity.logBasicEntity?.price != null){
                    table.addCell("" + entity.logBasicEntity?.price)
                }else{
                    table.addCell("")
                }

                //  PdfWriter.getInstance(document, FileOutputStream("sample3.pdf"));
                //  document.open();
            //    document.add(table)
            //    document.add(Paragraph(" "))
             //   document.add(Paragraph(" "))
                val basic = Paragraph(getString(R.string.tab_basic))
                basic.font.setStyle(Font.BOLD)
                basic.alignment = Paragraph.ALIGN_CENTER
                document.add(basic)
                var basicDataTable = PdfPTable(7)
                basicDataTable.isLockedWidth = true
                basicDataTable.totalWidth = document.pageSize.width - document.leftMargin()
                basicDataTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER)
                basicDataTable.addCell(getString(R.string.district))
                basicDataTable.addCell(getString(R.string.forest_owner).substring(0,getString(R.string.forest_owner).length-1))
                basicDataTable.addCell(getString(R.string.forester))
                basicDataTable.addCell(getString(R.string.forestry))
                basicDataTable.addCell(getString(R.string.felier))
                basicDataTable.addCell(getString(R.string.clearer))
                basicDataTable.addCell(getString(R.string.skidder))

                basicDataTable.setHeaderRows(1)
                var basicDataCells = basicDataTable.getRow(0).getCells()
                for (i in 0..basicDataCells.size - 1) {
                    basicDataCells[i].setBorder(Rectangle.NO_BORDER)
                    basicDataCells[i].setPadding(5f)
                    //  basicDataCells[i].setBackgroundColor(BaseColor.GRAY);

                }

                if(entity.logBasicEntity?.district != null){
                    basicDataTable.addCell(entity.logBasicEntity?.district)
                }else{
                    basicDataTable.addCell(" ")
                }

                if(entity.logBasicEntity?.forestOwner != null){
                    basicDataTable.addCell(entity.logBasicEntity?.forestOwner)
                }else{

                    basicDataTable.addCell(" ")
                }

                if(entity.logBasicEntity?.forester != null){
                    basicDataTable.addCell(entity.logBasicEntity?.forester)
                }else{
                    basicDataTable.addCell(" ")
                }


                if(entity.logBasicEntity?.forestry != null){
                    basicDataTable.addCell(entity.logBasicEntity?.forestry)
                }else{
                    basicDataTable.addCell(" ")
                }


                if(entity.logBasicEntity?.feller != null){
                    basicDataTable.addCell(entity.logBasicEntity?.feller)
                }else{
                    basicDataTable.addCell(" ")
                }


                if(entity.logBasicEntity?.clearer != null){
                    basicDataTable.addCell(entity.logBasicEntity?.clearer)
                }else{
                    basicDataTable.addCell(" ")
                }

                if(entity.logBasicEntity?.skidder != null){
                    basicDataTable.addCell(entity.logBasicEntity?.skidder)
                }else{
                    basicDataTable.addCell(" ")
                }


                //  PdfWriter.getInstance(document, FileOutputStream("sample3.pdf"));
                //  document.open();
                document.add(Paragraph(" "))
                 document.add(basicDataTable)

             //   document.add(Paragraph(" "))


                if(entity.logMeasurementEntity  != null){
                    document.add(Paragraph(" "))
                    val measure = Paragraph(getString(R.string.tab_measurement))
                    measure.font.setStyle(Font.BOLD)
                    measure.alignment = Paragraph.ALIGN_CENTER
                    document.add(measure)
                }

                //document.add(Paragraph(getString(R.string.tab_measurement)))
                if(entity.logMeasurementEntity?.surveyingType != null) {

                    when (entity.logMeasurementEntity?.surveyingType) {


                        getString(R.string.section_surveying) -> {

                            if(entity.logMeasurementEntity?.sectionEntity != null){

                                document.add(Paragraph(" "))

                                val section = Paragraph(getString(R.string.section_surveying))
                                section.font.setStyle(Font.BOLD)
                                section.alignment = Paragraph.ALIGN_CENTER
                                document.add(section)
                                document.add(Paragraph(" "))

                                var sectionSurveyTable = PdfPTable(6)
                                sectionSurveyTable.isLockedWidth = true
                                sectionSurveyTable.totalWidth = document.pageSize.width - document.leftMargin()
                                sectionSurveyTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER)
                                sectionSurveyTable.addCell(getString(R.string.length_m).subSequence(0,getString(R.string.length_m).length-2).toString())
                                sectionSurveyTable.addCell(getString(R.string.width_m).subSequence(0,getString(R.string.width_m).length-2).toString())
                                sectionSurveyTable.addCell(getString(R.string.logs_count).subSequence(0,getString(R.string.logs_count).length-1).toString())
                                sectionSurveyTable.addCell(getString(R.string.min_top_cm).subSequence(0,getString(R.string.min_top_cm).length-1).toString())
                                sectionSurveyTable.addCell(getString(R.string.max_base_cm).subSequence(0,getString(R.string.max_base_cm).length-1).toString())
                                sectionSurveyTable.addCell(getString(R.string.oversize_m).subSequence(0,getString(R.string.oversize_m).length-1).toString())

                                sectionSurveyTable.setHeaderRows(1)
                                var sectionSurveyCells = sectionSurveyTable.getRow(0).getCells()
                                for (i in 0..sectionSurveyCells.size - 1) {
                                    sectionSurveyCells[i].setBorder(Rectangle.NO_BORDER)
                                    sectionSurveyCells[i].setPadding(3f)
                                }

                                if(entity.logMeasurementEntity?.sectionEntity?.length != null)
                                    sectionSurveyTable.addCell(entity.logMeasurementEntity?.sectionEntity!!.length.toString())

                                if(entity.logMeasurementEntity?.sectionEntity?.width != null)
                                    sectionSurveyTable.addCell(entity.logMeasurementEntity?.sectionEntity?.width.toString())

                                if(entity.logMeasurementEntity?.logsCount!= null)
                                    sectionSurveyTable.addCell(entity.logMeasurementEntity?.logsCount.toString())

                                if(entity.logMeasurementEntity?.minTopCm != null)
                                    sectionSurveyTable.addCell(entity.logMeasurementEntity?.minTopCm.toString())

                                if(entity.logMeasurementEntity?.maxBaseCm != null)
                                    sectionSurveyTable.addCell(entity.logMeasurementEntity?.maxBaseCm.toString())

                                if(entity.logMeasurementEntity?.oversizeM != null)
                                    sectionSurveyTable.addCell(entity.logMeasurementEntity?.oversizeM.toString())

                                document.add(sectionSurveyTable)
                                document.add(Paragraph(" "))

                            }

                            if(entity.logMeasurementEntity?.sectionEntity?.height_list != null &&
                                    entity.logMeasurementEntity?.sectionEntity?.height_list!!.size > 0){

                                var sectionHeightEntryTable = PdfPTable(3)
                                sectionHeightEntryTable.isLockedWidth = true
                                sectionHeightEntryTable.totalWidth = document.pageSize.width - document.leftMargin()

                                sectionHeightEntryTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER)
                                sectionHeightEntryTable.addCell(getString(R.string.nr))
                                sectionHeightEntryTable.addCell(getString(R.string.height_m).subSequence(0,getString(R.string.height_m).length-2).toString())
                                sectionHeightEntryTable.addCell(getString(R.string.section_length_m).subSequence(0,getString(R.string.section_length_m).length-2).toString())

                                sectionHeightEntryTable.setHeaderRows(1)
                                var sectionHeightEntryCells = sectionHeightEntryTable.getRow(0).getCells()
                                for (i in 0..sectionHeightEntryCells.size - 1) {
                                    sectionHeightEntryCells[i].setBorder(Rectangle.NO_BORDER)
                                    sectionHeightEntryCells[i].setPadding(3f)
                                }

                                for(i in 0..entity.logMeasurementEntity?.sectionEntity?.height_list!!.size-1){
                                    val heightListEntity = entity.logMeasurementEntity?.sectionEntity?.height_list!!.get(i)
                                    sectionHeightEntryTable.addCell(heightListEntity.index.toString())
                                    sectionHeightEntryTable.addCell(heightListEntity.height.toString())
                                    sectionHeightEntryTable.addCell(heightListEntity.section.toString())
                                }



                                var sectionOverviewTable = PdfPTable(8)
                                sectionOverviewTable.isLockedWidth = true
                                sectionOverviewTable.totalWidth = document.pageSize.width - document.leftMargin()
                                sectionOverviewTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER)
                                sectionOverviewTable.addCell(getString(R.string.sum_of_sections).subSequence(0,getString(R.string.sum_of_sections).length-1).toString())
                                sectionOverviewTable.addCell(getString(R.string.height_count).subSequence(0,getString(R.string.height_count).length-1).toString())
                                sectionOverviewTable.addCell(getString(R.string.avg_height).subSequence(0,getString(R.string.avg_height).length-1).toString())
                                sectionOverviewTable.addCell(getString(R.string.volume).subSequence(0,getString(R.string.volume).length-1).toString())
                                sectionOverviewTable.addCell(getString(R.string.st).subSequence(0,getString(R.string.st).length-1).toString())
                                sectionOverviewTable.addCell(getString(R.string.distr).subSequence(0,getString(R.string.distr).length-1).toString())
                                sectionOverviewTable.addCell("m3")
                                sectionOverviewTable.addCell(getString(R.string.factor).subSequence(0,getString(R.string.factor).length-1).toString())

                                sectionOverviewTable.setHeaderRows(1)
                                var sectionSurveyCells = sectionOverviewTable.getRow(0).getCells()
                                for (i in 0..sectionSurveyCells.size - 1) {
                                    sectionSurveyCells[i].setBorder(Rectangle.NO_BORDER)
                                    sectionSurveyCells[i].setPadding(3f)

                                }

                                var sumValue = "0"
                                if(entity.logMeasurementEntity?.sectionEntity!!.sum_of_sections.toString() != null){
                                    sumValue = entity.logMeasurementEntity?.sectionEntity!!.sum_of_sections.toString()
                                    sectionOverviewTable.addCell(sumValue)
                                }

                                var heightCountValue = "0"
                                if(entity.logMeasurementEntity?.sectionEntity!!.height_count.toString() != null){
                                    heightCountValue = entity.logMeasurementEntity?.sectionEntity!!.height_count.toString()
                                    sectionOverviewTable.addCell(heightCountValue)
                                }

                                var avgHeightValue = "0.0"
                                if(entity.logMeasurementEntity?.sectionEntity!!.avg_height.toString() != null){
                                    avgHeightValue = entity.logMeasurementEntity?.sectionEntity!!.avg_height.toString()
                                    sectionOverviewTable.addCell(avgHeightValue)
                                }

                                var volumeBarkValue = "0.0"
                                if(entity.logMeasurementEntity?.sectionEntity!!.volume_bark.toString() != null){
                                    volumeBarkValue = entity.logMeasurementEntity?.sectionEntity!!.volume_bark.toString()
                                    sectionOverviewTable.addCell(volumeBarkValue)
                                }

                                var stValue = "0.0"
                                if(entity.logMeasurementEntity?.sectionEntity!!.st.toString() != null){
                                    stValue = entity.logMeasurementEntity?.sectionEntity!!.st.toString()
                                    sectionOverviewTable.addCell(stValue)
                                }


                                var distrValue = "0.0"
                                if(entity.logMeasurementEntity?.sectionEntity!!.distr.toString() != null){
                                    distrValue = entity.logMeasurementEntity?.sectionEntity!!.distr.toString()
                                    sectionOverviewTable.addCell(distrValue)
                                }


                                var m3Value = "0.0"
                                if(entity.logMeasurementEntity?.sectionEntity!!.m3.toString() != null){
                                    m3Value = entity.logMeasurementEntity?.sectionEntity!!.m3.toString()
                                    sectionOverviewTable.addCell(m3Value)

                                }


                                var factorValue = "0.0"
                                if(entity.logMeasurementEntity?.sectionEntity!!.factor.toString() != null){
                                    factorValue = entity.logMeasurementEntity?.sectionEntity!!.factor.toString()
                                    sectionOverviewTable.addCell(factorValue)

                                }




                                document.add(Paragraph(" "))
                                document.add(sectionHeightEntryTable)
                                document.add(Paragraph(" "))
                                document.add(sectionOverviewTable)


                            }

                            document.add(Paragraph(" "))
                        }

                        getString(R.string.estimation_surveying) -> {

                            if(entity.logMeasurementEntity?.estimationEntity != null){
                                val log = Paragraph(getString(R.string.estimation_surveying))
                                log.font.setStyle(Font.BOLD)
                                log.alignment = Paragraph.ALIGN_CENTER
                                document.add(log)
                                document.add(Paragraph(" "))


                                var estTable = PdfPTable(floatArrayOf(5f,5f,5f,5f,5f,5f,5f,5f))
                                estTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER)
                                estTable.addCell(getString(R.string.species))
                                estTable.addCell(getString(R.string.kind))
                                estTable.addCell(getString(R.string.quality))
                                estTable.addCell(getString(R.string.size))
                                estTable.addCell(getString(R.string.length_m))
                                estTable.addCell(getString(R.string.width_m).subSequence(0,getString(R.string.length_m).length-2).toString())
                                estTable.addCell(getString(R.string.unit).subSequence(0,getString(R.string.unit).length-1).toString())
                                estTable.addCell(getString(R.string.volume_value).subSequence(0,getString(R.string.volume_value).length-1).toString())


                                estTable.setHeaderRows(1)
                                var logTableCells = estTable.getRow(0).getCells()
                                for (i in 0..logTableCells.size - 1) {
                                    logTableCells[i].setBackgroundColor(BaseColor.GRAY);
                                }

                                if(entity.logMeasurementEntity?.estimationEntity?.species != null)
                                    estTable.addCell(entity.logMeasurementEntity?.estimationEntity!!.species.toString())

                                if(entity.logMeasurementEntity?.estimationEntity?.kind != null)
                                    estTable.addCell(entity.logMeasurementEntity?.estimationEntity!!.kind.toString())

                                if(entity.logMeasurementEntity?.estimationEntity?.quality != null)
                                    estTable.addCell(entity.logMeasurementEntity?.estimationEntity!!.quality.toString())

                                if(entity.logMeasurementEntity?.estimationEntity?.size != null)
                                    estTable.addCell(entity.logMeasurementEntity?.estimationEntity!!.size.toString())

                                if(entity.logMeasurementEntity?.estimationEntity?.length != null)
                                    estTable.addCell(entity.logMeasurementEntity?.estimationEntity!!.length.toString())

                                if(entity.logMeasurementEntity?.estimationEntity?.width != null)
                                    estTable.addCell(entity.logMeasurementEntity?.estimationEntity!!.width.toString())

                                if(entity.logMeasurementEntity?.estimationEntity?.unit != null)
                                    estTable.addCell(entity.logMeasurementEntity?.estimationEntity!!.unit.toString())

                                if(entity.logMeasurementEntity?.estimationEntity?.volume != null)
                                    estTable.addCell(entity.logMeasurementEntity?.estimationEntity!!.volume.toString())


                                document.add(estTable)
                                document.add(Paragraph(" "))


                            }


                        }


                        getString(R.string.logs_surveying) -> {


                            if(entity.logMeasurementEntity?.logsEntity != null){
                                val log = Paragraph(getString(R.string.logs_surveying))
                                log.font.setStyle(Font.BOLD)
                                log.alignment = Paragraph.ALIGN_CENTER
                              //  document.add(log)
                             //   document.add(Paragraph(" "))
                            }


                            if(entity.logMeasurementEntity?.logsEntity?.add_log_entity_list != null &&
                                    entity.logMeasurementEntity?.logsEntity?.add_log_entity_list!!.size > 0){


                                var logCount = entity.logMeasurementEntity?.logsEntity?.log_count
                                var p = addParagraph(getString(R.string.logs_count),logCount.toString())
                               // document.add(p)

                                var totalVolume = entity.logMeasurementEntity?.logsEntity?.total_volume_m3
                                var p1 = addParagraph(getString(R.string.total_volume)+": ",totalVolume.toString())
                             //   document.add(p1)

                                var logOverviewTable = PdfPTable(4)
                                logOverviewTable.totalWidth = document.pageSize.width - document.leftMargin()
                                logOverviewTable.isLockedWidth = true
                                logOverviewTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER)
                                logOverviewTable.addCell(getString(R.string.logs_count).subSequence(0,getString(R.string.logs_count).length-1).toString())
                                logOverviewTable.addCell(getString(R.string.min_top_cm).subSequence(0,getString(R.string.min_top_cm).length-1).toString())
                                logOverviewTable.addCell(getString(R.string.max_base_cm).subSequence(0,getString(R.string.max_base_cm).length-1).toString())
                                logOverviewTable.addCell(getString(R.string.oversize_m).subSequence(0,getString(R.string.oversize_m).length-1).toString())

                                logOverviewTable.setHeaderRows(1)
                                var logOverviewCells = logOverviewTable.getRow(0).getCells()
                                for (i in 0..logOverviewCells.size - 1) {
                                    logOverviewCells[i].setBackgroundColor(BaseColor.WHITE)
                                    logOverviewCells[i].border = Rectangle.NO_BORDER
                                    logOverviewCells[i].setPadding(5f)
                                }


                                if(entity.logMeasurementEntity?.logsCount!= null)
                                    logOverviewTable.addCell(entity.logMeasurementEntity?.logsCount.toString())

                                if(entity.logMeasurementEntity?.minTopCm != null)
                                    logOverviewTable.addCell(entity.logMeasurementEntity?.minTopCm.toString())

                                if(entity.logMeasurementEntity?.maxBaseCm != null)
                                    logOverviewTable.addCell(entity.logMeasurementEntity?.maxBaseCm.toString())

                                if(entity.logMeasurementEntity?.oversizeM != null)
                                    logOverviewTable.addCell(entity.logMeasurementEntity?.oversizeM.toString())

                                document.add(logOverviewTable)

                                val log = Paragraph(getString(R.string.logs))
                                log.font.setStyle(Font.BOLD)
                                log.alignment = Paragraph.ALIGN_CENTER
                               // document.add(log)
                                document.add(Paragraph(" "))
                                document.add(Paragraph(" "))


                                var logTable = PdfPTable(12)
                                logTable.totalWidth = document.pageSize.width - document.leftMargin()
                                logTable.isLockedWidth = true

                                // logTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_JUSTIFIED)
                                logTable.addCell(getString(R.string.nr))
                                logTable.addCell(getString(R.string.plate))
                                logTable.addCell(getString(R.string.species))
                                logTable.addCell(getString(R.string.kind))
                                logTable.addCell(getString(R.string.quality))
                                logTable.addCell(getString(R.string.length_m).subSequence(0,getString(R.string.length_m).length-1).toString())
                                logTable.addCell(getString(R.string.add_log_diameter_cm).subSequence(0,getString(R.string.add_log_diameter_cm).length-1).toString())
                                logTable.addCell(getString(R.string.add_log_volume))
                                logTable.addCell(getString(R.string.oversize))
                                logTable.addCell(getString(R.string.klasse))
                                logTable.addCell("RIN")
                                logTable.addCell("DE")

                                logTable.setHeaderRows(1)
                                var logTableCells = logTable.getRow(0).getCells()
                                for (i in 0..logTableCells.size - 1) {
                                   // logTableCells[i].setBackgroundColor(BaseColor.GRAY);
                                    logTableCells[i].setBorder(Rectangle.NO_BORDER)
                                    logTableCells[i].setBorderColorLeft(BaseColor.BLACK)
                                   // cells[i].setBackgroundColor(BaseColor.WHITE)
                                }

                                val addLogList = ArrayList<AddLogEntity>()
                                addLogList.addAll(entity.logMeasurementEntity?.logsEntity?.add_log_entity_list!!)


                               Collections.sort(addLogList, object : Comparator<AddLogEntity> {
                                    override fun compare(lhs: AddLogEntity, rhs: AddLogEntity): Int {

                                       // return Integer.valueOf(lhs.kind).compareTo(Integer.valueOf(rhs.kind))
                                        val value1 = Integer.valueOf(lhs.kind).compareTo(Integer.valueOf(rhs.kind))
                                        if (value1 == 0) {
                                            val value2 = Integer.valueOf(lhs.plate).compareTo(Integer.valueOf(rhs.plate))
                                            return value2
                                        }
                                        return value1


                                    }
                                })


                                for(i in 0..addLogList!!.size-1){
                                    val addLogEntity = addLogList!!.get(i)

                                    logTable.addCell(addLogEntity.log_nr.toString())
                                    logTable.addCell(addLogEntity.plate.toString())
                                    logTable.addCell(addLogEntity.species.toString())
                                    logTable.addCell(addLogEntity.kind.toString())
                                    logTable.addCell(addLogEntity.quality.toString())
                                    logTable.addCell(addLogEntity.length_m.toString())
                                    logTable.addCell(addLogEntity.diameter_cm.toString())
                                    logTable.addCell(addLogEntity.volume_m3.toString())
                                    logTable.addCell(addLogEntity.oversize_m.toString())
                                    logTable.addCell(addLogEntity.klasse.toString())
                                    logTable.addCell(addLogEntity.bark_cm.toString())
                                    logTable.addCell("cm")
                                }

                                val pdfpcell1 = PdfPCell(Phrase(" "))
                                pdfpcell1.border = Rectangle.BOX
                                pdfpcell1.borderColor = BaseColor.BLACK
                                pdfpcell1.colspan = 6
                                logTable.addCell(pdfpcell1)
                                var phrease = Phrase("Total        "+totalVolume)
                                phrease.font.setStyle(Font.BOLD)
                                val pdfpcell = PdfPCell(phrease)
                                pdfpcell.border = Rectangle.BOX
                                pdfpcell.borderColor = BaseColor.BLACK
                                pdfpcell.paddingBottom = 5f
                                pdfpcell.colspan = 2
                                logTable.addCell(pdfpcell)
                                var phrease1 = Phrase(" "+totalVolume)
                                phrease1.font.setStyle(Font.BOLD)
                                val cell = PdfPCell(Phrase(phrease1))
                                cell.border = Rectangle.BOX
                                cell.borderColor = BaseColor.BLACK
                              //  logTable.addCell(cell)
                               // logTable.addCell(" "+totalVolume)
                                val cell1 = PdfPCell(Phrase(" "))
                                cell1.border = Rectangle.BOX
                                cell1.borderColor = BaseColor.BLACK
                                cell1.colspan = 4
                                logTable.addCell(cell1)
                               // logTable.addCell(PdfPCell(Phrase(" ")))

                                logTable.addCell(getPdfpCellWithNoBorder(" "))
                                logTable.addCell(getPdfpCellWithNoBorder(" "))
                                logTable.addCell(getPdfpCellWithNoBorder(" "))
                                logTable.addCell(getPdfpCellWithNoBorder(" "))
                                logTable.addCell(getPdfpCellWithNoBorder(" "))
                                logTable.addCell(getPdfpCellWithNoBorder(" "))
                                logTable.addCell(getPdfpCellWithNoBorder(getString(R.string.kind)))
                                logTable.addCell(getPdfpCellWithNoBorder(getString(R.string.total_volume)))
                                logTable.addCell(getPdfpCellWithNoBorder(" "))
                                logTable.addCell(getPdfpCellWithNoBorder(" "))
                                logTable.addCell(getPdfpCellWithNoBorder(" "))
                                logTable.addCell(getPdfpCellWithNoBorder(" "))



                              //  document.add(logTable)
                                document.add(Paragraph(" "))

                                var totalVolumeTable = PdfPTable(2)
                                totalVolumeTable.isLockedWidth = true
                                totalVolumeTable.totalWidth = document.pageSize.width - document.leftMargin()

                                totalVolumeTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER)
                                // logTable.addCell(getString(R.string.nr))
                                totalVolumeTable.addCell(getString(R.string.kind))
                                totalVolumeTable.addCell(getString(R.string.total_volume))

                                totalVolumeTable.setHeaderRows(1)
                                var totalVolumeTableCells = totalVolumeTable.getRow(0).getCells()
                                for (i in 0..totalVolumeTableCells.size - 1) {
                                    totalVolumeTableCells[i].setBorder(Rectangle.NO_BORDER)
                                    totalVolumeTableCells[i].setBorderColorLeft(BaseColor.BLACK)
                                }

                               // val addLogList = entity.logMeasurementEntity?.logsEntity?.add_log_entity_list!!
                            //    val totalVolumeList = ArrayList<TotalVolumeEntity>()
                                val hash  = HashMap<Int,String>()

                                for(i in 0..addLogList.size-1){

                                    if(!hash.containsKey(Integer.parseInt(addLogList.get(i).kind!!))){
                                        hash.put((Integer.parseInt(addLogList.get(i).kind!!)),addLogList.get(i).volume_m3.toString())
                                    }else{
                                        var volume = hash.get(Integer.parseInt(addLogList.get(i).kind))!!.toDouble()
                                        volume = volume + addLogList.get(i).volume_m3
                                        hash.put(Integer.parseInt(addLogList.get(i).kind!!),volume.toString())

                                    }
                                }

                             //  val sortedMap = hash.toSortedMap(compareBy { it })
                              //  val keys = TreeSet<String>(hash.keys)
                                val entrySet = hash.entries

                                //Creating an ArrayList of Entry objects

                             //   val keyList = ArrayList(hash.keys)
                             //   val valueList = ArrayList(hash.values)
                            //    val sortedMap = sortHashMapByValues(hash)

                                val set = hash.entries
                                val iterator = set.iterator()
                                while (iterator.hasNext())
                                {
                                    val me = iterator.next() as Map.Entry<Int,String>
                                    System.out.println("Logg "+me.key+": "+me.value)
                                    //System.out.println(me.getValue())
                                }
                                val map = TreeMap<Int, String>(hash)
                                println("After Sorting:")
                                val set2 = map.entries
                                val iterator2 = set2.iterator()
                                while (iterator2.hasNext())
                                {
                                    val me2 = iterator2.next() as Map.Entry<Int,String>
                                    System.out.println("<Logg"+me2.key+": "+me2.value)


                                    totalVolumeTable.addCell(me2.key.toString())
                                    totalVolumeTable.addCell(me2.value.toString())
                                    logTable.addCell(getPdfpCellWithNoBorder(" "))
                                    logTable.addCell(getPdfpCellWithNoBorder(" "))
                                    logTable.addCell(getPdfpCellWithNoBorder(" "))
                                    logTable.addCell(getPdfpCellWithNoBorder(" "))
                                    logTable.addCell(getPdfpCellWithNoBorder(" "))

                                    logTable.addCell(getPdfpCellWithNoBorder(" "))

                                    var keyPhrase = Phrase(me2.key.toString())
                                    keyPhrase.font.setStyle(Font.BOLD)
                                   // logTable.addCell(me2.key.toString())
                                    var valuePhrase = Phrase(me2.value.toString())
                                    valuePhrase.font.setStyle(Font.BOLD)
                                    var valueCell = PdfPCell(valuePhrase)
                                    valueCell.setBorder(Rectangle.BOX)
                                    valueCell.setBackgroundColor(BaseColor.WHITE)

                                    var keyCell = PdfPCell(keyPhrase)
                                    keyCell.setBorder(Rectangle.BOX)
                                    keyCell.setBackgroundColor(BaseColor.WHITE)
                                    logTable.addCell(keyCell)
                                    logTable.addCell(valueCell)
                                    logTable.addCell(getPdfpCellWithNoBorder(" "))
                                    logTable.addCell(getPdfpCellWithNoBorder(" "))
                                    logTable.addCell(getPdfpCellWithNoBorder(" "))
                                    logTable.addCell(getPdfpCellWithNoBorder(" "))

                                }

                                document.add(Paragraph(" "))

                                document.add(logTable)


                                // Overview Table Data

                                var overviewList = ArrayList<AddLogEntity>()
                                var specieList = HashMap<String, String>()

                                var logSpecieDetailTable = PdfPTable(5)
                                logSpecieDetailTable.totalWidth = document.pageSize.width - document.leftMargin()
                                logSpecieDetailTable.isLockedWidth = true

                                // logTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_JUSTIFIED)
                                logSpecieDetailTable.addCell(getString(R.string.species))
                                logSpecieDetailTable.addCell(getString(R.string.quality))
                                logSpecieDetailTable.addCell(getString(R.string.klasse))
                                logSpecieDetailTable.addCell("Stuck")
                                logSpecieDetailTable.addCell(getString(R.string.add_log_volume))

                                logSpecieDetailTable.setHeaderRows(1)
                                var logSpecieDetailTableCells = logSpecieDetailTable.getRow(0).getCells()
                                for (i in 0..logSpecieDetailTableCells.size - 1) {
                                    // logTableCells[i].setBackgroundColor(BaseColor.GRAY);
                                    logSpecieDetailTableCells[i].setBorder(Rectangle.NO_BORDER)
                                    logSpecieDetailTableCells[i].setBorderColorLeft(BaseColor.BLACK)
                                    logSpecieDetailTableCells[i].setPadding(5f)
                                    // cells[i].setBackgroundColor(BaseColor.WHITE)
                                }

                                for (i in 0..addLogList.size - 1) {

                                    val key = "" + addLogList[i].species + " " + addLogList[i].quality +
                                            " " + addLogList[i].klasse
                                    if (specieList.containsKey(key)) {
                                        val newVolume = specieList.get(key).toString() + " " + addLogList[i].volume_m3.toString()
                                        specieList.put(key, "" + newVolume)
                                    } else {
                                        specieList.put(key, addLogList[i].volume_m3.toString())
                                    }
                                }

                                System.out.println("Hash" + specieList.size)

                                val entrySets = specieList.entries

                                //Creating an ArrayList of Entry objects

                                //   val keyList = ArrayList(hash.keys)
                                //   val valueList = ArrayList(hash.values)
                                //    val sortedMap = sortHashMapByValues(hash)

                                val sets = specieList.entries
                                val iterators = sets.iterator()

                                val overviewLogList = ArrayList<OverviewLogEntity>()

                                var totalSumStuck = 0
                                var totalSumVolume = 0.0

                                while (iterators.hasNext()) {
                                    val me = iterators.next() as Map.Entry<String, String>
                                    //   rowIndex = rowIndex + 1
                                    //  System.out.println("Hash "+me.key.toString()+" : "+me.value)
                                    if (me.value != null) {

                                        val splited = me.value.toString().split(" ")
                                        if (splited.size > 1) {

                                            val count = splited.size
                                            var volume = 0.0
                                            for (i in 0..splited.size - 1) {
                                                volume = volume + splited[i].toDouble()
                                            }

                                            val spiltKey = me.key.split(" ")

                                            for (i in 0..spiltKey.size - 1) {

                                         //       logSpecieDetailTable.addCell(spiltKey.get(i))
                                               // sheet.addCell(Label(i, rowIndex, spiltKey.get(i)))

                                            }
                                          //  logSpecieDetailTable.addCell(count.toString())

                                            try{
                                                val updatedVolume = String.format("%.3f", volume)
                                           //     logSpecieDetailTable.addCell(updatedVolume.toString())
                                               // sheet.addCell(Label(spiltKey.size + 1, rowIndex, updatedVolume.toString()))

                                            }catch(e : Exception){
                                                e.printStackTrace()
                                           //     logSpecieDetailTable.addCell(volume.toString())

                                                //  sheet.addCell(Label(spiltKey.size + 1, rowIndex, volume.toString()))
                                            }


                                            //sheet.addCell(Label(spiltKey.size + 1, rowIndex, volume.toString()))

                                            overviewLogList.add(OverviewLogEntity(spiltKey.get(0),
                                                    spiltKey.get(1), spiltKey.get(2), count.toString(),
                                                    volume.toString()))



                                            System.out.println("Hash " + me.key.toString() + " : " + me.value.toString() + " " + count + " " + volume)
                                        } else {

                                            val spiltKey = me.key.split(" ")
                                            for (i in 0..spiltKey.size - 1) {

                                             //   logSpecieDetailTable.addCell(spiltKey.get(i))

                                                //   sheet.addCell(Label(i, rowIndex, spiltKey.get(i)))

                                            }
                                          //  logSpecieDetailTable.addCell("1")
                                         //   logSpecieDetailTable.addCell(me.value.toString())
                                         //   //   sheet.addCell(Label(spiltKey.size, rowIndex, "1"))
                                            //   sheet.addCell(Label(spiltKey.size + 1, rowIndex, me.value.toString()))
                                            System.out.println("Hash " + me.key.toString() + " : " + me.value)
                                            overviewLogList.add(OverviewLogEntity(spiltKey.get(0),
                                                    spiltKey.get(1), spiltKey.get(2), "1",
                                                    me.value.toString()))

                                        }





                                    }

                                }

                               // document.add(logSpecieDetailTable)

                                val groupOverviewMap = HashMap<String, ArrayList<OverviewLogEntity>>()
                                var sortedList = ArrayList<OverviewLogEntity>()

                                for (overview in overviewLogList) {
                                    val key = overview.specie
                                    if (groupOverviewMap.containsKey(key)) {
                                        sortedList = groupOverviewMap.get(key)!!
                                        sortedList.add(overview)

                                    } else {
                                        sortedList = ArrayList<OverviewLogEntity>()
                                        sortedList.add(overview)
                                        groupOverviewMap.put(key, sortedList)

                                    }

                                }


                                val overviewset = groupOverviewMap.entries
                                val overiterator = overviewset.iterator()
                                while (overiterator.hasNext()) {
                                    val overallTotal = overiterator.next() as Map.Entry<String,ArrayList<OverviewLogEntity>>
                                    System.out.println("SortedHashmap "+overallTotal.key+": "+overallTotal.value)

                                    var totalStuck = 0
                                    var totalVolume = 0.0



                                    val qualityMap = HashMap<String, ArrayList<OverviewLogEntity>>()
                                    var sortedQualityList = ArrayList<OverviewLogEntity>()

                                    for (overview in overallTotal.value) {
                                        val key = overview.quality
                                        if (qualityMap.containsKey(key)) {
                                            sortedQualityList = qualityMap.get(key)!!
                                            sortedQualityList.add(overview)

                                        } else {
                                            sortedQualityList = ArrayList<OverviewLogEntity>()
                                            sortedQualityList.add(overview)
                                            qualityMap.put(key, sortedQualityList)

                                        }

                                    }

                                  //  rowIndex = rowIndex + 1

                                    val overviewQualityset = qualityMap.entries
                                    val overQualityiterator = overviewQualityset.iterator()

                                    while(overQualityiterator.hasNext()){

                                        val me = overQualityiterator.next() as Map.Entry<String,ArrayList<OverviewLogEntity>>
                                        System.out.println("SortedQualityHashmap "+me.key+": "+me.value)

                                        var totalStuck = 0
                                        var totalVolume = 0.0

                                        for(i in 0..me.value.size-1){

                                            val overviewEntity = me.value.get(i)
                                            logSpecieDetailTable.addCell(overviewEntity.specie)
                                            logSpecieDetailTable.addCell(overviewEntity.quality)
                                            logSpecieDetailTable.addCell(overviewEntity.klasse)
                                            logSpecieDetailTable.addCell(overviewEntity.stuck)
                                            logSpecieDetailTable.addCell(overviewEntity.volume)
                                         //   sheet.addCell(Label(0,rowIndex,overviewEntity.specie))
                                         //   sheet.addCell(Label(1,rowIndex,overviewEntity.quality))
                                        //    sheet.addCell(Label(2,rowIndex,overviewEntity.klasse))
                                         //   sheet.addCell(Label(3,rowIndex,overviewEntity.stuck))
                                        //    sheet.addCell(Label(4,rowIndex,overviewEntity.volume))

                                         //   rowIndex = rowIndex + 1

                                            totalStuck = totalStuck + overviewEntity.stuck.toInt()
                                            totalVolume = totalVolume + overviewEntity.volume.toDouble()

                                            System.out.println("Total "+totalStuck+ " "+totalVolume)

                                            if(i == me.value.size-1){

                                                logSpecieDetailTable.addCell(makeParagraphBold(me.value.get(0).specie))
                                                logSpecieDetailTable.addCell(makeParagraphBold(me.value.get(0).quality))
                                                logSpecieDetailTable.addCell(makeParagraphBold(" "))
                                                logSpecieDetailTable.addCell(makeParagraphBold(totalStuck.toString()))
                                            //    sheet.addCell(Label(0,rowIndex,me.value.get(0).specie,cellFormatBold))
                                           //     sheet.addCell(Label(1,rowIndex,me.value.get(0).quality,cellFormatBold))
                                            //    sheet.addCell(Label(2,rowIndex," ",cellFormatBold))
                                            //    sheet.addCell(Label(3,rowIndex,totalStuck.toString(),cellFormatBold))

                                                try{
                                                    val updatedVolume = String.format("%.3f", totalVolume)
                                                    logSpecieDetailTable.addCell(makeParagraphBold(updatedVolume.toString()))
                                              //      sheet.addCell(Label(4,rowIndex,updatedVolume.toString(),cellFormatBold))

                                                }catch(e : Exception){
                                                    e.printStackTrace()
                                                    logSpecieDetailTable.addCell(makeParagraphBold(totalVolume.toString()))
                                            //        sheet.addCell(Label(4,rowIndex,totalVolume.toString(),cellFormatBold))
                                                }

                                                //  sheet.addCell(Label(4,rowIndex,totalVolume.toString(),cellFormatBold))

                                            //    rowIndex = rowIndex + 1
                                            }

                                        }

                                    }



                                    for(i in 0..overallTotal.value.size-1){

                                        val overviewEntity = overallTotal.value.get(i)

                                        /*   sheet.addCell(Label(0,rowIndex,overviewEntity.specie))
                                           sheet.addCell(Label(1,rowIndex,overviewEntity.quality))
                                           sheet.addCell(Label(2,rowIndex,overviewEntity.klasse))
                                           sheet.addCell(Label(3,rowIndex,overviewEntity.stuck))
                                           sheet.addCell(Label(4,rowIndex,overviewEntity.volume))*/

                                        totalStuck = totalStuck + overviewEntity.stuck.toInt()
                                        totalVolume = totalVolume + overviewEntity.volume.toDouble()

                                        System.out.println("Totals "+totalStuck+ " "+totalVolume)

                                        if(i == overallTotal.value.size-1){

                                            logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(overallTotal.value.get(0).specie))
                                            logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(" "))
                                            logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(" "))
                                            logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(totalStuck.toString()))
                                        //    sheet.addCell(Label(0,rowIndex,overallTotal.value.get(0).specie,boldWithBorder))
                                        //    sheet.addCell(Label(1,rowIndex," ",boldWithBorder))
                                        //    sheet.addCell(Label(2,rowIndex," ",boldWithBorder))
                                        //    sheet.addCell(Label(3,rowIndex,totalStuck.toString(),boldWithBorder))

                                            try{
                                                val updatedVolume = String.format("%.3f", totalVolume)
                                                logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(updatedVolume.toString()))
                                         //       sheet.addCell(Label(4,rowIndex,updatedVolume.toString(),boldWithBorder))

                                            }catch(e : Exception){
                                                e.printStackTrace()
                                                logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(totalVolume.toString()))
                                          //      sheet.addCell(Label(4,rowIndex,totalVolume.toString(),boldWithBorder))
                                            }
                                            //   sheet.addCell(Label(4,rowIndex,totalVolume.toString(),cellFormatBold))

                                            totalSumStuck = totalSumStuck + totalStuck
                                            totalSumVolume = totalSumVolume + totalVolume

                                          //  rowIndex = rowIndex + 1
                                        }

                                    }

                                    //System.out.println(me.getValue())
                                }


                               // rowIndex = rowIndex + 1

                                logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(("Total ")))
                                logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(" "))
                                logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(" "))
                                logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(totalSumStuck.toString()))
                                //  sheet.addCell(Label(0,rowIndex,"Total ",boldWithBorder))
                             //   sheet.addCell(Label(1,rowIndex," ",boldWithBorder))
                             //   sheet.addCell(Label(2,rowIndex," ",boldWithBorder))
                             //   sheet.addCell(Label(3,rowIndex,totalSumStuck.toString(),boldWithBorder))
                                try{
                                    val updatedVolume = String.format("%.3f", totalSumVolume)
                                    logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(updatedVolume.toString()))

                                    //  sheet.addCell(Label(4,rowIndex,updatedVolume.toString(),boldWithBorder))

                                }catch(e : Exception){
                                    e.printStackTrace()
                                    logSpecieDetailTable.addCell(getPdfpCellWithBlackBorder(totalSumVolume.toString().toString()))

                                    //  sheet.addCell(Label(4,rowIndex,totalSumVolume.toString(),boldWithBorder))
                                }

                                document.add(logSpecieDetailTable)

                                document.add(Paragraph(" "))

                                // Average of Length, Dimeter and Volume

                                var totalLength = 0.0
                                var totalVolumeList = 0.0
                                var totalDiameter = 0.0
                                for(i in 0..addLogList.size-1){
                                    totalLength = totalLength + addLogList[i].length_m
                                    totalVolumeList = totalVolumeList + addLogList[i].volume_m3
                                    totalDiameter = totalDiameter + addLogList[i].diameter_cm
                                }
                                var avgLength = totalLength/addLogList.size
                                var avgVolume = totalVolumeList/addLogList.size
                                var avgDiameter = totalDiameter/addLogList.size

                                try{
                                    val updatedAvgLength= String.format("%.3f", avgLength)
                                    val avgLengthParagraph = addParagraph("D-Lange(m): ",updatedAvgLength.toString())
                                    document.add(avgLengthParagraph)
                                }catch(e: Exception){
                                    e.printStackTrace()
                                    val avgLengthParagraph = addParagraph("D-Lange(m): ",avgLength.toString())
                                    document.add(avgLengthParagraph)
                                }

                                try{
                                    val updatedAvgDiameter = String.format("%.3f", avgDiameter)
                                    val avgDiaParagraph = addParagraph("D-Durchmesser(cm): ",updatedAvgDiameter.toString())
                                    document.add(avgDiaParagraph)
                                }catch(e: Exception){
                                    e.printStackTrace()
                                    val avgDiaParagraph = addParagraph("D-Durchmesser(cm): ",avgDiameter.toString())
                                    document.add(avgDiaParagraph)
                                }

                                try{
                                    val updatedAvgVolume = String.format("%.3f", avgVolume)
                                    val avgVolParagraph = addParagraph("D-Kubatur(Fm/Stk): ",updatedAvgVolume.toString())
                                    document.add(avgVolParagraph)
                                }catch(e: Exception){
                                    e.printStackTrace()
                                    val avgVolParagraph = addParagraph("D-Kubatur(Fm/Stk): ",avgVolume.toString())
                                    document.add(avgVolParagraph)
                                }


                               // val avgDiameterParagraph = addParagraph("D-Durchmesser(cm): ",avgDiameter.toString())
                              //  val avgVolumeParagraph = addParagraph("D-Kubatur(Fm/Stk): ",avgVolume.toString())


                               // document.add(avgDiameterParagraph)
                               // document.add(avgVolumeParagraph)




                                // Overview Table Done
                                val result = hash.toList().sortedBy { (_, key) -> key}.toMap()

                               /* for ((key, value) in sortedMap) {
                                    totalVolumeTable.addCell(key)
                                    totalVolumeTable.addCell(value)
                                }*/



                                //    document.add(totalVolumeTable)



                           //    System.out.println("Log hash "+sortedMap.toString())



                            }

                        }


                    }

                }





                if(entity.logPhotoEntity != null && entity.logPhotoEntity?.imageUriList?.size!! > 0){

                    val photo = Paragraph(getString(R.string.tab_photos)+"("+ entity.logPhotoEntity?.imageUriList?.count()+")")
                    photo.font.setStyle(Font.BOLD)
                    photo.alignment = Paragraph.ALIGN_CENTER
                    document.add(photo)
                //    document.add(Paragraph(getString(R.string.tab_photos)+"("+ entity.logPhotoEntity?.imageUriList?.count()+")"))
                    for(i in 0..entity.logPhotoEntity?.imageUriList?.size!!-1){
                        document.newPage()
                        val image = Image.getInstance(entity.logPhotoEntity?.imageUriList?.get(i))
                        val indentation = 0
                        val scaler = (document.pageSize.width - document.leftMargin()
                                - document.rightMargin() - indentation) / image.getWidth() * 100

                        image.scalePercent(scaler)

                        // image.setAbsolutePosition(0f,0f)
                      /*  val ims = assets.open("nonwebp.jpg")
                        var bmp = BitmapFactory.decodeStream(ims)
                        bmp = getResizedBitmap(bmp,72,72)
                        val stream = ByteArrayOutputStream()
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)

                        var image = Image.getInstance(stream.toByteArray())
                        image.alignment = Image.ALIGN_RIGHT
                     //   image.scaleAbsoluteWidth(72f)
                       // image.scaleAbsoluteHeight(72f)

                        image.setAbsolutePosition(500f,750f)*/


                        document.add(image)

                    }
                    document.add(Paragraph(" "))
                  //  document.add(Paragraph("--------------------------------------------------------------------------"))
                }else{
                    val photo = Paragraph(getString(R.string.tab_photos)+"(0)")
                    photo.font.setStyle(Font.BOLD)
                    photo.alignment = Paragraph.ALIGN_CENTER
                    document.add(photo)
                    document.add(Paragraph(" "))
                  //  document.add(Paragraph("---------------------------------------------------------------------------"))
                }
                document.newPage()

             //   document.add(tableNr)

            }
            document.close()
            System.out.println("Done")
            //  viewPdf(file,"","")
            return true

        } catch (de: DocumentException) {
            Log.e("PDFCreator", "DocumentException:$de")
            return false
        } catch (e: IOException) {
            Log.e("PDFCreator", "ioException:$e")
            return false
        } finally {
            doc.close()
          //  viewPdf(currentFile!!,"","")
        }

    }


    fun addParagraph(text1:String,text2: String): Paragraph{


        var c1 = Chunk(text1)
        c1.font.setStyle(Font.BOLD)

        val c2 = Chunk(text2)

        val p1 = Paragraph()

        p1.add(Chunk(c1))
        p1.add(Chunk(c2))


        return p1

      //  document.add(p1)

    }

    // Method for opening a pdf file
    private fun viewCSV(files : File,file: String, directory: String) {

        val pdfFile = File(getExternalStorageDirectory() ,"/" + directory + "/" + file)
        val contentUri = FileProvider.getUriForFile(this, "com.mobipolter.provider",files)
        //val path = contentUri.path

        // Setting the intent for pdf reader
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(contentUri, "application/csv")
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pdfIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)


        try {
            startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No app found for opening csv file", Toast.LENGTH_SHORT).show()
        }

    }


    // Method for opening a pdf file
    private fun viewExcel(files : File,file: String, directory: String) {

        val pdfFile = File(getExternalStorageDirectory() ,"/" + directory + "/" + file)
        val contentUri = FileProvider.getUriForFile(this, "com.mobipolter.provider",files)
        //val path = contentUri.path

        // Setting the intent for pdf reader
        var myMime = MimeTypeMap.getSingleton()
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(contentUri, "application/vnd.ms-excel")
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pdfIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)


        try {
            startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {

            Toast.makeText(this, "No app found for opening excel file", Toast.LENGTH_SHORT).show()
        }

    }


    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false)
        bm.recycle()
        return resizedBitmap
    }

    fun sortHashMapByValues(passedMap:HashMap<String, String>):LinkedHashMap<String, String> {
        val mapKeys = ArrayList(passedMap.keys)
        val mapValues = ArrayList(passedMap.values)
        Collections.sort(mapValues)
        Collections.sort(mapKeys)
        val sortedMap = LinkedHashMap<String,String>()
        val valueIt = mapValues.iterator()
        while (valueIt.hasNext())
        {
            val `val` = valueIt.next()
            val keyIt = mapKeys.iterator()
            while (keyIt.hasNext())
            {
                val key = keyIt.next()
                val comp1 = passedMap.get(key)
                val comp2 = `val`
                if (comp1 == comp2)
                {
                    keyIt.remove()
                    sortedMap.put(key, `val`)
                    break
                }
            }
        }
        return sortedMap
    }


    fun createExcelFile(entityList: List<LogStackEntity>) : Boolean{

        var filename = "qwertty"

        var title = ""
        if(entityList.size == 1){
            title = entityList.get(0).logBasicEntity?.stackNR!!
        }else{
            title = "LogStack"
        }
        val file = create_Excel_File(title)
        currentFile = file
        currentFilePath = file.absolutePath
        val fOut = FileOutputStream(file)
        val wbSettings = WorkbookSettings()


        wbSettings.setLocale(Locale("en", "EN"))

        val cellFont = WritableFont(WritableFont.COURIER, 10)
        cellFont.setBoldStyle(WritableFont.BOLD)

        val cellFormatBold = WritableCellFormat(cellFont)

        val font = WritableFont(WritableFont.createFont("Hi"), 10, WritableFont.BOLD, false,
                UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK)
        val boldWithBorder = WritableCellFormat(font)
        boldWithBorder.setBorder(Border.ALL, BorderLineStyle.MEDIUM);
        boldWithBorder.setAlignment(Alignment.LEFT);
        boldWithBorder.setVerticalAlignment(VerticalAlignment.JUSTIFY)
        //sheet.addCell(new Label(1, 2, "An other text", cellFormatBold));


    try {
        val a= 1
        val workbook = Workbook.createWorkbook(file, wbSettings)
        //workbook.createSheet("Report", 0);
        var i= 0

        try {

            for (entity in entityList) {


                var sheet = workbook.createSheet(entity.logBasicEntity?.stackNR, i)

                var rowIndex = 1
                // val file = File(mAppSharedPreferences.getString("imgLogo",null)).path
                if (i == 0) {

                    if (mAppSharedPreferences.getString("imgLogo", null) != null) {
                        val im = WritableImage(0.0, 0.0, 3.0, 9.0, File(Uri.parse(mAppSharedPreferences.getString("imgLogo", null)).path))
                        sheet.addImage(im)
                        rowIndex = 11
                    }


                    if (mAppSharedPreferences.getString("address", null) != null) {

                        val labelAddress = Label(3, 2, mAppSharedPreferences.getString("address", null))
                        sheet.addColumnPageBreak(2)
                        sheet.addCell(labelAddress)

                    }

                }

                i++
                var columnIndex = 0


                if (entity.logBasicEntity != null) {
                    val label = Label(0, rowIndex, getString(R.string.TYP), cellFormatBold)
                    val polter_info = Label(0, rowIndex + 1, getString(R.string.polter_info), cellFormatBold)

                    val labelNr = Label(1, rowIndex, getString(R.string.nr), cellFormatBold)
                    var nr = ""
                    if (entity.logBasicEntity?.stackNR != null) {
                        nr = entity.logBasicEntity?.stackNR!!
                    }
                    rowIndex = rowIndex + 1
                    val labelNrValue = Label(1, rowIndex, nr, cellFormatBold)

                    val cellView1: CellView = sheet.getColumnView(4)
                    cellView1.isAutosize = true
                    sheet.setColumnView(1, cellView1)
                    rowIndex++

                    val labellosId = Label(2, 1, getString(R.string.los_id), cellFormatBold)
                    var losId = " "
                    if (entity.logBasicEntity?.iosID != null) {
                        losId = entity.logBasicEntity?.iosID!!
                    }
                    val labelLosIdValue = Label(2, 2, losId, cellFormatBold)

                    val cellView2: CellView = sheet.getColumnView(4)
                    cellView2.isAutosize = true
                    sheet.setColumnView(2, cellView2)

                    rowIndex++
                    rowIndex++

                    val labelforeignNr = Label(3, 1, getString(R.string.foreign_nr))
                    var foreignNr = " "
                    if (entity.logBasicEntity?.foreignNR != null) {
                        foreignNr = entity.logBasicEntity?.foreignNR!!
                    }
                    val labelForeignNrValue = Label(3, 2, foreignNr)
                    val cellView3: CellView = sheet.getColumnView(4)
                    cellView3.isAutosize = true
                    sheet.setColumnView(3, cellView3)


                    rowIndex++
                    val labeldate = Label(4, 1, getString(R.string.date))
                    var date = " "
                    if (entity.logBasicEntity?.date != null) {
                        date = entity.logBasicEntity?.date!!
                    }
                    val labelDateValue = Label(4, 2, date)
                    labelDateValue.cellFormat

                    val cellView: CellView = sheet.getColumnView(4)
                    cellView.isAutosize = true
                    sheet.setColumnView(4, cellView)

                    rowIndex++
                    val labellocationE = Label(5, 1, getString(R.string.location) + " E")
                    var locationE = 0.0
                    if (entity?.logLocationEntity != null) {
                        locationE = entity.logLocationEntity?.longtitude!!
                    }
                    val labelLocationEValue = Label(5, 2, locationE.toString())
                    val cellView5: CellView = sheet.getColumnView(4)
                    cellView5.isAutosize = true
                    sheet.setColumnView(5, cellView2)
                    rowIndex++

                    val labellocationN = Label(6, 1, getString(R.string.location) + " N")
                    var locationN = 0.0
                    if (entity?.logLocationEntity != null) {
                        locationN = entity.logLocationEntity?.longtitude!!
                    }
                    val labelLocationNValue = Label(6, 2, locationN.toString())
                    val cellView6: CellView = sheet.getColumnView(4)
                    cellView6.isAutosize = true
                    sheet.setColumnView(6, cellView6)

                    rowIndex++
                    val labelcomment = Label(7, 1, getString(R.string.comment))
                    var comment = " "
                    if (entity?.logBasicEntity?.comment != null) {
                        comment = entity?.logBasicEntity?.comment!!
                    }
                    val labelNrComment = Label(7, 2, comment)

                    val cellView7: CellView = sheet.getColumnView(4)
                    cellView7.isAutosize = true
                    sheet.setColumnView(7, cellView7)

                    val cellView8: CellView = sheet.getColumnView(4)
                    cellView8.isAutosize = true
                    sheet.setColumnView(8, cellView8)

                    val cellView9: CellView = sheet.getColumnView(4)
                    cellView9.isAutosize = true
                    sheet.setColumnView(9, cellView9)

                    val cellView10: CellView = sheet.getColumnView(4)
                    cellView10.isAutosize = true
                    sheet.setColumnView(10, cellView10)

                    val cellView11: CellView = sheet.getColumnView(4)
                    cellView11.isAutosize = true
                    sheet.setColumnView(11, cellView11)

                    val cellView12: CellView = sheet.getColumnView(4)
                    cellView12.isAutosize = true
                    sheet.setColumnView(12, cellView12)

                    rowIndex++


                    sheet.addCell(label)
                    sheet.addCell(polter_info)
                    sheet.addCell(labelNr)
                    sheet.addCell(labellosId)
                    sheet.addCell(labelforeignNr)
                    sheet.addCell(labeldate)
                    sheet.addCell(labellocationE)
                    sheet.addCell(labellocationN)
                    sheet.addCell(labelcomment)
                    sheet.addCell(labelNrValue)
                    sheet.addCell(labelLosIdValue)
                    sheet.addCell(labelForeignNrValue)
                    sheet.addCell(labelDateValue)
                    sheet.addCell(labelLocationEValue)
                    sheet.addCell(labelLocationNValue)
                    sheet.addCell(labelNrComment)

                    // -------------------------------------------
                    //  FSC, PEFC and Price row
                  //  rowIndex = rowIndex + 1

                    val PfcCertified = Label(0, 4, getString(R.string.PFC),cellFormatBold)
                    sheet.addCell(PfcCertified)
                    val labelFsc = Label(1, 4, getString(R.string.fsc))
                    var fsc = getString(R.string.no)
                    if (entity.logBasicEntity?.fsc == 1) {
                        fsc = getString(R.string.yes)
                    }
                    val labelFscValue = Label(1, 5, fsc)
                    rowIndex++

                    val labelPefc = Label(2, 4, getString(R.string.pefc))
                    var pefc = getString(R.string.no)
                    if (entity.logBasicEntity?.pefc == 1) {
                        pefc = getString(R.string.yes)
                    }
                    val labelPefcValue = Label(2, 5, pefc)
                    rowIndex++
                    val labelPrice = Label(3, 4, getString(R.string.price))
                    var price = 0.00
                    if (entity.logBasicEntity?.price != null) {
                        price = entity.logBasicEntity?.price!!
                    }
                    val labelPriceValue = Label(3, 5, price.toString())

                    rowIndex = rowIndex + 1

                    sheet.addCell(labelFsc)
                    sheet.addCell(labelPefc)
                    sheet.addCell(labelPrice)
                    sheet.addCell(labelFscValue)
                    sheet.addCell(labelPefcValue)
                    sheet.addCell(labelPriceValue)

                    //--------------------------------------------
                    //  Location, District  row

                  //  rowIndex = rowIndex + 1


                    val Lagerort = Label(0, 7, getString(R.string.Lagerort),cellFormatBold)
                    sheet.addCell(Lagerort)

                    val labelLocation = Label(1, 7, getString(R.string.location))
                    var location = ""
                    if (entity.logBasicEntity?.location != null) {
                        location = entity.logBasicEntity?.location!!
                    }
                    val labelLocationValue = Label(1, 8, location)
                    rowIndex++
                    val labelDistrict = Label(2, 7, getString(R.string.district))
                    var district = " "
                    if (entity.logBasicEntity?.district != null) {
                        district = entity.logBasicEntity?.district!!
                    }
                    val labelDistrictValue = Label(2, 8, district)
                    rowIndex++
                    val labelFOwner = Label(3, 7, getString(R.string.forest_owner))
                    var forestOwner = " "
                    if (entity.logBasicEntity?.forestOwner != null) {
                        forestOwner = entity.logBasicEntity?.forestOwner!!
                    }
                    val labelForestOwnerValue = Label(3, 8, forestOwner)
                    rowIndex++
                    val labelForester = Label(4, 7, getString(R.string.forester))
                    var forester = " "
                    if (entity?.logBasicEntity?.forester != null) {
                        forester = entity?.logBasicEntity?.forester!!
                    }
                    val labelForesterValue = Label(4, 8, forester)
                    rowIndex++
                    val labelForestry = Label(5, 7, getString(R.string.forestry))
                    var forestry = " "
                    if (entity?.logBasicEntity?.forestry != null) {
                        forestry = entity?.logBasicEntity?.forestry!!
                    }
                    val labelForestryValue = Label(5, 8, forestry)
                    rowIndex++
                    val labelClearer = Label(6, 7, getString(R.string.clearer))
                    var clearer = " "
                    if (entity?.logBasicEntity?.clearer != null) {
                        clearer = entity?.logBasicEntity?.clearer!!
                    }
                    val labelClearerValue = Label(6, 8, clearer)
                    rowIndex++
                    val labelFelier = Label(7, 7, getString(R.string.felier))
                    var feller = " "
                    if (entity?.logBasicEntity?.feller != null) {
                        feller = entity?.logBasicEntity?.feller!!
                    }
                    val labelFelierValue = Label(7, 8, feller)
                    rowIndex++
                    val labelSkidder = Label(8, 7, getString(R.string.skidder))
                    var skidder = " "
                    if (entity?.logBasicEntity?.skidder != null) {
                        skidder = entity?.logBasicEntity?.skidder!!
                    }
                    val labelSkidderValue = Label(8, 8, skidder)
                  //  rowIndex = rowIndex + 1

                    sheet.addCell(labelLocation)
                    sheet.addCell(labelDistrict)
                    sheet.addCell(labelFOwner)
                    sheet.addCell(labelForester)
                    sheet.addCell(labelForestry)
                    sheet.addCell(labelClearer)
                    sheet.addCell(labelFelier)
                    sheet.addCell(labelSkidder)
                    sheet.addCell(labelLocationValue)
                    sheet.addCell(labelDistrictValue)
                    sheet.addCell(labelForestOwnerValue)
                    sheet.addCell(labelForesterValue)
                    sheet.addCell(labelForestryValue)
                    sheet.addCell(labelClearerValue)
                    sheet.addCell(labelFelierValue)
                    sheet.addCell(labelSkidderValue)

                    //----------------------------------------------

//                    rowIndex = rowIndex + 2

                    if (entity.logMeasurementEntity != null) {

                        var labelMeasure = Label(1, 10, getString(R.string.tab_measurement))
                        sheet.addCell(labelMeasure)
                        rowIndex = rowIndex + 1

                    }


                    if (entity.logMeasurementEntity?.surveyingType != null) {



                        when (entity.logMeasurementEntity?.surveyingType) {


                            getString(R.string.section_surveying) -> {

                                if (entity.logMeasurementEntity?.sectionEntity != null) {


                                    val labelsection = Label(1, 11, getString(R.string.section_surveying))
                                    sheet.addCell(labelsection)
                                    rowIndex = 11 + 1


                                    val labelLength = Label(1, rowIndex, getString(R.string.length_m).subSequence(0, getString(R.string.length_m).length - 2).toString())
                                    val labelWidth = Label(2, rowIndex, getString(R.string.width_m).subSequence(0, getString(R.string.width_m).length - 2).toString())
                                    val labelLogscount = Label(3, rowIndex, getString(R.string.logs_count).subSequence(0, getString(R.string.logs_count).length - 1).toString())
                                    val labelMinTopCm = Label(4, rowIndex, getString(R.string.min_top_cm).subSequence(0, getString(R.string.min_top_cm).length - 1).toString())
                                    val labelMaxBaseCm = Label(5, rowIndex, getString(R.string.max_base_cm).subSequence(0, getString(R.string.max_base_cm).length - 1).toString())
                                    val labelOversieM = Label(6, rowIndex, getString(R.string.oversize_m).subSequence(0, getString(R.string.oversize_m).length - 1).toString())

                                    sheet.addCell(labelLength)
                                    sheet.addCell(labelWidth)
                                    sheet.addCell(labelLogscount)
                                    sheet.addCell(labelMinTopCm)
                                    sheet.addCell(labelMaxBaseCm)
                                    sheet.addCell(labelOversieM)

                                    rowIndex = rowIndex + 1

                                    var lengthValue = " "
                                    if (entity.logMeasurementEntity?.sectionEntity?.length != null)
                                        lengthValue = entity.logMeasurementEntity?.sectionEntity!!.length.toString()

                                    var widthValue = " "
                                    if (entity.logMeasurementEntity?.sectionEntity?.width != null)
                                        widthValue = entity.logMeasurementEntity?.sectionEntity?.width.toString()

                                    var logsCount = " "
                                    if (entity.logMeasurementEntity?.logsCount != null)
                                        logsCount = entity.logMeasurementEntity?.logsCount.toString()


                                    var minTopCm = " "
                                    if (entity.logMeasurementEntity?.minTopCm != null)
                                        minTopCm = entity.logMeasurementEntity?.minTopCm.toString()

                                    var maxBaseCm = " "
                                    if (entity.logMeasurementEntity?.maxBaseCm != null)
                                        maxBaseCm = entity.logMeasurementEntity?.maxBaseCm.toString()

                                    var oversizeM = " "
                                    if (entity.logMeasurementEntity?.oversizeM != null)
                                        oversizeM = entity.logMeasurementEntity?.oversizeM.toString()

                                    sheet.addCell(Label(1, rowIndex, lengthValue))
                                    sheet.addCell(Label(2, rowIndex, widthValue))
                                    sheet.addCell(Label(3, rowIndex, logsCount))
                                    sheet.addCell(Label(4, rowIndex, minTopCm))
                                    sheet.addCell(Label(5, rowIndex, maxBaseCm))
                                    sheet.addCell(Label(6, rowIndex, oversizeM))


                                }

                                rowIndex = rowIndex + 2

                                if (entity.logMeasurementEntity?.sectionEntity?.height_list != null &&
                                        entity.logMeasurementEntity?.sectionEntity?.height_list!!.size > 0) {


                                    var labelNr = Label(1, rowIndex, getString(R.string.nr))
                                    var labelHeightM = Label(2, rowIndex, getString(R.string.height_m).subSequence(0, getString(R.string.height_m).length - 2).toString())
                                    var labelSectionLengthM = Label(3, rowIndex, getString(R.string.section_length_m).subSequence(0, getString(R.string.section_length_m).length - 2).toString())

                                    sheet.addCell(labelNr)
                                    sheet.addCell(labelHeightM)
                                    sheet.addCell(labelSectionLengthM)

                                    rowIndex = rowIndex + 1

                                    for (i in 0..entity.logMeasurementEntity?.sectionEntity?.height_list!!.size - 1) {
                                        val heightListEntity = entity.logMeasurementEntity?.sectionEntity?.height_list!!.get(i)
                                        var labelIndex = Label(1, rowIndex, heightListEntity.index.toString())
                                        var labelHeight = Label(2, rowIndex, heightListEntity.height.toString())
                                        var labelSection = Label(3, rowIndex, heightListEntity.section.toString())
                                        sheet.addCell(labelIndex)
                                        sheet.addCell(labelHeight)
                                        sheet.addCell(labelSection)
                                        rowIndex = rowIndex + 1
                                    }

                                    rowIndex = rowIndex + 1


                                    val labelSumOfSections = Label(1, rowIndex, getString(R.string.sum_of_sections))
                                    val labelHeightCount= Label(2, rowIndex, getString(R.string.height_count).subSequence(0, getString(R.string.height_count).length - 1).toString())
                                    val labelAvgHeightM = Label(3, rowIndex, getString(R.string.avg_height).subSequence(0, getString(R.string.avg_height).length - 1).toString())
                                    val labelVolumeST = Label(4, rowIndex, getString(R.string.volume).subSequence(0, getString(R.string.volume).length - 1).toString())
                                    val labelST = Label(5, rowIndex, getString(R.string.st).subSequence(0, getString(R.string.st).length - 1).toString())
                                    val labelDistr = Label(6, rowIndex, getString(R.string.distr).subSequence(0, getString(R.string.distr).length - 1).toString())
                                    val labelM3 = Label(7, rowIndex,"m3")
                                    val labelFactor = Label(8, rowIndex, getString(R.string.factor).subSequence(0, getString(R.string.factor).length - 1).toString())


                                    sheet.addCell(labelSumOfSections)
                                    sheet.addCell(labelHeightCount)
                                    sheet.addCell(labelAvgHeightM)
                                    sheet.addCell(labelVolumeST)
                                    sheet.addCell(labelST)
                                    sheet.addCell(labelDistr)
                                    sheet.addCell(labelM3)
                                    sheet.addCell(labelFactor)

                                    rowIndex = rowIndex + 1

                                    var sumValue = "0"
                                    if(entity.logMeasurementEntity?.sectionEntity!!.sum_of_sections.toString() != null){
                                        sumValue = entity.logMeasurementEntity?.sectionEntity!!.sum_of_sections.toString()
                                    }

                                    var heightCountValue = "0"
                                    if(entity.logMeasurementEntity?.sectionEntity!!.height_count.toString() != null){
                                        heightCountValue = entity.logMeasurementEntity?.sectionEntity!!.height_count.toString()
                                    }

                                    var avgHeightValue = "0.0"
                                    if(entity.logMeasurementEntity?.sectionEntity!!.avg_height.toString() != null){
                                        avgHeightValue = entity.logMeasurementEntity?.sectionEntity!!.avg_height.toString()
                                    }

                                    var volumeBarkValue = "0.0"
                                    if(entity.logMeasurementEntity?.sectionEntity!!.volume_bark.toString() != null){
                                        volumeBarkValue = entity.logMeasurementEntity?.sectionEntity!!.volume_bark.toString()
                                    }

                                    var stValue = "0.0"
                                    if(entity.logMeasurementEntity?.sectionEntity!!.st.toString() != null){
                                        stValue = entity.logMeasurementEntity?.sectionEntity!!.st.toString()
                                    }


                                    var distrValue = "0.0"
                                    if(entity.logMeasurementEntity?.sectionEntity!!.distr.toString() != null){
                                        distrValue = entity.logMeasurementEntity?.sectionEntity!!.distr.toString()
                                    }


                                    var m3Value = "0.0"
                                    if(entity.logMeasurementEntity?.sectionEntity!!.m3.toString() != null){
                                        m3Value = entity.logMeasurementEntity?.sectionEntity!!.m3.toString()
                                    }


                                    var factorValue = "0.0"
                                    if(entity.logMeasurementEntity?.sectionEntity!!.factor.toString() != null){
                                        factorValue = entity.logMeasurementEntity?.sectionEntity!!.factor.toString()
                                    }


                                    val labelSumOfSectionsValue = Label(1, rowIndex,sumValue)
                                    val labelHeightCountValue = Label(2, rowIndex,heightCountValue)
                                    val labelAvgHeightMValue  = Label(3, rowIndex, avgHeightValue)
                                    val labelVolumeSTValue  = Label(4, rowIndex, volumeBarkValue)
                                    val labelSTValue  = Label(5, rowIndex, stValue)
                                    val labelDistrValue  = Label(6, rowIndex, distrValue)
                                    val labelM3Value  = Label(7, rowIndex,m3Value)
                                    val labelFactorValue  = Label(8, rowIndex,factorValue)


                                    sheet.addCell(labelSumOfSectionsValue )
                                    sheet.addCell(labelHeightCountValue)
                                    sheet.addCell(labelAvgHeightMValue)
                                    sheet.addCell(labelVolumeSTValue)
                                    sheet.addCell(labelSTValue)
                                    sheet.addCell(labelDistrValue)
                                    sheet.addCell(labelM3Value)
                                    sheet.addCell(labelFactorValue)
                                    rowIndex++








                                }
                            }

                            getString(R.string.estimation_surveying) -> {

                                if (entity.logMeasurementEntity?.estimationEntity != null) {
                                    val labelEstimate = Label(1, 12, getString(R.string.estimation_surveying))
                                    sheet.addCell(labelEstimate)
                                    rowIndex = 12 + 2

                                    val labelSpecie = Label(1, rowIndex, getString(R.string.species))
                                    val labelkind = Label(2, rowIndex, getString(R.string.kind))
                                    val labelQuality = Label(3, rowIndex, getString(R.string.quality))
                                    val labelSize = Label(4, rowIndex, getString(R.string.size))
                                    val labelLengthM = Label(5, rowIndex, getString(R.string.length_m))
                                    val labelWidthM = Label(6, rowIndex, getString(R.string.width_m).subSequence(0, getString(R.string.length_m).length - 2).toString())
                                    val labelUnit = Label(7, rowIndex, getString(R.string.unit).subSequence(0, getString(R.string.unit).length - 1).toString())
                                    val labelVolume = Label(8, rowIndex, getString(R.string.volume_value).subSequence(0, getString(R.string.volume_value).length - 1).toString())

                                    sheet.addCell(labelSpecie)
                                    sheet.addCell(labelkind)
                                    sheet.addCell(labelQuality)
                                    sheet.addCell(labelSize)
                                    sheet.addCell(labelLengthM)
                                    sheet.addCell(labelWidthM)
                                    sheet.addCell(labelUnit)
                                    sheet.addCell(labelVolume)

                                    rowIndex = rowIndex + 1

                                    var specieValue = " "
                                    if (entity.logMeasurementEntity?.estimationEntity?.species != null)
                                        specieValue = entity.logMeasurementEntity?.estimationEntity!!.species.toString()

                                    var kindValue = " "
                                    if (entity.logMeasurementEntity?.estimationEntity?.kind != null)
                                        kindValue = entity.logMeasurementEntity?.estimationEntity!!.kind.toString()

                                    var qualityValue = " "
                                    if (entity.logMeasurementEntity?.estimationEntity?.quality != null)
                                        qualityValue = entity.logMeasurementEntity?.estimationEntity!!.quality.toString()

                                    var sizeValue = " "
                                    if (entity.logMeasurementEntity?.estimationEntity?.size != null)
                                        sizeValue = entity.logMeasurementEntity?.estimationEntity!!.size.toString()

                                    var lengthValue = " "
                                    if (entity.logMeasurementEntity?.estimationEntity?.length != null)
                                        lengthValue = entity.logMeasurementEntity?.estimationEntity!!.length.toString()

                                    var widthValue = " "
                                    if (entity.logMeasurementEntity?.estimationEntity?.width != null)
                                        widthValue = entity.logMeasurementEntity?.estimationEntity!!.width.toString()

                                    var unitValue = " "
                                    if (entity.logMeasurementEntity?.estimationEntity?.unit != null)
                                        unitValue = entity.logMeasurementEntity?.estimationEntity!!.unit.toString()

                                    var volumeValue = " "
                                    if (entity.logMeasurementEntity?.estimationEntity?.volume != null)
                                        volumeValue = entity.logMeasurementEntity?.estimationEntity!!.volume.toString()

                                    sheet.addCell(Label(1, rowIndex, specieValue))
                                    sheet.addCell(Label(2, rowIndex, kindValue))
                                    sheet.addCell(Label(3, rowIndex, qualityValue))
                                    sheet.addCell(Label(4, rowIndex, sizeValue))
                                    sheet.addCell(Label(5, rowIndex, lengthValue))
                                    sheet.addCell(Label(6, rowIndex, widthValue))
                                    sheet.addCell(Label(7, rowIndex, unitValue))
                                    sheet.addCell(Label(8, rowIndex, volumeValue))

                                }


                            }


                            getString(R.string.logs_surveying) -> {


                                if (entity.logMeasurementEntity?.logsEntity != null) {
                                    var log = Label(1, 11, getString(R.string.logs_surveying))
                                    sheet.addCell(log)
                                    rowIndex = rowIndex + 1

                                    sheet.addRowPageBreak(2)
                                }


                                if (entity.logMeasurementEntity?.logsEntity?.add_log_entity_list != null &&
                                        entity.logMeasurementEntity?.logsEntity?.add_log_entity_list!!.size > 0) {


                                    var labelLogCount = Label(1, 12, getString(R.string.logs_count))
//                                    var labelTotalVolume = Label(1, rowIndex, getString(R.string.total_volume),boldWithBorder)

                                    sheet.addCell(labelLogCount)
//                                    sheet.addCell(labelTotalVolume)

                                    rowIndex = rowIndex + 1

                                    var logCount = entity.logMeasurementEntity?.logsEntity?.log_count
                                    sheet.addCell(Label(1, 13, logCount.toString()))

                                    var labelMinTop = Label(2,12,getString(R.string.min_top_cm))
                                    sheet.addCell(labelMinTop)

                                    var minTopCm = entity.logMeasurementEntity?.minTopCm
                                    sheet.addCell(Label(2,13,minTopCm.toString()))



                                    var labelMaxTop = Label(3,12,getString(R.string.max_base_cm))
                                    sheet.addCell(labelMaxTop)

                                    var maxBaseCm = entity.logMeasurementEntity?.maxBaseCm
                                    sheet.addCell(Label(3,13,maxBaseCm.toString()))


                                    var overSize = Label(3,12,getString(R.string.oversize))
                                    sheet.addCell(overSize)

                                    var overSizeValue = entity.logMeasurementEntity?.oversizeM
                                    sheet.addCell(Label(3,13,overSizeValue.toString()))








                                    var totalVolume = entity.logMeasurementEntity?.logsEntity?.total_volume_m3
//                                    sheet.addCell(Label(1, rowIndex, totalVolume.toString()))


                                    rowIndex = 13 + 2
                                    columnIndex = 1

                                    var logNr = Label(columnIndex, rowIndex, getString(R.string.nr),boldWithBorder)
                                    var logPlate = Label(columnIndex+1, rowIndex, getString(R.string.plate),boldWithBorder)
                                    var logSpecie = Label(columnIndex+2, rowIndex, getString(R.string.species),boldWithBorder)
                                    var logKind = Label(columnIndex+3, rowIndex, getString(R.string.kind),boldWithBorder)
                                    var logQuality = Label(columnIndex+4, rowIndex, getString(R.string.quality),boldWithBorder)
                                    var logLengthM = Label(columnIndex+5, rowIndex, getString(R.string.length_m).subSequence(0, getString(R.string.length_m).length - 1).toString(),boldWithBorder)
                                    var logDiaM = Label(columnIndex+6, rowIndex, getString(R.string.add_log_diameter_cm).subSequence(0, getString(R.string.add_log_diameter_cm).length - 1).toString(),boldWithBorder)
                                    var logLogVolume = Label(columnIndex+7, rowIndex, getString(R.string.add_log_volume),boldWithBorder)
                                    var logOversize = Label(columnIndex+8, rowIndex, getString(R.string.oversize),boldWithBorder)
                                    var logKlasse = Label(columnIndex+9, rowIndex, getString(R.string.klasse),boldWithBorder)
                                    var logBarmCm = Label(columnIndex+10, rowIndex, "RIN",boldWithBorder)
                                    var logBarmCmUnit = Label(columnIndex+11, rowIndex, "DE",boldWithBorder)

                                    sheet.addCell(logNr)
                                    sheet.addCell(logPlate)
                                    sheet.addCell(logSpecie)
                                    sheet.addCell(logKind)
                                    sheet.addCell(logQuality)
                                    sheet.addCell(logLengthM)
                                    sheet.addCell(logDiaM)
                                    sheet.addCell(logLogVolume)
                                    sheet.addCell(logOversize)
                                    sheet.addCell(logKlasse)
                                    sheet.addCell(logBarmCm)
                                    sheet.addCell(logBarmCmUnit)

                                    rowIndex = rowIndex + 1

                                    val addLogList = ArrayList<AddLogEntity>()
                                    addLogList.addAll(entity.logMeasurementEntity?.logsEntity?.add_log_entity_list!!)


                                    Collections.sort(addLogList, object : Comparator<AddLogEntity> {
                                        override fun compare(lhs: AddLogEntity, rhs: AddLogEntity): Int {

                                            // return Integer.valueOf(lhs.kind).compareTo(Integer.valueOf(rhs.kind))
                                            val value1 = Integer.valueOf(lhs.kind).compareTo(Integer.valueOf(rhs.kind))
                                            if (value1 == 0) {
                                                val value2 = Integer.valueOf(lhs.plate).compareTo(Integer.valueOf(rhs.plate))
                                                return value2
                                            }
                                            return value1


                                        }
                                    })


                                    for (i in 0..addLogList!!.size - 1) {
                                        val addLogEntity = addLogList!!.get(i)

                                        val LogInfo = Label(0, rowIndex, getString(R.string.LogInfo),cellFormatBold)
                                        sheet.addCell(LogInfo)

                                        var logNrValue = Label(1, rowIndex, addLogEntity.log_nr.toString())
                                        var logPlateValue = Label(2, rowIndex, addLogEntity.plate.toString())
                                        var logSpecieValue = Label(3, rowIndex, addLogEntity.species.toString())
                                        var logKindValue = Label(4, rowIndex, addLogEntity.kind.toString())
                                        var logQualityValue = Label(5, rowIndex, addLogEntity.quality.toString())
                                        var logLengthValue = Label(6, rowIndex, addLogEntity.length_m.toString())
                                        var logDiaMValue = Label(7, rowIndex, addLogEntity.diameter_cm.toString())
                                        var logVolumeValue = Label(8, rowIndex, addLogEntity.volume_m3.toString())
                                        var logOversizeValue = Label(9, rowIndex, addLogEntity.oversize_m.toString())
                                        var logKlasseValue = Label(10, rowIndex, addLogEntity.klasse.toString())
                                        var logBarkCmValue = Label(11, rowIndex, addLogEntity.bark_cm.toString())
                                        var logBarkCmUnit = Label(12, rowIndex,"cm")

                                        sheet.addCell(logNrValue)
                                        sheet.addCell(logPlateValue)
                                        sheet.addCell(logSpecieValue)
                                        sheet.addCell(logKindValue)
                                        sheet.addCell(logQualityValue)
                                        sheet.addCell(logLengthValue)
                                        sheet.addCell(logDiaMValue)
                                        sheet.addCell(logVolumeValue)
                                        sheet.addCell(logOversizeValue)
                                        sheet.addCell(logKlasseValue)
                                        sheet.addCell(logBarkCmValue)
                                        sheet.addCell(logBarkCmUnit)

                                        rowIndex = rowIndex + 1
                                    }

                                    rowIndex = rowIndex + 1

                                    // val addLogList = entity.logMeasurementEntity?.logsEntity?.add_log_entity_list!!
                                    //    val totalVolumeList = ArrayList<TotalVolumeEntity>()
                                    val hash = HashMap<Int, String>()

                                    for (i in 0..addLogList.size - 1) {

                                        if (!hash.containsKey(Integer.parseInt(addLogList.get(i).kind!!))) {
                                            hash.put((Integer.parseInt(addLogList.get(i).kind!!)), addLogList.get(i).volume_m3.toString())
                                        } else {
                                            var volume = hash.get(Integer.parseInt(addLogList.get(i).kind))!!.toDouble()
                                            volume = volume + addLogList.get(i).volume_m3
                                            hash.put(Integer.parseInt(addLogList.get(i).kind!!), volume.toString())

                                        }
                                    }

                                    var kindLabel = Label(1, rowIndex, getString(R.string.kind),boldWithBorder)
                                    var totalVolumeLabel = Label(2, rowIndex, getString(R.string.total_volume),boldWithBorder)

                                    sheet.addCell(kindLabel)
                                    sheet.addCell(totalVolumeLabel)

//                                    rowIndex = rowIndex + 1

                                    //  val sortedMap = hash.toSortedMap(compareBy { it })
                                    //  val keys = TreeSet<String>(hash.keys)
                                    val entrySet = hash.entries

                                    //Creating an ArrayList of Entry objects

                                    //   val keyList = ArrayList(hash.keys)
                                    //   val valueList = ArrayList(hash.values)
                                    //    val sortedMap = sortHashMapByValues(hash)

                                    val set = hash.entries
                                    val iterator = set.iterator()
                                    while (iterator.hasNext()) {
                                        val me = iterator.next() as Map.Entry<Int, String>
                                        //  System.out.println("Logg "+me.key+": "+me.value)
                                        //System.out.println(me.getValue())
                                    }
                                    val map = TreeMap<Int, String>(hash)
                                    println("After Sorting:")
                                    val set2 = map.entries
                                    val iterator2 = set2.iterator()
                                    while (iterator2.hasNext()) {
                                        val me2 = iterator2.next() as Map.Entry<Int, String>
                                        System.out.println("<Logg" + me2.key + ": " + me2.value)
                                        var kindvalue = Label(1, rowIndex+1, me2.key.toString())
                                        var totalVolume = Label(2, rowIndex+1, me2.value.toString())

                                        sheet.addCell(kindvalue)
                                        sheet.addCell(totalVolume)

                                        rowIndex = rowIndex + 1


                                    }

                                    rowIndex = rowIndex + 2



                                    var overviewList = ArrayList<AddLogEntity>()
                                    var specieList = HashMap<String, String>()

                                    columnIndex = 1

                                    var overviewSpecieLabel = Label(columnIndex, rowIndex, getString(R.string.species),boldWithBorder)
                                    var overviewQualityLabel = Label(columnIndex+1, rowIndex, getString(R.string.quality),boldWithBorder)
                                    var overviewKlasseLabel = Label(columnIndex+2, rowIndex, getString(R.string.klasse),boldWithBorder)
                                    var overviewStuckLabel = Label(columnIndex+3, rowIndex, "Stuck",boldWithBorder)
                                    var overviewKubaturLabel = Label(columnIndex+4, rowIndex, getString(R.string.add_log_volume),boldWithBorder)

                                   rowIndex = rowIndex + 1

                                    sheet.addCell(overviewSpecieLabel)
                                    sheet.addCell(overviewQualityLabel)
                                    sheet.addCell(overviewKlasseLabel)
                                    sheet.addCell(overviewStuckLabel)
                                    sheet.addCell(overviewKubaturLabel)

                                    for (i in 0..addLogList.size - 1) {

                                        val key = "" + addLogList[i].species + " " + addLogList[i].quality +
                                                " " + addLogList[i].klasse
                                        if (specieList.containsKey(key)) {
                                            val newVolume = specieList.get(key).toString() + " " + addLogList[i].volume_m3.toString()
                                            specieList.put(key, "" + newVolume)
                                        } else {
                                            specieList.put(key, addLogList[i].volume_m3.toString())
                                        }
                                    }

                                    System.out.println("Hash" + specieList.size)

                                    val entrySets = specieList.entries

                                    //Creating an ArrayList of Entry objects

                                    //   val keyList = ArrayList(hash.keys)
                                    //   val valueList = ArrayList(hash.values)
                                    //    val sortedMap = sortHashMapByValues(hash)

                                    val sets = specieList.entries
                                    val iterators = sets.iterator()

                                    val overviewLogList = ArrayList<OverviewLogEntity>()

                                    var totalSumStuck = 0
                                    var totalSumVolume = 0.0

                                    while (iterators.hasNext()) {
                                        val me = iterators.next() as Map.Entry<String, String>
                                     //   rowIndex = rowIndex + 1
                                        //  System.out.println("Hash "+me.key.toString()+" : "+me.value)
                                        if (me.value != null) {

                                            val splited = me.value.toString().split(" ")
                                            if (splited.size > 1) {

                                                val count = splited.size
                                                var volume = 0.0
                                                for (i in 0..splited.size - 1) {
                                                    volume = volume + splited[i].toDouble()
                                                }

                                                val spiltKey = me.key.split(" ")

                                                for (i in 0..spiltKey.size - 1) {

                                                  //  sheet.addCell(Label(i, rowIndex, spiltKey.get(i)))

                                                }
                                              //  sheet.addCell(Label(spiltKey.size, rowIndex, count.toString()))

                                                try{
                                                    val updatedVolume = String.format("%.3f", volume)
                                                 //   sheet.addCell(Label(spiltKey.size + 1, rowIndex, updatedVolume.toString()))

                                                }catch(e : Exception){
                                                    e.printStackTrace()
                                                //    sheet.addCell(Label(spiltKey.size + 1, rowIndex, volume.toString()))
                                                }


                                                //sheet.addCell(Label(spiltKey.size + 1, rowIndex, volume.toString()))

                                                overviewLogList.add(OverviewLogEntity(spiltKey.get(0),
                                                        spiltKey.get(1), spiltKey.get(2), count.toString(),
                                                        volume.toString()))



                                                System.out.println("Hash " + me.key.toString() + " : " + me.value.toString() + " " + count + " " + volume)
                                            } else {

                                                val spiltKey = me.key.split(" ")
                                                for (i in 0..spiltKey.size - 1) {

                                                 //   sheet.addCell(Label(i, rowIndex, spiltKey.get(i)))

                                                }
                                             //   sheet.addCell(Label(spiltKey.size, rowIndex, "1"))
                                             //   sheet.addCell(Label(spiltKey.size + 1, rowIndex, me.value.toString()))
                                                System.out.println("Hash " + me.key.toString() + " : " + me.value)
                                                overviewLogList.add(OverviewLogEntity(spiltKey.get(0),
                                                        spiltKey.get(1), spiltKey.get(2), "1",
                                                        me.value.toString()))

                                            }





                                        }

                                    }



                                    val groupOverviewMap = HashMap<String, ArrayList<OverviewLogEntity>>()
                                    var sortedList = ArrayList<OverviewLogEntity>()

                                    for (overview in overviewLogList) {
                                        val key = overview.specie
                                        if (groupOverviewMap.containsKey(key)) {
                                            sortedList = groupOverviewMap.get(key)!!
                                            sortedList.add(overview)

                                        } else {
                                            sortedList = ArrayList<OverviewLogEntity>()
                                            sortedList.add(overview)
                                            groupOverviewMap.put(key, sortedList)

                                        }

                                    }


                                    val overviewset = groupOverviewMap.entries
                                    val overiterator = overviewset.iterator()
                                    while (overiterator.hasNext()) {
                                        val overallTotal = overiterator.next() as Map.Entry<String,ArrayList<OverviewLogEntity>>
                                        System.out.println("SortedHashmap "+overallTotal.key+": "+overallTotal.value)

                                        var totalStuck = 0
                                        var totalVolume = 0.0



                                        val qualityMap = HashMap<String, ArrayList<OverviewLogEntity>>()
                                        var sortedQualityList = ArrayList<OverviewLogEntity>()

                                        for (overview in overallTotal.value) {
                                            val key = overview.quality
                                            if (qualityMap.containsKey(key)) {
                                                sortedQualityList = qualityMap.get(key)!!
                                                sortedQualityList.add(overview)

                                            } else {
                                                sortedQualityList = ArrayList<OverviewLogEntity>()
                                                sortedQualityList.add(overview)
                                                qualityMap.put(key, sortedQualityList)

                                            }

                                        }

                                        rowIndex = rowIndex + 1

                                        val overviewQualityset = qualityMap.entries
                                        val overQualityiterator = overviewQualityset.iterator()

                                        while(overQualityiterator.hasNext()){

                                            val me = overQualityiterator.next() as Map.Entry<String,ArrayList<OverviewLogEntity>>
                                            System.out.println("SortedQualityHashmap "+me.key+": "+me.value)

                                            var totalStuck = 0
                                            var totalVolume = 0.0

                                            for(i in 0..me.value.size-1){

                                                columnIndex = 1

                                                val overviewEntity = me.value.get(i)
                                                sheet.addCell(Label(1,rowIndex,overviewEntity.specie))
                                               sheet.addCell(Label(2,rowIndex,overviewEntity.quality))
                                               sheet.addCell(Label(3,rowIndex,overviewEntity.klasse))
                                               sheet.addCell(Label(4,rowIndex,overviewEntity.stuck))
                                                sheet.addCell(Label(5,rowIndex,overviewEntity.volume))

                                                rowIndex = rowIndex + 1

                                                totalStuck = totalStuck + overviewEntity.stuck.toInt()
                                                totalVolume = totalVolume + overviewEntity.volume.toDouble()

                                                System.out.println("Total "+totalStuck+ " "+totalVolume)

                                                if(i == me.value.size-1){
                                                    sheet.addCell(Label(1,rowIndex,me.value.get(0).specie,cellFormatBold))
                                                    sheet.addCell(Label(2,rowIndex,me.value.get(0).quality,cellFormatBold))
                                                    sheet.addCell(Label(3,rowIndex," ",cellFormatBold))
                                                    sheet.addCell(Label(4,rowIndex,totalStuck.toString(),cellFormatBold))

                                                    try{
                                                        val updatedVolume = String.format("%.3f", totalVolume)
                                                        sheet.addCell(Label(5,rowIndex,updatedVolume.toString(),cellFormatBold))

                                                    }catch(e : Exception){
                                                        e.printStackTrace()
                                                        sheet.addCell(Label(5,rowIndex,totalVolume.toString(),cellFormatBold))
                                                    }

                                                  //  sheet.addCell(Label(4,rowIndex,totalVolume.toString(),cellFormatBold))

                                                    rowIndex = rowIndex + 1
                                                }

                                            }

                                        }



                                        for(i in 0..overallTotal.value.size-1){

                                            val overviewEntity = overallTotal.value.get(i)
                                            /*   sheet.addCell(Label(0,rowIndex,overviewEntity.specie))
                                               sheet.addCell(Label(1,rowIndex,overviewEntity.quality))
                                               sheet.addCell(Label(2,rowIndex,overviewEntity.klasse))
                                               sheet.addCell(Label(3,rowIndex,overviewEntity.stuck))
                                               sheet.addCell(Label(4,rowIndex,overviewEntity.volume))*/

                                            totalStuck = totalStuck + overviewEntity.stuck.toInt()
                                            totalVolume = totalVolume + overviewEntity.volume.toDouble()

                                            System.out.println("Totals "+totalStuck+ " "+totalVolume)

                                            if(i == overallTotal.value.size-1){
                                                sheet.addCell(Label(1,rowIndex,overallTotal.value.get(0).specie,boldWithBorder))
                                                sheet.addCell(Label(2,rowIndex," ",boldWithBorder))
                                                sheet.addCell(Label(3,rowIndex," ",boldWithBorder))
                                                sheet.addCell(Label(4,rowIndex,totalStuck.toString(),boldWithBorder))

                                                try{
                                                    val updatedVolume = String.format("%.3f", totalVolume)
                                                    sheet.addCell(Label(5,rowIndex,updatedVolume.toString(),boldWithBorder))

                                                }catch(e : Exception){
                                                    e.printStackTrace()
                                                    sheet.addCell(Label(5,rowIndex,totalVolume.toString(),boldWithBorder))
                                                }
                                             //   sheet.addCell(Label(4,rowIndex,totalVolume.toString(),cellFormatBold))

                                                totalSumStuck = totalSumStuck + totalStuck
                                                totalSumVolume = totalSumVolume + totalVolume

                                                rowIndex = rowIndex + 1
                                            }

                                        }

                                        //System.out.println(me.getValue())
                                    }


                                    rowIndex = rowIndex + 1



                                    sheet.addCell(Label(1,rowIndex,"Total ",boldWithBorder))
                                    sheet.addCell(Label(2,rowIndex," ",boldWithBorder))
                                    sheet.addCell(Label(3,rowIndex," ",boldWithBorder))
                                    sheet.addCell(Label(4,rowIndex,totalSumStuck.toString(),boldWithBorder))
                                    try{
                                        val updatedVolume = String.format("%.3f", totalSumVolume)
                                        sheet.addCell(Label(5,rowIndex,updatedVolume.toString(),boldWithBorder))

                                    }catch(e : Exception){
                                        e.printStackTrace()
                                        sheet.addCell(Label(5,rowIndex,totalSumVolume.toString(),boldWithBorder))
                                    }

                                    rowIndex = rowIndex + 2


                                    // Average of Length, Dimeter and Volume

                                    var totalLength = 0.0
                                    var totalVolumeList = 0.0
                                    var totalDiameter = 0.0
                                    for(i in 0..addLogList.size-1){
                                        totalLength = totalLength + addLogList[i].length_m
                                        totalVolumeList = totalVolumeList + addLogList[i].volume_m3
                                        totalDiameter = totalDiameter + addLogList[i].diameter_cm
                                    }
                                    var avgLength = totalLength/addLogList.size
                                    var avgVolume = totalVolumeList/addLogList.size
                                    var avgDiameter = totalDiameter/addLogList.size

                                    try{

                                        val avgLengthLabelTitle = Label(1,rowIndex,"D-Lange(m): ",cellFormatBold)
                                        sheet.addCell(avgLengthLabelTitle)
                                        val updatedAvgLength = String.format("%.3f", avgLength)
                                        val avgLengthLabelValue  = Label(3,rowIndex,updatedAvgLength.toString())
                                        rowIndex++
                                        sheet.addCell(avgLengthLabelValue)
                                    }catch(e: Exception){
                                        e.printStackTrace()
                                        val avgLengthLabelValue  = Label(3,rowIndex,avgLength.toString())
                                        rowIndex++
                                        sheet.addCell(avgLengthLabelValue)
                                    }

                                    try{

                                        val avgDiaParagraph = Label(1,rowIndex,"D-Durchmesser(cm): ",cellFormatBold)
                                        sheet.addCell(avgDiaParagraph)
                                        val updatedAvgDiameter = String.format("%.3f", avgDiameter)
                                        val avgDiaValue = Label(3,rowIndex,updatedAvgDiameter.toString())
                                        rowIndex++
                                        sheet.addCell(avgDiaValue)


                                    }catch(e: Exception){
                                        e.printStackTrace()
                                        val avgDiaValue = Label(3,rowIndex,avgDiameter.toString())
                                        rowIndex++
                                        sheet.addCell(avgDiaValue)
                                    }

                                    try{
                                        val avgVolParagraph = Label(1,rowIndex,"D-Kubatur(Fm/Stk): ",cellFormatBold)
                                        sheet.addCell(avgVolParagraph)
                                        val updatedAvgVolume = String.format("%.3f", avgVolume)
                                        val avgVolValue = Label(3,rowIndex,updatedAvgVolume.toString())
                                        rowIndex++
                                        sheet.addCell(avgVolValue)
                                    }catch(e: Exception){
                                        e.printStackTrace()
                                        val avgVolParagraph = Label(3,rowIndex,avgVolume.toString())
                                        rowIndex++
                                        sheet.addCell(avgVolParagraph)
                                    }



                                }


                            }

                        }


                    }


                }

            }


        } catch ( e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }


        try {
            workbook.write()

            workbook.close()
            return true
        } catch (e : WriteException ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false
        }
        //createExcel(excelSheet);
    } catch (e : IOException ) {
        // TODO Auto-generated catch block
        e.printStackTrace()
        return false
    }

    }


    fun sortEntitiesByDescendingDate(){


        Collections.sort(logStackEntityListDb, object : Comparator<LogStackEntity> {
            override fun compare(o1: LogStackEntity, o2: LogStackEntity): Int {

                val dateO1 = SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.getDefault()).parse(o1.logBasicEntity?.date)
                val firstDate = Date(dateO1.time)
                val dateO2 = SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.getDefault()).parse(o2.logBasicEntity?.date)
                val secondDate = Date(dateO2.time)
                if (firstDate.before(secondDate)) {
                            return -1
                        } else if (firstDate.after(secondDate)) {
                            return 1
                        } else {
                    return 0
                }
            }
        })

        if(adapter != null)
            adapter?.notifyDataSetChanged()
    }

    fun sortEntitiesByAscendingDate(){


        Collections.sort(logStackEntityListDb, object : Comparator<LogStackEntity> {
            override fun compare(o1: LogStackEntity, o2: LogStackEntity): Int {

                val dateO1 = SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.getDefault()).parse(o1.logBasicEntity?.date)
                val firstDate = Date(dateO1.time)
                val dateO2 = SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.getDefault()).parse(o2.logBasicEntity?.date)
                val secondDate = Date(dateO2.time)
                if (secondDate.before(firstDate)) {
                    return -1
                } else if (secondDate.after(firstDate)) {
                    return 1
                } else {
                    return 0
                }
            }
        })

        if(adapter != null)
            adapter?.notifyDataSetChanged()
    }


    fun getPdfpCell(text : String): PdfPCell{

        val pdfpcell = PdfPCell(Phrase(text))
        pdfpcell.setBorder(Rectangle.BOTTOM)
        pdfpcell.setBackgroundColor(BaseColor.WHITE)
        return pdfpcell
    }

    fun getPdfpCellWithNoBorder(text : String): PdfPCell{

        val pdfpcell = PdfPCell(Phrase(text))
        pdfpcell.setBorder(Rectangle.NO_BORDER)
        pdfpcell.setBackgroundColor(BaseColor.WHITE)
        pdfpcell.setPadding(3f)
        return pdfpcell
    }

    fun makeParagraphBold(text : String): Paragraph{

        val paragraph = Paragraph(text)
        paragraph.font.style = Font.BOLD
        return paragraph
    }

    fun getPdfpCellWithBlackBorder(text : String): PdfPCell{

        val phrase = Phrase(text)
        phrase.font.setStyle(Font.BOLD)
        val pdfpcell = PdfPCell(phrase)
        pdfpcell.setBorder(Rectangle.BOX)
        pdfpcell.setBorderColor(BaseColor.BLACK)
        pdfpcell.borderWidth = 2f
        pdfpcell.setBackgroundColor(BaseColor.WHITE)
        pdfpcell.setPadding(3f)
        return pdfpcell
    }


}
