package com.polter.mobipolter.activities.database

import android.arch.persistence.room.*
import com.polter.mobipolter.activities.model.BasicTabEntity


@Dao
interface BasicTabDao {


    @Query("SELECT * FROM LogDetailBasicTab WHERE basicLogDetailID = :id")
    fun getDetailById(id : Int):BasicTabEntity
    /*@Query("SELECT * FROM LogDetailBasicTab WHERE logTitle LIKE :title")
    fun findByTitle(title: String): LogStackEntity*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg todo: BasicTabEntity)

    @Delete
    fun delete(todo: BasicTabEntity)

    @Delete
    fun deleteAll(list: List<BasicTabEntity>)

    @Update()
    fun update(vararg todos: BasicTabEntity)

}