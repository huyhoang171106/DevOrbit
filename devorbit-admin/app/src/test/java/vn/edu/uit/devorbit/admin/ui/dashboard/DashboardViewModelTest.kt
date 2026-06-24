package vn.edu.uit.devorbit.admin.ui.dashboard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import vn.edu.uit.devorbit.admin.data.remote.dto.AdminStatsResponse
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun mockRepository(
        statsResult: Result<AdminStatsResponse> = Result.success(AdminStatsResponse()),
    ): AdminRepository {
        return mock {
            onBlocking { getStats() } doReturn statsResult
            onBlocking { getPendingCandidates("all") } doReturn Result.success(emptyList())
            onBlocking { getRepoReviews() } doReturn Result.success(emptyList())
            onBlocking { getCommunityMessages() } doReturn Result.success(emptyList())
            onBlocking { getScanLogs() } doReturn Result.success(emptyList())
            onBlocking { getStudents() } doReturn Result.success(emptyList())
            onBlocking { getUnreadCount() } doReturn Result.success(0L)
        }
    }

    @Test
    fun `init loads stats on success`() = runTest(testDispatcher) {
        val stats = AdminStatsResponse(
            totalStudents = 100,
            totalCourses = 10,
            totalRepos = 50,
            pendingCandidates = 5,
        )
        val repository = mockRepository(statsResult = Result.success(stats))
        val viewModel = DashboardViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val status = viewModel.state.value.status
        assertTrue("Expected Success status, got $status", status is DashboardStatus.Success)
        val data = (status as DashboardStatus.Success).data
        assertEquals(100L, data.totalStudents)
        assertEquals(10L, data.totalCourses)
        assertEquals(50L, data.totalRepos)
        assertEquals(5L, data.pendingCandidatesCount)
    }

    @Test
    fun `loadStats sets error on failure`() = runTest(testDispatcher) {
        val repository = mockRepository(
            statsResult = Result.failure(Exception("Network error")),
        )
        val viewModel = DashboardViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val status = viewModel.state.value.status
        assertTrue("Expected Error status, got $status", status is DashboardStatus.Error)
        assertEquals("Network error", (status as DashboardStatus.Error).message)
    }

    @Test
    fun `loadStats completes with success status`() = runTest(testDispatcher) {
        val repository = mockRepository()
        val viewModel = DashboardViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val status = viewModel.state.value.status
        assertTrue("Expected Success status, got $status", status is DashboardStatus.Success)
    }
}
