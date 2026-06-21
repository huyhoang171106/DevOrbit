package vn.edu.uit.devorbit.mobile.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import vn.edu.uit.devorbit.mobile.data.local.entity.TechStackEntity

@Dao
interface TechStackDao {
    @Query("SELECT * FROM tech_stacks ORDER BY name ASC")
    fun getAllTechStacks(): Flow<List<TechStackEntity>>

    @Query("SELECT name FROM tech_stacks")
    suspend fun getTechStackNames(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTechStack(stack: TechStackEntity)

    @Query("DELETE FROM tech_stacks WHERE id = :id")
    suspend fun deleteTechStack(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM tech_stacks WHERE name = :name)")
    suspend fun isTechStackAdded(name: String): Boolean
}
