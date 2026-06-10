package vn.edu.uit.devorbit.mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val courseId: Long? = null,
    val estimatedMinutes: Int = 0,
    val achievedMinutes: Int = 0,
    val deadline: Long? = null,
    val priority: Int = 0,
    val completed: Boolean = false,
    val taskType: String = "general",
    val parentTaskId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
