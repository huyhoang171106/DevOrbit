package vn.edu.uit.devorbit.mobile.data.local.entity

import androidx.room.Entity

@Entity(tableName = "semester_courses", primaryKeys = ["courseId", "majorCode"])
data class SemesterCourseEntity(
    val courseId: Long,
    val majorCode: String,
    val semester: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
)
