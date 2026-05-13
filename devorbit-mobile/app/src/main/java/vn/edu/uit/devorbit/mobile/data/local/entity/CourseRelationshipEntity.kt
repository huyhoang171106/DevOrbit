package vn.edu.uit.devorbit.mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "course_relationships")
data class CourseRelationshipEntity(
    @PrimaryKey val id: Long,
    val fromCourseId: Long,
    val toCourseId: Long,
    val type: String
)
