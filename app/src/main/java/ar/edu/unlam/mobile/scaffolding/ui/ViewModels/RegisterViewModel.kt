package ar.edu.unlam.mobile.scaffolding.ui.ViewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {

    // Estado de los campos de texto
    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _mail = MutableLiveData<String>()
    val mail: LiveData<String> get() = _mail

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    private val _confirmPassword = MutableLiveData<String>()
    val confirmPassword: LiveData<String> get() = _confirmPassword

    private val _userAvatar = MutableLiveData<String?>()
    val userAvatar: LiveData<String?> get() = _userAvatar

    // Estado de error (opcional para mostrar mensajes)
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // Estado de éxito o estado de validación
    private val _isRegistrationSuccessful = MutableLiveData<Boolean>()
    val isRegistrationSuccessful: LiveData<Boolean> get() = _isRegistrationSuccessful

    // Función para actualizar el estado de cada campo
    fun onUsernameChanged(newUsername: String) {
        _username.value = newUsername
    }

    fun onMailChanged(newMail: String) {
        _mail.value = newMail
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
    }

    fun onConfirmPasswordChanged(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
    }

    fun onUserAvatarChanged(newUserAvatar: String?) {
        _userAvatar.value = newUserAvatar
    }

    // Lógica para el registro de usuario
    fun registerUser(context: Context) {
        val usernameValue = _username.value
        val mailValue = _mail.value
        val passwordValue = _password.value
        val confirmPasswordValue = _confirmPassword.value

        if (usernameValue.isNullOrEmpty() || mailValue.isNullOrEmpty() || passwordValue.isNullOrEmpty()) {
            _error.value = "Todos los campos son obligatorios"
            return
        }
        if (passwordValue != confirmPasswordValue) {
            _error.value = "Las contraseñas no coinciden"
            return
        }

        // Guardar en SharedPreferences
        val prefs = context.getSharedPreferences("users", Context.MODE_PRIVATE)
        prefs.edit().putString(mailValue, passwordValue).apply()

        _isRegistrationSuccessful.value = true
        _error.value = null
    }

}