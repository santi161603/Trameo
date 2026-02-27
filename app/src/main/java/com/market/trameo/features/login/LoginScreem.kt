package com.market.trameo.features.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.market.trameo.R
import com.market.trameo.core.utils.RequestResult
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit = {},
    viewModel: LoginViewModel = viewModel()
) {
    // Estado para gestionar los snackbars
    val snackbarHostState = remember { SnackbarHostState() }

    // Observar estados del ViewModel
    val loginResult by viewModel.loginResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val focusManager = LocalFocusManager.current

    // Efecto para mostrar el snackbar cuando hay resultado
    LaunchedEffect(loginResult) {
        loginResult?.let { result ->
            // Obtener el mensaje según el resultado
            val message = when (result) {
                is RequestResult.Success -> result.message
                is RequestResult.Failure -> result.errorMessage
            }

            // Mostrar el snackbar con el mensaje
            snackbarHostState.showSnackbar(message)

            // Navegar a la pantalla principal si el login fue exitoso
            if (result is RequestResult.Success) {
                delay(1000) // Esperar 1 segundo para que el usuario vea el mensaje
                onLoginSuccess()
                viewModel.resetForm() // Resetear el formulario después del éxito
            }

            // Resetear el estado del loginResult en el ViewModel
            viewModel.resetLoginResult()
        }
    }

    // Scaffold con SnackbarHost
    Scaffold(
        snackbarHost = {
            // Mostrar el SnackbarHost para gestionar los snackbars
            SnackbarHost(snackbarHostState) { data ->
                val isError = loginResult is RequestResult.Failure
                // Mostrar el Snackbar con el estilo adecuado según si es error o éxito
                Snackbar(
                    containerColor = if (isError)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Text(data.visuals.message)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplicar padding del Scaffold
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                modifier = Modifier.size(300.dp),
                contentDescription = "Logo de la aplicacion Trameo",
                painter = painterResource(id = R.mipmap.ic_launcher_foreground)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Campo de email
            OutlinedTextField(
                value = viewModel.email.value,
                onValueChange = { viewModel.email.onChange(it) },
                label = { Text(text = "Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = viewModel.email.error != null,
                supportingText = viewModel.email.error?.let { error ->
                    { Text(text = error) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de contraseña
            OutlinedTextField(
                value = viewModel.password.value,
                onValueChange = { viewModel.password.onChange(it) },
                label = { Text(text = "Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = viewModel.password.error != null,
                supportingText = viewModel.password.error?.let { error ->
                    { Text(text = error) }
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (viewModel.isFormValid) {
                            viewModel.login()
                        }
                    }
                ),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "¿Olvidaste tu contraseña?",
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable(enabled = !isLoading) {
                        onForgotPasswordClick()
                    }
                    .padding(8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )


            Spacer(modifier = Modifier.height(10.dp))
            // Fila con los dos botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botón de login
                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = viewModel.isFormValid && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = "Iniciar sesión")
                    }
                }

                // Botón de registro
                Button(
                    onClick = onRegisterClick,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading
                ) {
                    Text(text = "Regresar")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onLoginSuccess = {},
        onRegisterClick = {}
    )
}