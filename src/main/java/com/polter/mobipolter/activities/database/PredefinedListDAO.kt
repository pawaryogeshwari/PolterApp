package com.polter.mobipolter.activities.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.polter.mobipolter.activities.model.KindEntity
import com.polter.mobipolter.activities.model.SpeciesEntity

@Dao
interface PredefinedListDAO {


    @Query("SELECT * FROM Species")
    fun getAllSpecies(): LiveData<List<SpeciesEntity>>

    /*@Query("SELECT * FROM LogDetailBasicTab WHERE logTitle LIKE :title")
    fun findByTitle(title: String): LogStackEntity*/

    @Insert
    fun insertSpecies(todo: List<SpeciesEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSpecies(vararg todo: SpeciesEntity)


    @Delete
    fun deleteSpecies(todo: SpeciesEntity)

    @Delete
    fun deleteAllSpecies(list: List<SpeciesEntity>)

    @Update()
    fun updateSpecies(vararg todos: SpeciesEntity)


    @Query("SELECT * FROM Kind")
    fun getAllKind(): LiveData<List<KindEntity>>

    /*@Query("SELECT * FROM LogDetailBasicTab WHERE logTitle LIKE :title")
    fun findByTitle(title: String): LogStackEntity*/

    @Insert
    fun insertKinds(todo: List<KindEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertKind(vararg todo: KindEntity)


    @Delete
    fun deleteKind(todo: KindEntity)

    @Delete
    fun deleteAllKind(list: List<KindEntity>)

    @Update()
    fun updateKind(vararg todos: KindEntity)
}