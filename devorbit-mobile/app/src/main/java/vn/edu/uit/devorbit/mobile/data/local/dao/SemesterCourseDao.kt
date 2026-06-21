package vn.edu.uit.devorbit.mobile.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import vn.edu.uit.devorbit.mobile.data.local.entity.SemesterCourseEntity

@Dao
interface SemesterCourseDao {
    @Query("SELECT * FROM semester_courses ORDER BY addedAt ASC")
    fun getAllSemesterCourses(): Flow<List<SemesterCourseEntity>>

    @Query("SELECT courseId FROM semester_courses")
    suspend fun getSemesterCourseIds(): List<Long>

    @Upsert
    suspend fun addCourse(course: SemesterCourseEntity)

    @Query("DELETE FROM semester_courses WHERE courseId = :courseId")
    suspend fun removeCourse(courseId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM semester_courses WHERE courseId = :courseId)")
    suspend fun isCourseAdded(courseId: Long): Boolean
}
