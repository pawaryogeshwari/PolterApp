package com.polter.mobipolter.activities.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.polter.mobipolter.activities.model.*

@Dao
interface LogEntryDao {
    @Query("SELECT * FROM LogEntryItems")
    fun getAll(): List<LogStackEntity>

    @Query("SELECT * FROM LogEntryItems WHERE logTitle LIKE :title")
    fun findByTitle(title: String): LogStackEntity

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg todo: LogStackEntity)

    @Delete
    fun delete(todo: LogStackEntity)

    @Delete
    fun deleteAll(list: List<LogStackEntity>)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateTodo(vararg todos: LogStackEntity)

    @Query("SELECT * FROM LogEntryItems WHERE logId = :id")
    fun findLogEntryById(id: Int): LiveData<LogStackEntity>

    @Query("SELECT * FROM LogEntryItems WHERE logId = :id")
    fun findLogById(id: Int): LogStackEntity

    @Query("SELECT * FROM LogEntryItems")
    fun getAllLive(): LiveData<List<LogStackEntity>>


    @Query("UPDATE LogEntryItems SET recentTab = :recentTabName,surveyingType = :surveyingType WHERE logId = :id")
    fun updateRecentTab(recentTabName : String,surveyingType : String, id : Int)

    @Query("UPDATE LogEntryItems SET surveyingType = :surveyingType WHERE logId = :id")
    fun updateRecentSurveyingType(surveyingType : String, id : Int)

    @Query("UPDATE LogEntryItems SET logBasicEntity = :updatedBasicEntity WHERE logId = :id")
    fun updateBasicEntityById(updatedBasicEntity : BasicTabEntity, id : Int)

    @Query("UPDATE LogEntryItems SET logMeasurementEntity = :updatedMeasureEntity WHERE logId = :id")
    fun updateMeasureEntityById(updatedMeasureEntity : MeasurementTabEntity, id : Int)

    @Query("UPDATE LogEntryItems SET logPhotoEntity = :updatedPhotoEntity WHERE logId = :id")
    fun updatePhotoEntityById(updatedPhotoEntity : PhotoTabEntity, id : Int)

    @Query("UPDATE LogEntryItems SET logLocationEntity = :updatedLocationEntity WHERE logId = :id")
    fun updateLocationEntityById(updatedLocationEntity : LocationEntity, id : Int)


    @Query("UPDATE LogEntryItems SET logForestOwner = :logForestOwnerValue WHERE logId = :id")
    fun updateForestOwnerById(logForestOwnerValue : String, id : Int)


    @Query("UPDATE LogEntryItems SET logSpeciesCount = :species,logKind = :kind,logQuality = :quality,logLength = :length,logVolume = :volume,logCount = :logcount,logVolumeM3 = :volumeM3 WHERE logId = :id")
    fun updateMeasureToLogStackById(species : String?, kind: String?, quality : String?, length: Double,volume: Double, logcount :Int,volumeM3 : Double,id : Int)


}