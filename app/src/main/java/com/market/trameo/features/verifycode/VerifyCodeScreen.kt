package com.market.trameo.features.verifycode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.market.trameo.core.utils.RequestResult
import kotlinx.coroutines.delay

@Composable
fun VerifyCodeScreen(
    onCodeVerified: () -> Unit,
    viewModel: VerifyCodeViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val verifyResult by viewModel.verifyResult.collectAsState()

    LaunchedEffect(verifyResult) {
        verifyResult?.let { result ->
            val message = when (result) {
                is RequestResult.Success -> result.message
                is RequestResult.Failure -> result.errorMessage
            }
            snackbarHostState.showSnackbar(message)
            if (result is RequestResult.Success) {
                delay(500)
                onCodeVerified()
            }
            viewModel.resetVerifyResult()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Ingresar código",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Código quemado para pruebas: 123456",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = viewModel.code.value,
                onValueChange = { viewModel.code.onChange(it) },
                label = { Text("Código") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = viewModel.code.error != null,
                supportingText = viewModel.code.error?.let { error -> { Text(error) } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.verifyCode() },
                enabled = viewModel.isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Validar código")
            }
        }
    }
}

