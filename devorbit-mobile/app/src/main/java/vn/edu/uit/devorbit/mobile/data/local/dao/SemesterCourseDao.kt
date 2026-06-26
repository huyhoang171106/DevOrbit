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

    @Query("SELECT * FROM semester_courses WHERE semester = :semester ORDER BY addedAt ASC")
    suspend fun getCoursesBySemester(semester: Int): List<SemesterCourseEntity>

    @Upsert
    suspend fun addCourse(course: SemesterCourseEntity)

    @Query("UPDATE semester_courses SET semester = :newSemester WHERE courseId = :courseId")
    suspend fun moveCourse(courseId: Long, newSemester: Int)

    @Query("DELETE FROM semester_courses WHERE courseId = :courseId")
    suspend fun removeCourse(courseId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM semester_courses WHERE courseId = :courseId)")
    suspend fun isCourseAdded(courseId: Long): Boolean

    @Query("SELECT semester FROM semester_courses WHERE courseId = :courseId")
    suspend fun getCourseSemester(courseId: Long): Int?

    @Query("DELETE FROM semester_courses")
    suspend fun clearAll()
}
