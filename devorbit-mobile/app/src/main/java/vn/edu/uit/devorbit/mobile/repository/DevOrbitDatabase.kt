package vn.edu.uit.devorbit.mobile.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import vn.edu.uit.devorbit.mobile.model.domain.LearningTask

@Database(entities = [LearningTask::class], version = 1, exportSchema = false)
abstract class DevOrbitDatabase : RoomDatabase() {
    abstract fun learningTaskDao(): LearningTaskDao

    companion object {
        @Volatile private var INSTANCE: DevOrbitDatabase? = null

        fun getInstance(context: Context): DevOrbitDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DevOrbitDatabase::class.java,
                    "devorbit_academic.db"
                ).fallbackToDestructiveMigration()
                .build().also { INSTANCE = it }
            }
        }
    }
}
