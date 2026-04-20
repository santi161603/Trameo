package com.market.trameo.features.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.market.trameo.R
import com.market.trameo.core.utils.RequestResult
import com.market.trameo.core.theme.GrisPizarra
import com.market.trameo.core.theme.Marfil
import com.market.trameo.core.theme.Terracota
import com.market.trameo.core.theme.TrameoTheme

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val loginResult by viewModel.loginResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(loginResult) {
        loginResult?.let { result ->
            val message = when (result) {
                is RequestResult.Success -> result.message
                is RequestResult.Failure -> result.errorMessage
            }
            snackbarHostState.showSnackbar(message)
            if (result is RequestResult.Success) {
                onLoginSuccess()
                viewModel.resetForm()
            }
            viewModel.resetLoginResult()
        }
    }

    Scaffold(
        containerColor = Marfil,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                val isError = loginResult is RequestResult.Failure
                Snackbar(
                    containerColor = if (isError)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(data.visuals.message)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Marfil)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cabecera superior con estilo del mock: bloque terracota + circulo blanco.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(Terracota),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(146.dp)
                        .background(color = androidx.compose.ui.graphics.Color.White, shape = RoundedCornerShape(73.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.size(94.dp),
                        contentDescription = "Logo de Trameo",
                        painter = painterResource(id = R.mipmap.ic_launcher_foreground)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = viewModel.email.value,
                onValueChange = { viewModel.email.onChange(it) },
                label = { Text(text = "Correo electrónico") },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Email, contentDescription = null, tint = Terracota)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
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
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Terracota,
                    focusedLabelColor = Terracota,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.password.value,
                onValueChange = { viewModel.password.onChange(it) },
                label = { Text(text = "Contraseña") },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Lock, contentDescription = null, tint = Terracota)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = null,
                            tint = GrisPizarra.copy(alpha = 0.7f)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                isError = viewModel.password.error != null,
                supportingText = viewModel.password.error?.let { error ->
                    { Text(text = error) }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Terracota,
                    focusedLabelColor = Terracota,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "¿Olvidaste tu contraseña?",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clickable(enabled = !isLoading) {
                        onForgotPasswordClick()
                    }
                    .padding(8.dp),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = Terracota,
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.login() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = viewModel.isFormValid && !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Terracota)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Iniciar sesión", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                Text(
                    text = "o",
                    color = GrisPizarra,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Terracota),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.8f))
            ) {
                Text(text = "Crear cuenta", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TrameoTheme {
        LoginScreen(
            onLoginSuccess = {},
            onRegisterClick = {}
        )
    }
}
