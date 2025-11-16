package ar.edu.unlam.mobile.scaffolding.data.datasources.local

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject

class SessionManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val prefs = context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)

        fun saveLogin(email: String) {
            prefs.edit {
                putBoolean("is_logged_in", true)
                putString("email", email)
            }
        }

        fun saveToken(token: String) {
            prefs.edit {
                putString("auth_token", token)
            }
        }

        fun logout() {
            prefs.edit {
                clear()
            }
        }

        fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)
    }
