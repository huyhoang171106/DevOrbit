package vn.edu.uit.devorbit.mobile.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import vn.edu.uit.devorbit.mobile.data.local.dao.CourseDao
import vn.edu.uit.devorbit.mobile.data.local.dao.DailyActivityDao
import vn.edu.uit.devorbit.mobile.data.local.dao.RepoDao
import vn.edu.uit.devorbit.mobile.data.local.dao.RelationshipDao
import vn.edu.uit.devorbit.mobile.data.local.dao.SemesterCourseDao
import vn.edu.uit.devorbit.mobile.data.local.dao.TaskDao
import vn.edu.uit.devorbit.mobile.data.local.dao.TechStackDao
import vn.edu.uit.devorbit.mobile.data.local.entity.*

@Database(
    entities = [CourseEntity::class, RepoEntity::class, CourseRelationshipEntity::class, TaskEntity::class, DailyActivityEntity::class, SemesterCourseEntity::class, TechStackEntity::class],
    version = 8,
    exportSchema = true
)
abstract class DevOrbitDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun repoDao(): RepoDao
    abstract fun relationshipDao(): RelationshipDao
    abstract fun taskDao(): TaskDao
    abstract fun dailyActivityDao(): DailyActivityDao
    abstract fun semesterCourseDao(): SemesterCourseDao
    abstract fun techStackDao(): TechStackDao

    companion object {
        const val DATABASE_NAME = "devorbit_db"
    }
}
