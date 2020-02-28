package com.polter.mobipolter.activities.database

import android.arch.persistence.room.*
import com.polter.mobipolter.activities.model.CurrentLogEntry

@Dao
interface CurrentLogEntryDao {
    @Query("SELECT * FROM CurrentLogEntry")
    fun getCurrentLogEntry(): CurrentLogEntry

    @Insert
    fun insert(vararg todo: CurrentLogEntry)

    @Delete
    fun delete(todo: CurrentLogEntry)

    @Update
    fun updateCurrentLogEntry(vararg todos: CurrentLogEntry)


}