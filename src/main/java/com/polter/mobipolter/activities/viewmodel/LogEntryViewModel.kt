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
public class LogEntryViewModel
/**
 * Application context should be passed to ViewModel. If activity context passed and configuration changed then activity will be destroyed,
 * which means MemoryLeak occurs because there is no activity to reference.
 *
 * @param application
 */
 (@NonNull application: Application) : AndroidViewModel(application) {
    public val repository: AppRepository
    val allNotes: LiveData<List<LogStackEntity>>
   // val logListFromDB: List<LogStackEntity>
    var getCurrentLogEntryIndex: CurrentLogEntry ? = null

    init {
        repository = AppRepository(application)
        allNotes = repository.allNotes
      //  logListFromDB = repository.logListFromDB
       // getCurrentLogEntryIndex = repository.getCurrentIndex()
    }

    /**
     * @Info: All the activities has to reference to ViewModel, not the repository. [NoteRepository]
     * So we must create methods for ViewModel that match the repository methods
     */

    /*
    Accessing repository for Getting data from Table - LogEntryItems
    */

    fun insert(note: LogStackEntity) {
        repository.insertLogEntryInDB(note)
    }

    fun update(note: LogStackEntity) {
        repository.updateLogEntryInDB(note)
    }

    fun delete(note: LogStackEntity) {
        repository.deleteLogEntryInDB(note)
    }

    fun deleteList(list: List<LogStackEntity>) {
        repository.deleteLogEntryList(list)
    }


    fun insertBasicTabDetail(basicTabEntity: BasicTabEntity) {
        repository.insertBasicTabIntoDB(basicTabEntity)
    }


    // Delete All BasicTab Entries
    fun deleteBasicTabList(list: List<BasicTabEntity>) {
        repository.deleteLogEntryBasicList(list)
    }

    // Delete All MeasureTab Entries
    fun deleteMeasureTabList(list: List<MeasurementTabEntity>) {
        repository.deleteLogEntryMeasureList(list)
    }

    // Delete All Photo Tab Entries
    fun deletePhotoTabList(list: List<PhotoTabEntity>) {
        repository.deleteLogEntryPhotoList(list)
    }

    // Delete All Location Tab Entries
    fun deleteLocationTabList(list: List<LocationEntity>) {
        repository.deleteLogEntryLocationList(list)
    }


    //Find Entity
    fun findLogById(id: Int) : LogStackEntity?{
        return repository.findLogById(id)
    }


    //insert measurement

    fun insertMeasureTabDetail(measurementTabEntity: MeasurementTabEntity) {
        repository.insertMeasureTabIntoDB(measurementTabEntity)
    }

    //insert Photo
    fun insertPhotoTabDetail(photoTabEntity: PhotoTabEntity) {
        repository.insertPhotoTabIntoDB(photoTabEntity)
    }

    //.insert location
    fun insertLocationTabDetail(locationEntity: LocationEntity) {
        repository.insertLocationTabIntoDB(locationEntity)
    }



    /*
    Accessing repository for Getting data from Table - CurrentLogEntry


    fun insertIntoCurrentLogEntry(currentLogEntry: CurrentLogEntry) {
        repository.insertCurrentLogEntryInDB(currentLogEntry)
    }

    fun updateIntoCurrentLogEntry(currentLogEntry: CurrentLogEntry) {
        repository.updateCurrentLogEntryInDB(currentLogEntry)
    }

    fun deleteIntoCurrentLogEntry(currentLogEntry: CurrentLogEntry) {
        repository.deleteCurrentLogEntryInDB(currentLogEntry)
    }



    fun deleteAllNotes() {
        repository.deleteAllNotes()
    }*/


    //------------------Predefined List Data access

    fun insertSpecies(list:List<SpeciesEntity>){

        repository.insertAllSpeciesIntoDB(list)

    }


    fun insertKind(list:List<KindEntity>){

        repository.insertAllKindIntoDB(list)
    }
}
