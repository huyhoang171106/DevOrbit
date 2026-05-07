package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.model.AiResponse
import vn.edu.uit.devorbit.mobile.repository.DevOrbitRepository
import javax.inject.Inject

@HiltViewModel
class AiTutorViewModel @Inject constructor(
    private val repository: DevOrbitRepository
) : ViewModel() {

    private val _summary = mutableStateOf<AiResponse?>(null)
    val summary: State<AiResponse?> = _summary

    private val _advice = mutableStateOf<AiResponse?>(null)
    val advice: State<AiResponse?> = _advice

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun loadAiInsights(repoId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _summary.value = null
            _advice.value = null
            
            repository.getAiSummary(repoId).onSuccess { _summary.value = it }
            repository.getAiAdvice(repoId).onSuccess { _advice.value = it }
            
            _isLoading.value = false
        }
    }
}
