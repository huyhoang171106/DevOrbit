package vn.edu.uit.devorbit.mobile.data.repository

import vn.edu.uit.devorbit.mobile.domain.repository.DiscoveryRepository
import vn.edu.uit.devorbit.mobile.domain.repository.RecentRepo
import vn.edu.uit.devorbit.mobile.domain.repository.TopStack
import vn.edu.uit.devorbit.mobile.domain.repository.TechStackInfo
import vn.edu.uit.devorbit.mobile.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiscoveryRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : DiscoveryRepository {

    override suspend fun getTechStacks(): List<TechStackInfo> = try {
        apiService.getTechStacks().map { TechStackInfo(it["name"] ?: "") }
    } catch (e: Exception) { emptyList() }

    override suspend fun getRecentRepos(): List<RecentRepo> = try {
        apiService.getRecentDiscoveryRepos().map {
            RecentRepo(
                id = (it["id"] as? Number)?.toLong() ?: 0,
                name = it["displayName"] as? String ?: "",
                description = it["description"] as? String ?: "",
                githubUrl = it["githubUrl"] as? String ?: "",
                language = it["primaryLanguage"] as? String ?: "",
                stars = (it["stars"] as? Number)?.toInt() ?: 0,
                courseName = it["courseName"] as? String
            )
        }
    } catch (e: Exception) { emptyList() }

    override suspend fun getTopStacks(): List<TopStack> = try {
        apiService.getTopStacks().map {
            TopStack(
                name = it["name"] as? String ?: "",
                count = (it["count"] as? Number)?.toInt() ?: 0
            )
        }
    } catch (e: Exception) { emptyList() }
}
