package com.polter.mobipolter.activities.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import com.polter.mobipolter.activities.database.*
import com.polter.mobipolter.activities.model.*

import java.lang.Exception
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class AppRepository(application: Application) {
    private val dao: LogEntryDao
    private val currentLogEntryDao: CurrentLogEntryDao
    private val basicTabDao: BasicTabDao
    private val photoTabDao: PhotoTabDao
    private val measurementTabDao: MeasurementTabDao
    private val locationTabDao: LocationTabDao
    private val predefinedListDao: PredefinedListDAO


    private var executor: Executor ?= null

    val allNotes: LiveData<List<LogStackEntity>>
    val allSpecies: LiveData<List<SpeciesEntity>>
    val allKind: LiveData<List<KindEntity>>
   // val logListFromDB: List<LogStackEntity>

    init {
        val database = AppDatabase.getInstance(application)
        dao = database.logDao()
        currentLogEntryDao = database.currentLogEntryDao()
        basicTabDao = database.basicTabDao()
        photoTabDao = database.photosTabDao()
        measurementTabDao = database.measurementTabDao()
        locationTabDao = database.locationTabDao()
        predefinedListDao = database.predefinedListDao()

       // logListFromDB = dao.getAll()
        allNotes = dao.getAllLive()  // Getting all log entries with data
        allSpecies = predefinedListDao.getAllSpecies() // Retrieve all species from DB
        allKind = predefinedListDao.getAllKind() // Retrieve all kind from DB


        //  getCurrentLogEntryIndex = currentLogEntryDao.getCurrentLogEntry() // Getting current log entry index
        executor = Executors.newSingleThreadExecutor()
    }

   /*

    All CRUD operations related to LogEntryItems table with LogEntryDAO

    */

    fun insertLogEntryInDB(logStackEntity: LogStackEntity) {
     //   InsertNoteAsyncTask(dao).execute(note)
        insertLogEntryIntoDB(logStackEntity)
    }

    fun updateLogEntryInDB(logStackEntity: LogStackEntity) {
        updateLogEntryIntoDB(logStackEntity)

    }

    fun deleteLogEntryInDB(logStackEntity: LogStackEntity) {
        deleteLogEntryIntoDB(logStackEntity)

    }

    fun deleteLogEntryList(logStackEntityList: List<LogStackEntity>) {
        deleteLogEntryListFromDB(logStackEntityList)

    }

    fun updateLogRecentTab(tab: String,surveyType:String,id:Int) {

        executor?.execute {

            dao.updateRecentTab(tab,surveyType,id)
        }

    }

    fun updateLogRecentSurveyingType(surveyType: String,id:Int) {

        executor?.execute {

           measurementTabDao.updateRecentSurveyingType(surveyType,id)
        }

    }


    /*fun findLogEntryById(id: Int) {
        findLogEntryFromDB(id)

    }*/


    fun insertLogEntryIntoDB(logStackEntity : LogStackEntity){

        executor?.execute {

            dao.insertAll(logStackEntity)
        }

    }

    fun updateLogEntryIntoDB(logStackEntity : LogStackEntity){

        executor?.execute {

            dao.updateTodo(logStackEntity)
        }

    }

    fun deleteLogEntryIntoDB(logStackEntity : LogStackEntity){

        executor?.execute {

            dao.delete(logStackEntity)
        }

    }

    fun deleteLogEntryListFromDB(logStackEntityList :List<LogStackEntity>){

        executor?.execute {

            dao.deleteAll(logStackEntityList)
        }

    }

    fun findLogEntryFromDB(id : Int): LiveData<LogStackEntity>?{

        return findLogEntityById(id)

    }


    fun updateLogBasicEntityFromDB(basicTabEntity: BasicTabEntity, id : Int){

        executor?.execute {

            dao.updateBasicEntityById(basicTabEntity,id)
        }


    }

    fun updateLogMeasureEntityFromDB(measureTabEntity: MeasurementTabEntity, id : Int){

        executor?.execute {

            dao.updateMeasureEntityById(measureTabEntity,id)
        }


    }

    fun updateLogPhotoEntityFromDB(photoTabEntity: PhotoTabEntity, id : Int){

        executor?.execute {

            dao.updatePhotoEntityById(photoTabEntity,id)
        }


    }

    fun updateLogLocationEntityFromDB(locationEntity: LocationEntity, id : Int){

        executor?.execute {

            dao.updateLocationEntityById(locationEntity,id)
        }


    }

    fun updateForestOwnerFromDB(forestOwner : String, id : Int){

        executor?.execute {

            dao.updateForestOwnerById(forestOwner,id)
        }


    }

    fun updateMeasureLogStackFromDB(species : String?, kind: String?, quality : String?, length: Double,volume: Double, logcount :Int,volumeM3 : Double,id : Int){

        executor?.execute {

            dao.updateMeasureToLogStackById(species,kind,quality,length,volume,logcount,volumeM3,id)
        }


    }



    /*

    CRUD operations related to LogDetail - BasicTab

     */

    fun insertBasicTabIntoDB(basicTabEntity: BasicTabEntity){

        executor?.execute {

            basicTabDao.insert(basicTabEntity)
        }

    }

    fun updateBasicTabIntoDB(basicTabEntity: BasicTabEntity){

        executor?.execute {

            basicTabDao.update(basicTabEntity)
        }

    }

    fun  findBasicTabDetailById(logId : Int): BasicTabEntity?{

        try{
            return FindBasicTabDetailAsyncTask(logId).execute().get()
        }catch(e:Exception){
            return null;
        }
    }

    inner class FindBasicTabDetailAsyncTask(var logId: Int) : AsyncTask<Int, Void,BasicTabEntity>() {
        override fun doInBackground(vararg params: Int?): BasicTabEntity {
            return basicTabDao.getDetailById(logId)
        }


    }


    fun  findLogEntityById(logId : Int): LiveData<LogStackEntity>?{

        try{
            return FindLogEntityAsyncTask(logId).execute().get()
        }catch(e:Exception){
            return null;
        }
    }

    fun  findLogById(logId : Int): LogStackEntity?{

        try{
            return FindLogEntityByIdAsyncTask(logId).execute().get()
        }catch(e:Exception){
            return null;
        }
    }

    inner class FindLogEntityAsyncTask(var logId: Int) : AsyncTask<Int, Void,LiveData<LogStackEntity>>() {
        override fun doInBackground(vararg params: Int?): LiveData<LogStackEntity> {
            return dao.findLogEntryById(logId)
        }


    }

    inner class FindLogEntityByIdAsyncTask(var logId: Int) : AsyncTask<Int, Void,LogStackEntity?>() {
        override fun doInBackground(vararg params: Int?): LogStackEntity? {
            return dao.findLogById(logId)
        }


    }


    fun deleteLogEntryBasicTab(basicTabEntity: BasicTabEntity){

        executor?.execute {

            basicTabDao.delete(basicTabEntity)
        }

    }

    fun deleteLogEntryBasicList(basicTabEntityList:List<BasicTabEntity>){

        executor?.execute {

            basicTabDao.deleteAll(basicTabEntityList)
        }

    }

    /*

   CRUD operations related to LogDetail - MeasurementTab

    */

    fun insertMeasureTabIntoDB(measurementTabEntity: MeasurementTabEntity){

        executor?.execute {

            measurementTabDao.insert(measurementTabEntity)
        }

    }

    fun updateMeasureTabIntoDB(measurementTabEntity: MeasurementTabEntity){

        executor?.execute {

            measurementTabDao.update(measurementTabEntity)
        }

    }

    fun  findMeasureTabDetailById(logId : Int): MeasurementTabEntity?{

        try{
            return FindMeasureTabDetailAsyncTask(logId).execute().get()
        }catch(e:Exception){
            return null;
        }
    }

    inner class FindMeasureTabDetailAsyncTask(var logId: Int) : AsyncTask<Int, Void,MeasurementTabEntity>() {
        override fun doInBackground(vararg params: Int?): MeasurementTabEntity {
            return measurementTabDao.getDetailById(logId)
        }


    }


    fun deleteLogEntryMeasureTab(measurementTabEntity: MeasurementTabEntity){

        executor?.execute {

            measurementTabDao.delete(measurementTabEntity)
        }

    }

    fun deleteLogEntryMeasureList(measureTabEntityList:List<MeasurementTabEntity>){

        executor?.execute {

            measurementTabDao.deleteAll(measureTabEntityList)
        }

    }

    /*

    CRUD operations related to LogDetail - PhotosTab

     */

    fun insertPhotoTabIntoDB(photoTabEntity: PhotoTabEntity){

        executor?.execute {

            photoTabDao.insert(photoTabEntity)
        }

    }

    fun updatePhotoTabIntoDB(photoTabEntity: PhotoTabEntity){

        executor?.execute {

            photoTabDao.update(photoTabEntity)
        }

    }

    fun  findPhotoTabDetailById(logId : Int): PhotoTabEntity?{

        try{
            return FindPhotoTabDetailAsyncTask(logId).execute().get()
        }catch(e:Exception){
            return null;
        }
    }

    inner class FindPhotoTabDetailAsyncTask(var logId: Int) : AsyncTask<Int, Void,PhotoTabEntity>() {
        override fun doInBackground(vararg params: Int?): PhotoTabEntity {
            return photoTabDao.getDetailById(logId)
        }


    }

    fun deleteLogEntryPhotoTab(photoTabEntity: PhotoTabEntity){

        executor?.execute {

            photoTabDao.delete(photoTabEntity)
        }

    }


    fun deleteLogEntryPhotoList(photoTabEntityList:List<PhotoTabEntity>){

        executor?.execute {

            photoTabDao.deleteAll(photoTabEntityList)
        }

    }

    /*

   CRUD operations related to LogDetail - LocationTab

    */

    fun insertLocationTabIntoDB(locationEntity: LocationEntity){

        executor?.execute {

            locationTabDao.insert(locationEntity)
        }

    }

    fun updateLocationTabIntoDB(locationEntity: LocationEntity){

        executor?.execute {

            locationTabDao.update(locationEntity)
        }

    }

    fun  findLocationTabDetailById(logId : Int): LocationEntity?{

        try{
            return FindLocationTabDetailAsyncTask(logId).execute().get()
        }catch(e:Exception){
            return null;
        }
    }

    inner class FindLocationTabDetailAsyncTask(var logId: Int) : AsyncTask<Int, Void,LocationEntity>() {
        override fun doInBackground(vararg params: Int?): LocationEntity {
            return locationTabDao.getDetailById(logId)
        }


    }


    fun deleteLogEntryLocationTab(locationEntity: LocationEntity){

        executor?.execute {

            locationTabDao.delete(locationEntity)
        }

    }

    fun deleteLogEntryLocationList(locationTabEntityList:List<LocationEntity>){

        executor?.execute {

            locationTabDao.deleteAll(locationTabEntityList)
        }

    }

   // --------------------------------------------------------------------

  //                    PredefinedListDAO functions


    fun insertAllSpeciesIntoDB(list:List<SpeciesEntity>){
        executor?.execute {

            predefinedListDao.insertSpecies(list)
        }
    }

    fun insertSpeciesIntoDB(specie:SpeciesEntity){
        executor?.execute {

            predefinedListDao.insertSpecies(specie)
        }
    }


    fun insertAllKindIntoDB(list:List<KindEntity>){
        executor?.execute {

            predefinedListDao.insertKinds(list)
        }
    }

    fun insertKindIntoDB(specie:KindEntity){
        executor?.execute {

            predefinedListDao.insertKind(specie)
        }
    }


}
