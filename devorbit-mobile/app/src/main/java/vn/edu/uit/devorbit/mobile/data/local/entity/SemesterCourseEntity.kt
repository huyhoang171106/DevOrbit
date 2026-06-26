package vn.edu.uit.devorbit.mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "semester_courses")
data class SemesterCourseEntity(
    @PrimaryKey val courseId: Long,
    val semester: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
)
