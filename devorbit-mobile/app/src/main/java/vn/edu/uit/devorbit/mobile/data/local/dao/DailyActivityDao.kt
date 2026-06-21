package vn.edu.uit.devorbit.mobile.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import vn.edu.uit.devorbit.mobile.data.local.entity.DailyActivityEntity

@Dao
interface DailyActivityDao {
    @Query("SELECT * FROM daily_activity WHERE studentCode = :studentCode ORDER BY date DESC LIMIT 14")
    fun getRecentActivities(studentCode: String): Flow<List<DailyActivityEntity>>

    @Query("SELECT * FROM daily_activity WHERE studentCode = :studentCode AND date = :date")
    suspend fun getActivity(studentCode: String, date: String): DailyActivityEntity?

    @Upsert
    suspend fun upsertActivity(activity: DailyActivityEntity)

    @Query("SELECT SUM(reposViewed) FROM daily_activity WHERE studentCode = :studentCode AND date >= :since")
    suspend fun getTotalReposViewedSince(studentCode: String, since: String): Int
}
