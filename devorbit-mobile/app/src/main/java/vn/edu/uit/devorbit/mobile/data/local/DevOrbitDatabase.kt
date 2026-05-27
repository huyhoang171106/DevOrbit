package vn.edu.uit.devorbit.mobile.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import vn.edu.uit.devorbit.mobile.data.local.dao.CourseDao
import vn.edu.uit.devorbit.mobile.data.local.dao.RepoDao
import vn.edu.uit.devorbit.mobile.data.local.dao.RelationshipDao
import vn.edu.uit.devorbit.mobile.data.local.dao.TaskDao
import vn.edu.uit.devorbit.mobile.data.local.entity.*

@Database(
    entities = [CourseEntity::class, RepoEntity::class, CourseRelationshipEntity::class, TaskEntity::class],
    version = 3,
    exportSchema = false
)
abstract class DevOrbitDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun repoDao(): RepoDao
    abstract fun relationshipDao(): RelationshipDao
    abstract fun taskDao(): TaskDao

    companion object {
        const val DATABASE_NAME = "devorbit_db"
    }
}
