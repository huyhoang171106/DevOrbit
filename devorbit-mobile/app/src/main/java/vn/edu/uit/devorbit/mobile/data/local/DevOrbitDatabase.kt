package vn.edu.uit.devorbit.mobile.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import vn.edu.uit.devorbit.mobile.data.local.dao.CourseDao
import vn.edu.uit.devorbit.mobile.data.local.dao.DailyActivityDao
import vn.edu.uit.devorbit.mobile.data.local.dao.RepoDao
import vn.edu.uit.devorbit.mobile.data.local.dao.RelationshipDao
import vn.edu.uit.devorbit.mobile.data.local.dao.SemesterCourseDao
import vn.edu.uit.devorbit.mobile.data.local.dao.TechStackDao
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseRelationshipEntity
import vn.edu.uit.devorbit.mobile.data.local.entity.DailyActivityEntity
import vn.edu.uit.devorbit.mobile.data.local.entity.RepoEntity
import vn.edu.uit.devorbit.mobile.data.local.entity.SemesterCourseEntity
import vn.edu.uit.devorbit.mobile.data.local.entity.TechStackEntity

@Database(
    entities = [CourseEntity::class, RepoEntity::class, CourseRelationshipEntity::class, DailyActivityEntity::class, SemesterCourseEntity::class, TechStackEntity::class],
    version = 9,
    exportSchema = true
)
abstract class DevOrbitDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun repoDao(): RepoDao
    abstract fun relationshipDao(): RelationshipDao
    abstract fun dailyActivityDao(): DailyActivityDao
    abstract fun semesterCourseDao(): SemesterCourseDao
    abstract fun techStackDao(): TechStackDao

    companion object {
        const val DATABASE_NAME = "devorbit_db"
    }
}
