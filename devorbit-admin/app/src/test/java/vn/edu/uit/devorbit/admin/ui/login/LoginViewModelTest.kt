package vn.edu.uit.devorbit.admin.ui.login

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

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
    fun `initial state is not logged in`() {
        val repository = mock<AdminRepository>()
        val viewModel = LoginViewModel(repository)
        assertFalse(viewModel.state.value.isLoggedIn)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `login with blank username shows error`() {
        val repository = mock<AdminRepository>()
        val viewModel = LoginViewModel(repository)
        viewModel.updatePassword("pass123")
        viewModel.login()
        assertEquals("Vui lòng nhập đầy đủ thông tin", viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `login with blank password shows error`() {
        val repository = mock<AdminRepository>()
        val viewModel = LoginViewModel(repository)
        viewModel.updateUsername("admin")
        viewModel.login()
        assertEquals("Vui lòng nhập đầy đủ thông tin", viewModel.state.value.error)
    }

    @Test
    fun `login calls repository login method`() = runTest(testDispatcher) {
        val repository = mock<AdminRepository> {
            onBlocking { login("admin", "pass") } doReturn Result.success(
                vn.edu.uit.devorbit.admin.domain.repository.AdminAuthResult("token123")
            )
        }
        val viewModel = LoginViewModel(repository)
        viewModel.updateUsername("admin")
        viewModel.updatePassword("pass")
        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()
        verify(repository).login("admin", "pass")
    }

    @Test
    fun `login succeeds with valid credentials`() = runTest(testDispatcher) {
        val repository = mock<AdminRepository> {
            onBlocking { login("admin", "pass") } doReturn Result.success(
                vn.edu.uit.devorbit.admin.domain.repository.AdminAuthResult("token123")
            )
        }
        val viewModel = LoginViewModel(repository)
        viewModel.updateUsername("admin")
        viewModel.updatePassword("pass")
        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.isLoggedIn)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `login sets error on failure`() = runTest(testDispatcher) {
        val repository = mock<AdminRepository> {
            onBlocking { login("admin", "pass") } doReturn Result.failure(Exception("Sai thông tin"))
        }
        val viewModel = LoginViewModel(repository)
        viewModel.updateUsername("admin")
        viewModel.updatePassword("pass")
        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("Tên đăng nhập hoặc mật khẩu không đúng", viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `logout clears login state`() = runTest(testDispatcher) {
        val repository = mock<AdminRepository> {
            onBlocking { login("admin", "pass") } doReturn Result.success(
                vn.edu.uit.devorbit.admin.domain.repository.AdminAuthResult("token123")
            )
        }
        val viewModel = LoginViewModel(repository)
        viewModel.updateUsername("admin")
        viewModel.updatePassword("pass")
        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.isLoggedIn)

        viewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.state.value.isLoggedIn)
    }
}
