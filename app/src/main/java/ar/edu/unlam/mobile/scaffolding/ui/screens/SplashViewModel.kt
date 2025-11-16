package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel
    @Inject
    constructor(
        private val sessionManager: SessionManager,
    ) : ViewModel() {
        private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
        val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn

        init {
            viewModelScope.launch {
                delay(1500)
                _isLoggedIn.value = sessionManager.isLoggedIn()
            }
        }
    }
