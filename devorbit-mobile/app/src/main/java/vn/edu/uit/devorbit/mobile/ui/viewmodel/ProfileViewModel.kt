package vn.edu.uit.devorbit.mobile.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.data.datastore.SettingsDataStore
import vn.edu.uit.devorbit.mobile.domain.repository.*
import javax.inject.Inject

data class ProfileUiState(
    val studentName: String = "",
    val studentCode: String = "",
    val avatar: String? = null,
    val email: String = "",
    val isLoggedIn: Boolean = false,
    val bookmarks: List<Bookmark> = emptyList(),
    val darkMode: Boolean = true,
    val isUploadingAvatar: Boolean = false,
    val avatarUploadSuccess: Boolean = false,
    val isUpdatingName: Boolean = false,
    val nameUpdateSuccess: Boolean = false,
    val isChangingPassword: Boolean = false,
    val passwordChangeSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsDataStore.studentName.collect { name ->
                _state.update { it.copy(studentName = name ?: "", isLoggedIn = !name.isNullOrBlank()) }
            }
        }
        viewModelScope.launch {
            settingsDataStore.studentCode.collect { code ->
                _state.update { it.copy(studentCode = code ?: "") }
            }
        }
        viewModelScope.launch {
            settingsDataStore.darkMode.collect { mode ->
                _state.update { it.copy(darkMode = mode) }
            }
        }
        viewModelScope.launch {
            bookmarkRepository.getAllBookmarks()
                .catch { emit(emptyList()) }
                .collect { bookmarks ->
                    _state.update { it.copy(bookmarks = bookmarks) }
                }
        }
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val result = authRepository.getProfile()
            result.onSuccess { profile ->
                _state.update { 
                    it.copy(
                        avatar = profile.avatar,
                        email = profile.email,
                        studentName = profile.fullName
                    ) 
                }
                settingsDataStore.saveStudentName(profile.fullName)
            }
        }
    }

    fun uploadAvatar(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(isUploadingAvatar = true, error = null) }
            val result = authRepository.uploadAvatar(uri)
            result.onSuccess { avatarUrl ->
                _state.update { 
                    it.copy(
                        avatar = avatarUrl,
                        isUploadingAvatar = false,
                        avatarUploadSuccess = true
                    ) 
                }
                kotlinx.coroutines.delay(2000)
                _state.update { it.copy(avatarUploadSuccess = false) }
            }
            result.onFailure {
                _state.update { it.copy(isUploadingAvatar = false, error = "Không thể cập nhật ảnh đại diện") }
            }
        }
    }

    fun updateFullName(fullName: String) {
        viewModelScope.launch {
            _state.update { it.copy(isUpdatingName = true, error = null) }
            val result = authRepository.updateFullName(fullName)
            result.onSuccess { profile ->
                _state.update { 
                    it.copy(
                        studentName = profile.fullName,
                        isUpdatingName = false,
                        nameUpdateSuccess = true
                    ) 
                }
                kotlinx.coroutines.delay(2000)
                _state.update { it.copy(nameUpdateSuccess = false) }
            }
            result.onFailure {
                _state.update { it.copy(isUpdatingName = false, error = "Không thể cập nhật tên") }
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _state.update { it.copy(isChangingPassword = true, error = null) }
            val result = authRepository.changePassword(currentPassword, newPassword)
            result.onSuccess {
                _state.update { 
                    it.copy(
                        isChangingPassword = false,
                        passwordChangeSuccess = true
                    ) 
                }
                kotlinx.coroutines.delay(2000)
                _state.update { it.copy(passwordChangeSuccess = false) }
            }
            result.onFailure {
                _state.update { it.copy(isChangingPassword = false, error = "Mật khẩu hiện tại không đúng") }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun removeBookmark(id: Long) {
        viewModelScope.launch { bookmarkRepository.removeBookmark(id) }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            settingsDataStore.setDarkMode(!_state.value.darkMode)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _state.update { it.copy(studentName = "", studentCode = "", avatar = null, isLoggedIn = false) }
        }
    }
}
