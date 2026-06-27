package vn.edu.uit.devorbit.admin.ui.dashboard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

    @Test
    fun `init loads stats on success`() = runTest(testDispatcher) {
        val stats = AdminStatsResponse(
            totalStudents = 100,
            totalCourses = 10,
            totalRepos = 50,
            pendingCandidates = 5
        )
        val repository = mock<AdminRepository> {
            onBlocking { getStats() } doReturn Result.success(stats)
        }
        val viewModel = DashboardViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(stats, viewModel.state.value.stats)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `loadStats sets error on failure`() = runTest(testDispatcher) {
        val repository = mock<AdminRepository> {
            onBlocking { getStats() } doReturn Result.failure(Exception("Network error"))
        }
        val viewModel = DashboardViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("Network error", viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `loadStats completes loading`() = runTest(testDispatcher) {
        val repository = mock<AdminRepository> {
            onBlocking { getStats() } doReturn Result.success(AdminStatsResponse())
        }
        val viewModel = DashboardViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.state.value.isLoading)
    }
}
