package vn.edu.uit.devorbit.mobile.data.local.entity

import androidx.room.Entity

@Entity(tableName = "daily_activity", primaryKeys = ["studentCode", "date"])
data class DailyActivityEntity(
    val studentCode: String,
    val date: String,
    val reposViewed: Int = 0,
    val tasksCompleted: Int = 0,
    val tasksTotal: Int = 0,
    val studyMinutes: Int = 0
)
