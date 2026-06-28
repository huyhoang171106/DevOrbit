package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import vn.edu.uit.devorbit.mobile.domain.repository.AuthRepository

abstract class BaseViewModel(
    protected val authRepository: AuthRepository
) : ViewModel() {

    protected fun handleUnauthorized() {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            authRepository.clearToken()
        }
    }

    protected suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> = runCatching {
        block()
    }.onFailure { e ->
        if (e is HttpException && e.code() == 401) {
            handleUnauthorized()
        }
    }
}
