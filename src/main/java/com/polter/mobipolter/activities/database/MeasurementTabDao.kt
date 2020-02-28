package com.polter.mobipolter.activities.database

import android.arch.persistence.room.*
import com.polter.mobipolter.activities.model.MeasurementTabEntity

@Dao
interface MeasurementTabDao {


    @Query("SELECT * FROM LogDetailMeasurementTab WHERE measureLogDetailID = :id")
    fun getDetailById(id : Int): MeasurementTabEntity

    /*@Query("SELECT * FROM LogDetailBasicTab WHERE logTitle LIKE :title")
    fun findByTitle(title: String): LogStackEntity*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg todo: MeasurementTabEntity)

    @Delete
    fun delete(todo: MeasurementTabEntity)

    @Delete
    fun deleteAll(list: List<MeasurementTabEntity>)

    @Update
    fun update(vararg todos: MeasurementTabEntity)

    @Query("UPDATE LogDetailMeasurementTab SET surveyingType = :surveyingType WHERE measureLogDetailID = :id")
    fun updateRecentSurveyingType(surveyingType : String, id : Int)

}