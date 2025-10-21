package ar.edu.unlam.mobile.scaffolding.ui.ViewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isLoginSuccessful = MutableLiveData<Boolean>()
    val isLoginSuccessful: LiveData<Boolean> get() = _isLoginSuccessful

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
    }

    // Login real usando SharedPreferences
    fun loginUser(context: Context) {
        val emailValueClean = _email.value?.trim()?.lowercase()
        val passwordValue = _password.value

        if (emailValueClean.isNullOrEmpty() || passwordValue.isNullOrEmpty()) {
            _error.value = "Ambos campos son obligatorios"
            _isLoginSuccessful.value = false
            return
        }

        val prefs = context.getSharedPreferences("users", Context.MODE_PRIVATE)
        val savedPassword = prefs.getString(emailValueClean, null)

        if (savedPassword == passwordValue) {
            _isLoginSuccessful.value = true
            _error.value = null
        } else {
            _isLoginSuccessful.value = false
            _error.value = "Email o contraseña incorrectos"
        }
    }
}
