package com.rj.poc.coraltask.data.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [Box::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun boxDao(): BoxDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "box_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
