package com.polter.mobipolter.activities.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import android.support.annotation.NonNull
import com.polter.mobipolter.activities.converters.*
import com.polter.mobipolter.activities.model.*

@Database(entities = arrayOf(LogStackEntity::class, CurrentLogEntry::class, BasicTabEntity::class, PhotoTabEntity::class
    , MeasurementTabEntity::class, LocationEntity::class,SpeciesEntity::class,KindEntity::class ), version = 4)
@TypeConverters(BasicTabDetailConverter::class, PhotoTabConverter::class,EstimationConverter::class, SectionConverter::class,HeightListConverter::class
                ,MeasurementTabConverter::class, LogsConverter::class, LocationEntityConverter::class,PhotoEntityConverter::class,
                SpeciesConverter::class,KindConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun logDao(): LogEntryDao
    abstract fun currentLogEntryDao(): CurrentLogEntryDao
    abstract fun basicTabDao(): BasicTabDao
    abstract fun photosTabDao(): PhotoTabDao
    abstract fun measurementTabDao(): MeasurementTabDao
    abstract fun locationTabDao(): LocationTabDao
    abstract fun predefinedListDao(): PredefinedListDAO

    companion object {


        private var instance: AppDatabase? = null
        // Synchronized means only thread can access to instance at a time
        // also prevents create two instance at a time
        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase::class.java, "PolterAppDB")
                        .fallbackToDestructiveMigration() // TODO: remove this in production and provide migration queries
                        .addCallback(roomCallback)
                        .build()
            }
            return instance!!
        }

        private val roomCallback = object : Callback() {

            @Override
            override fun onCreate(@NonNull db: SupportSQLiteDatabase) {
                super.onCreate(db)
                System.out.print("Created DB")
               // DatabaseUtils(instance!!).

            }
        }
    }
}
