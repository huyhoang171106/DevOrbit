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
        apiService.getTechStacks().map { TechStackInfo(it.name) }
    } catch (e: Exception) { emptyList() }

    override suspend fun getRecentRepos(): List<RecentRepo> = try {
        apiService.getRecentDiscoveryRepos().map { it.toRecentRepo() }
    } catch (e: Exception) { emptyList() }

    override suspend fun searchRepos(query: String): List<RecentRepo> = try {
        apiService.searchDiscoveryRepos(query.takeIf { it.isNotBlank() }).map { it.toRecentRepo() }
    } catch (e: Exception) { emptyList() }

    override suspend fun getTopStacks(): List<TopStack> = try {
        apiService.getTopStacks().mapIndexed { index, name ->
            TopStack(name = name, count = index + 1)
        }
    } catch (e: Exception) { emptyList() }
}
