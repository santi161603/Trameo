package com.market.trameo.features.forgotpassword

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.market.trameo.R
import com.market.trameo.core.utils.RequestResult
import com.market.trameo.core.theme.TrameoTheme

@Composable
fun ForgotPassWordScreem(
    onNavigateBack: () -> Unit = {},
    onCodeSent: () -> Unit = {},
    viewModel: ForgotPasswordViewModel = hiltViewModel()
){// Estado para gestionar los snackbars
val snackbarHostState = remember { SnackbarHostState() }
// Observar el estado de recoveryResult
val recoveryResult by viewModel.recoveryResult.collectAsState()

// Efecto para mostrar el snackbar cuando hay resultado
LaunchedEffect(recoveryResult) {
    recoveryResult?.let { result ->
        val message = when (result) {
            is RequestResult.Success -> result.message
            is RequestResult.Failure -> result.errorMessage
        }
        snackbarHostState.showSnackbar(message)

        // Si fue exitoso, ir al paso de ingresar código.
        if (result is RequestResult.Success) {
            delay(700)
            onCodeSent()
        }

        // Resetear el resultado después de mostrarlo
        viewModel.resetRecoveryResult()
    }
}

// Scaffold con SnackbarHost
Scaffold(
snackbarHost = {
    SnackbarHost(snackbarHostState) { data ->
        val isError = recoveryResult is RequestResult.Failure
        Snackbar(
            containerColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
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
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // Título: "Recuperar contraseña"
        Text(
            text = stringResource(id = R.string.forgot_password_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Card principal
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Instrucciones
                Text(
                    text = stringResource(id = R.string.forgot_password_description),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de correo electrónico con validación desde el ViewModel
                OutlinedTextField(
                    value = viewModel.email.value, // Estado desde el ViewModel
                    onValueChange = { viewModel.email.onChange(it) }, // Actualiza el estado en el ViewModel
                    label = { Text(stringResource(id = R.string.forgot_password_email_label)) },
                    placeholder = { Text(stringResource(id = R.string.forgot_password_email_label)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = stringResource(id = R.string.forgot_password_email_icon)
                        )
                    },
                    isError = viewModel.email.error != null, // Borde rojo si hay error
                    supportingText = viewModel.email.error?.let { error ->
                        { Text(text = error) } // Mensaje de error debajo del campo
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botón "Enviar enlace" - habilitado solo si el formulario es válido
                Button(
                    onClick = { viewModel.sendRecoveryEmail() }, // Llama la función del ViewModel
                    enabled = viewModel.isFormValid, // Solo habilitado si el email es válido
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = stringResource(id = R.string.forgot_password_send_icon),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(id = R.string.forgot_password_send_button))
                }
            }
        }
    }
}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPassWordScreemPreview() {
    TrameoTheme {
        ForgotPassWordScreem()
    }
}
