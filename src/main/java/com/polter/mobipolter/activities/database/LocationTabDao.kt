package com.polter.mobipolter.activities.database

import android.arch.persistence.room.*
import com.polter.mobipolter.activities.model.LocationEntity

@Dao
interface LocationTabDao {


    @Query("SELECT * FROM LogDetailLocationTab WHERE locationLogDetailID = :id")
    fun getDetailById(id : Int): LocationEntity

    /*@Query("SELECT * FROM LogDetailBasicTab WHERE logTitle LIKE :title")
    fun findByTitle(title: String): LogStackEntity*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg todo: LocationEntity)

    @Delete
    fun delete(todo: LocationEntity)

    @Delete
    fun deleteAll(list: List<LocationEntity>)

    @Update
    fun update(vararg todos: LocationEntity)

}