package vn.edu.uit.devorbit.mobile.ui.viewmodel

object AuthStateTransitions {
    fun registerStateAfterSwitchToLogin(state: RegisterUiState): RegisterUiState {
        return state.copy(error = null, message = null)
    }
}
