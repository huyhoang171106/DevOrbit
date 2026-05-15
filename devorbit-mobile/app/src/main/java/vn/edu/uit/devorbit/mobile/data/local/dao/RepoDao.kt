package vn.edu.uit.devorbit.mobile.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import vn.edu.uit.devorbit.mobile.data.local.entity.RepoEntity

@Dao
interface RepoDao {
    @Query("SELECT * FROM repositories WHERE courseId = :courseId")
    fun getReposByCourse(courseId: Long): Flow<List<RepoEntity>>

    @Query("SELECT * FROM repositories ORDER BY lastUpdated DESC LIMIT 10")
    fun getRecentRepos(): Flow<List<RepoEntity>>

    @Upsert
    suspend fun upsertRepos(repos: List<RepoEntity>)

    @Query("DELETE FROM repositories WHERE courseId = :courseId")
    suspend fun deleteReposByCourse(courseId: Long)
}
