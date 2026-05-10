package vn.edu.uit.devorbit.mobile.repository

import androidx.room.*
import vn.edu.uit.devorbit.mobile.model.domain.LearningTask

@Dao
interface LearningTaskDao {
    @Query("SELECT * FROM learning_tasks ORDER BY priority DESC, deadline ASC")
    suspend fun getAll(): List<LearningTask>

    @Query("SELECT * FROM learning_tasks WHERE subjectId = :subjectId ORDER BY deadline ASC")
    suspend fun getBySubject(subjectId: Long): List<LearningTask>

    @Query("SELECT * FROM learning_tasks WHERE completed = 0 ORDER BY deadline ASC")
    suspend fun getIncomplete(): List<LearningTask>

    @Query("SELECT * FROM learning_tasks WHERE completed = 1 ORDER BY createdAt DESC")
    suspend fun getCompleted(): List<LearningTask>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: LearningTask): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<LearningTask>)

    @Query("UPDATE learning_tasks SET completed = :completed WHERE id = :id")
    suspend fun setCompleted(id: Long, completed: Boolean)

    @Query("UPDATE learning_tasks SET achievedMinutes = achievedMinutes + :minutes WHERE id = :id")
    suspend fun addAchievedMinutes(id: Long, minutes: Int)

    @Delete
    suspend fun delete(task: LearningTask)
}
