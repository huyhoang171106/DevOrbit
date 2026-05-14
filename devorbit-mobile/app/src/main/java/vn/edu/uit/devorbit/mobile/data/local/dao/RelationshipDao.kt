package vn.edu.uit.devorbit.mobile.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseRelationshipEntity

@Dao
interface RelationshipDao {
    @Query("SELECT * FROM course_relationships")
    fun getAllRelationships(): Flow<List<CourseRelationshipEntity>>

    @Upsert
    suspend fun upsertRelationships(relationships: List<CourseRelationshipEntity>)

    @Query("DELETE FROM course_relationships")
    suspend fun deleteAll()
}
