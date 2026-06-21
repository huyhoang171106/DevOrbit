package vn.edu.uit.devorbit.mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tech_stacks")
data class TechStackEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)
