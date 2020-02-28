package com.polter.mobipolter.activities.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.support.annotation.NonNull
import com.polter.mobipolter.activities.model.*

import com.polter.mobipolter.activities.repository.AppRepository


/**
 * [is subclass of ][AndroidViewModel]
 */
public class LogDetailViewModel
/**
 * Application context should be passed to ViewModel. If activity context passed and configuration changed then activity will be destroyed,
 * which means MemoryLeak occurs because there is no activity to reference.
 *
 * @param application
 */
(@NonNull application: Application) : AndroidViewModel(application) {
    public val repository: AppRepository

    init {
        repository = AppRepository(application)
    }

    /**
     * @Info: All the activities has to reference to ViewModel, not the repository.
     * So we must create methods for ViewModel that match the repository methods
     */

    fun findLogEntityById(id: Int) : LiveData<LogStackEntity>?{
        return repository.findLogEntryFromDB(id)
    }


    fun findLogById(id: Int) : LogStackEntity?{
        return repository.findLogById(id)
    }


    fun updateLogBasicEntityById(updatedBasicEntity : BasicTabEntity , id: Int){
        return repository.updateLogBasicEntityFromDB(updatedBasicEntity,id)
    }

    fun updateLogMeasurementEntityById(updatedMeasureEntity : MeasurementTabEntity , id: Int){
        return repository.updateLogMeasureEntityFromDB(updatedMeasureEntity,id)
    }

    fun updateLogPhotoEntityById(photoTabEntity: PhotoTabEntity , id: Int){
        return repository.updateLogPhotoEntityFromDB(photoTabEntity,id)
    }

    fun updateLogLocationEntityById(updatedLocationEntity : LocationEntity , id: Int){
        return repository.updateLogLocationEntityFromDB(updatedLocationEntity,id)
    }


    fun deleteLogEntry(logEntity: LogStackEntity) {
        repository.deleteLogEntryInDB(logEntity)
    }


    fun insert(note: LogStackEntity) {
        repository.insertLogEntryInDB(note)
    }

    fun updateForestOwnerById(forestOwner : String, id: Int) {
        repository.updateForestOwnerFromDB(forestOwner,id)
    }

    fun updateMeasureLogStackById(species : String?, kind: String?, quality : String?, length: Double,volume: Double, logcount :Int,volumeM3 : Double,id : Int) {
        repository.updateMeasureLogStackFromDB(species,kind,quality,length,volume,logcount,volumeM3,id)
    }

    /*
    Accessing repository for Getting data from Table - LogDetailBasicTab
    */


    fun insertBasicTabDetail(basicTabEntity: BasicTabEntity) {
        repository.insertBasicTabIntoDB(basicTabEntity)
    }


    fun findLogBasicTabById(id: Int) : BasicTabEntity?{
        return repository.findBasicTabDetailById(id)
    }

    fun updateBasicTabDetail(basicTabEntity: BasicTabEntity) {
        repository.updateBasicTabIntoDB(basicTabEntity)
    }

    // Update last used tab
    fun updateLogRecentTab(tab: String,surveyType:String, id: Int) {
        repository.updateLogRecentTab(tab,surveyType,id)
    }

    // Update last selected surveying type
    fun updateLogRecentSurveyingType(surveyType: String, id: Int) {
        repository.updateLogRecentSurveyingType(surveyType,id)
    }

    // delete from BasicTab

    fun deleteLogFromBasicTab(basicTabEntity: BasicTabEntity) {
        repository.deleteLogEntryBasicTab(basicTabEntity)
    }


    /*
    Accessing repository for Getting data from Table - LogDetailMeasureTab
    */


    fun insertMeasureTabDetail(measurementTabEntity: MeasurementTabEntity) {
        repository.insertMeasureTabIntoDB(measurementTabEntity)
    }


    fun findLogMeasureTabById(id: Int) : MeasurementTabEntity?{
        return repository.findMeasureTabDetailById(id)
    }

    fun updateMeasureTabDetail(measurementTabEntity: MeasurementTabEntity) {
        repository.updateMeasureTabIntoDB(measurementTabEntity)
    }


    // delete from MeasureTab

    fun deleteLogFromMeasureTab(measurementTabEntity: MeasurementTabEntity) {
        repository.deleteLogEntryMeasureTab(measurementTabEntity)
    }


    /*
    Accessing repository for Getting data from Table - LogDetailPhotosTab
    */


    fun insertPhotoTabDetail(photoTabEntity: PhotoTabEntity) {
        repository.insertPhotoTabIntoDB(photoTabEntity)
    }


    fun findLogPhotoTabById(id: Int) : PhotoTabEntity?{
        return repository.findPhotoTabDetailById(id)
    }

    fun updatePhotoTabDetail(photoTabEntity: PhotoTabEntity) {
        repository.updatePhotoTabIntoDB(photoTabEntity)
    }


    // delete from PhotoTab

    fun deleteLogFromPhotoTab(photoTabEntity: PhotoTabEntity) {
        repository.deleteLogEntryPhotoTab(photoTabEntity)
    }



    /*
    Accessing repository for Getting data from Table - LogDetailLocationTab
    */


    fun insertLocationTabDetail(locationEntity: LocationEntity) {
        repository.insertLocationTabIntoDB(locationEntity)
    }


    fun findLocationTabById(id: Int) : LocationEntity?{
        return repository.findLocationTabDetailById(id)
    }

    fun updateLocationTabDetail(locationEntity: LocationEntity) {
        repository.updateLocationTabIntoDB(locationEntity)
    }

    // delete from LocationTab

    fun deleteLogFromLocationTab(locationEntity: LocationEntity) {
        repository.deleteLogEntryLocationTab(locationEntity)
    }

}
