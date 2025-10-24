package ar.edu.unlam.mobile.scaffolding.ui.screens.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.unlam.mobile.scaffolding.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    navController: NavController,
) {
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsState()

    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Regístrate",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = uiState.usernameTextField,
            onValueChange = { viewModel.onUsernameChange(it) },
            label = { Text("Nombre completo") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.emailTextField,
            onValueChange = { viewModel.onEmailChange(it) },
            label = { Text("Email") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.passwordTextField,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Contraseña") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                val image =
                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = image,
                        contentDescription =
                            if (passwordVisible) {
                                "Ocultar contraseña"
                            } else {
                                "Mostrar contraseña"
                            },
                    )
                }
            },
            visualTransformation =
                if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.confirmPasswordTextField,
            onValueChange = { viewModel.onConfirmPasswordChange(it) },
            label = { Text("Confirmar contraseña") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                val image =
                    if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = image,
                        contentDescription =
                            if (confirmPasswordVisible) {
                                "Ocultar contraseña"
                            } else {
                                "Mostrar contraseña"
                            },
                    )
                }
            },
            visualTransformation =
                if (confirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        )

        uiState.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = Color.Red,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = "Crear cuenta",
            width = 320.dp,
            onClick = {
                viewModel.onRegister(
                    email = uiState.emailTextField,
                    password = uiState.passwordTextField,
                    confirmPassword = uiState.confirmPasswordTextField,
                )
            },
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row {
            Text(
                text = "¿Ya tenés cuenta? ",
                color = Color.Gray,
                fontSize = 14.sp,
            )
            Text(
                text = "Iniciar sesión",
                color = Color(0xFF2E7D32),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier =
                    Modifier.clickable {
                        navController.navigate("login")
                    },
            )
        }
    }
}
