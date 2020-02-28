package com.polter.mobipolter.activities.database

import android.arch.persistence.room.*
import com.polter.mobipolter.activities.model.PhotoTabEntity


@Dao
interface PhotoTabDao {


    @Query("SELECT * FROM LogDetailPhotosTab WHERE photoLogDetailID = :id")
    fun getDetailById(id : Int):PhotoTabEntity

    /*@Query("SELECT * FROM LogDetailBasicTab WHERE logTitle LIKE :title")
    fun findByTitle(title: String): LogStackEntity*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg todo: PhotoTabEntity)

    @Delete
    fun delete(todo: PhotoTabEntity)

    @Delete
    fun deleteAll(list: List<PhotoTabEntity>)

    @Update()
    fun update(vararg todos: PhotoTabEntity)

}