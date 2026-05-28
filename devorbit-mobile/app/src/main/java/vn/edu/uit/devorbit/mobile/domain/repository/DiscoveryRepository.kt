package vn.edu.uit.devorbit.mobile.domain.repository

data class RecentRepo(val id: Long, val name: String, val description: String, val githubUrl: String, val language: String, val stars: Int, val courseName: String?)
data class TopStack(val name: String, val count: Int)
data class TechStackInfo(val name: String)

interface DiscoveryRepository {
    suspend fun getTechStacks(): List<TechStackInfo>
    suspend fun getRecentRepos(): List<RecentRepo>
    suspend fun getTopStacks(): List<TopStack>
}
