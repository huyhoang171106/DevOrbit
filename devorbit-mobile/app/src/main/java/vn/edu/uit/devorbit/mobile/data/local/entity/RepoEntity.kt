package vn.edu.uit.devorbit.mobile.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "repositories",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["courseId"])]
)
data class RepoEntity(
    @PrimaryKey val id: Long,
    val courseId: Long?,
    val displayName: String,
    val description: String?,
    val githubUrl: String,
    val primaryLanguage: String?,
    val stars: Int,
    val aiClassification: String?,
    val lastUpdated: Long = System.currentTimeMillis()
)
