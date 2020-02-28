package com.polter.mobipolter.activities.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.support.annotation.NonNull
import com.polter.mobipolter.activities.model.KindEntity
import com.polter.mobipolter.activities.model.SpeciesEntity
import com.polter.mobipolter.activities.repository.AppRepository


/**
 * [is subclass of ][AndroidViewModel]
 */
public class PredefinedListViewModel
/**
 * Application context should be passed to ViewModel. If activity context passed and configuration changed then activity will be destroyed,
 * which means MemoryLeak occurs because there is no activity to reference.
 *
 * @param application
 */
(@NonNull application: Application) : AndroidViewModel(application) {
    public val repository: AppRepository
    // val logListFromDB: List<LogStackEntity>
    val allSpecies: LiveData<List<SpeciesEntity>>
    val allKind: LiveData<List<KindEntity>>

    init {
        repository = AppRepository(application)
        allSpecies = repository.allSpecies
        allKind = repository.allKind
        //  logListFromDB = repository.logListFromDB
        // getCurrentLogEntryIndex = repository.getCurrentIndex()
    }

    /**
     * @Info: All the activities has to reference to ViewModel, not the repository. [NoteRepository]
     * So we must create methods for ViewModel that match the repository methods
     */

    //------------------Predefined List Data access

    fun insertSpecies(speciesEntity: SpeciesEntity){

        repository.insertSpeciesIntoDB(speciesEntity)
    }

    fun insertKind(kindEntity: KindEntity){

        repository.insertKindIntoDB(kindEntity)
    }




}
