package vn.edu.uit.devorbit.mobile.ui.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import vn.edu.uit.devorbit.mobile.data.datastore.SettingsDataStore
import vn.edu.uit.devorbit.mobile.network.ApiService

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelInitializationTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GroupPlanViewModel init creates week days without crashing`() = runTest(dispatcher) {
        val apiService = mock<ApiService>()
        val settingsDataStore = mock<SettingsDataStore>()
        whenever(settingsDataStore.studentCode).thenReturn(flowOf(null))

        val viewModel = GroupPlanViewModel(apiService, settingsDataStore)
        advanceUntilIdle()

        assertEquals(7, viewModel.detail.value.weekDates.size)
    }

    @Test
    fun `TaskManagementViewModel init creates week days without crashing`() = runTest(dispatcher) {
        val apiService = mock<ApiService>()
        whenever(apiService.getPersonalTasks(any())).thenReturn(emptyList())
        whenever(apiService.getMyGroupPlans()).thenReturn(emptyList())

        val viewModel = TaskManagementViewModel(apiService)
        advanceUntilIdle()

        assertEquals(7, viewModel.state.value.weekDates.size)
    }
}
