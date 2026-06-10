package vn.edu.uit.devorbit.mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: Long,
    val maMH: String,
    val tenMH: String,
    val credits: Int,
    val description: String?,
    val lastUpdated: Long = System.currentTimeMillis()
)
