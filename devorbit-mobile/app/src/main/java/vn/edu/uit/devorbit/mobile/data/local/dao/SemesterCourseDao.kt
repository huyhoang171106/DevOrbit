package vn.edu.uit.devorbit.mobile.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import vn.edu.uit.devorbit.mobile.data.local.entity.SemesterCourseEntity

@Dao
interface SemesterCourseDao {
    @Query("SELECT * FROM semester_courses WHERE majorCode = :majorCode ORDER BY addedAt ASC")
    fun getSemesterCoursesByMajor(majorCode: String): Flow<List<SemesterCourseEntity>>

    @Query("SELECT courseId FROM semester_courses WHERE majorCode = :majorCode")
    suspend fun getSemesterCourseIds(majorCode: String): List<Long>

    @Query("SELECT * FROM semester_courses WHERE semester = :semester AND majorCode = :majorCode ORDER BY addedAt ASC")
    suspend fun getCoursesBySemester(semester: Int, majorCode: String): List<SemesterCourseEntity>

    @Upsert
    suspend fun addCourse(course: SemesterCourseEntity)

    @Query("UPDATE semester_courses SET semester = :newSemester WHERE courseId = :courseId AND majorCode = :majorCode")
    suspend fun moveCourse(courseId: Long, newSemester: Int, majorCode: String)

    @Query("DELETE FROM semester_courses WHERE courseId = :courseId AND majorCode = :majorCode")
    suspend fun removeCourse(courseId: Long, majorCode: String)

    @Query("SELECT EXISTS(SELECT 1 FROM semester_courses WHERE courseId = :courseId AND majorCode = :majorCode)")
    suspend fun isCourseAdded(courseId: Long, majorCode: String): Boolean

    @Query("SELECT semester FROM semester_courses WHERE courseId = :courseId AND majorCode = :majorCode")
    suspend fun getCourseSemester(courseId: Long, majorCode: String): Int?

    @Query("DELETE FROM semester_courses WHERE majorCode = :majorCode")
    suspend fun clearByMajor(majorCode: String)
}
