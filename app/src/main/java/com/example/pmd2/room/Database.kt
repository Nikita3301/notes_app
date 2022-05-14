package com.example.pmd2.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TasksEntity::class], version = 1)
abstract class Database : RoomDatabase() {

    abstract fun tasksDao(): TasksDao

    companion object {
        @Volatile
        private var instance: com.example.pmd2.room.Database? = null

        fun getDatabase(context: Context): com.example.pmd2.room.Database {
            if (instance == null) {
                synchronized(this) {
                    instance = buildDatabase(context)
                }
            }
            return instance!!
        }

        private fun buildDatabase(context: Context): com.example.pmd2.room.Database {
            return Room.databaseBuilder(
                context.applicationContext,
                com.example.pmd2.room.Database::class.java,
                "tasks_database"
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}
