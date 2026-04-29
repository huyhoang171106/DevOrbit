package vn.edu.uit.devorbit.mobile.repository

import vn.edu.uit.devorbit.mobile.model.CourseSummary
import vn.edu.uit.devorbit.mobile.model.RepoSummary
import vn.edu.uit.devorbit.mobile.network.NetworkModule

class DevOrbitRepository {
    private val api = NetworkModule.apiService

    suspend fun getCourses(): Result<List<CourseSummary>> = runCatching {
        api.getCourses()
    }

    suspend fun getRepos(courseId: Long): Result<List<RepoSummary>> = runCatching {
        api.getRepos(courseId)
    }
}
