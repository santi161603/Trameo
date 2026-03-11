package com.market.trameo.features.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.market.trameo.core.utils.RequestResult
import com.market.trameo.ui.theme.TrameoTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    // Estado para gestionar los snackbars
    val snackbarHostState = remember { SnackbarHostState() }

    // Observar estados del ViewModel
    val registerResult by viewModel.registerResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Estados de visibilidad de contraseñas
    var passwordVisible by remember { mutableStateOf(false) }
    var repetirPasswordVisible by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Efecto para mostrar el snackbar cuando hay resultado
    LaunchedEffect(registerResult) {
        registerResult?.let { result ->
            val message = when (result) {
                is RequestResult.Success -> result.message
                is RequestResult.Failure -> result.errorMessage
            }

            snackbarHostState.showSnackbar(message)

            if (result is RequestResult.Success) {
                delay(1500)
                onRegisterSuccess()
                viewModel.resetForm()
            }

            viewModel.resetRegisterResult()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                val isError = registerResult is RequestResult.Failure
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
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "Registro",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
            )

            // Campo Nombre
            OutlinedTextField(
                value = viewModel.nombre.value,
                onValueChange = { viewModel.nombre.onChange(it) },
                label = { Text("Nombre") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = true,
                isError = viewModel.nombre.error != null,
                supportingText = viewModel.nombre.error?.let { error ->
                    { Text(text = error) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = !isLoading
            )

            // Campo Apellidos
            OutlinedTextField(
                value = viewModel.apellidos.value,
                onValueChange = { viewModel.apellidos.onChange(it) },
                label = { Text("Apellidos") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = true,
                isError = viewModel.apellidos.error != null,
                supportingText = viewModel.apellidos.error?.let { error ->
                    { Text(text = error) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = !isLoading
            )

            // Campo Cédula
            OutlinedTextField(
                value = viewModel.cedula.value,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        viewModel.cedula.onChange(it)
                    }
                },
                label = { Text("Cédula") },
                leadingIcon = {
                    Icon(Icons.Default.Badge, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = true,
                isError = viewModel.cedula.error != null,
                supportingText = viewModel.cedula.error?.let { error ->
                    { Text(text = error) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = !isLoading
            )

            // Campo Correo
            OutlinedTextField(
                value = viewModel.correo.value,
                onValueChange = { viewModel.correo.onChange(it) },
                label = { Text("Correo") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = true,
                isError = viewModel.correo.error != null,
                supportingText = viewModel.correo.error?.let { error ->
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

            // Campo Contraseña
            OutlinedTextField(
                value = viewModel.password.value,
                onValueChange = {
                    viewModel.password.onChange(it)
                    viewModel.validatePasswordMatch() // Revalidar coincidencia
                },
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = true,
                isError = viewModel.password.error != null,
                supportingText = viewModel.password.error?.let { error ->
                    { Text(text = error) }
                },
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = !isLoading
            )

            // Campo Repetir Contraseña
            OutlinedTextField(
                value = viewModel.repetirPassword.value,
                onValueChange = { viewModel.repetirPassword.onChange(it) },
                label = { Text("Repetir Contraseña") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { repetirPasswordVisible = !repetirPasswordVisible }) {
                        Icon(
                            imageVector = if (repetirPasswordVisible)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = true,
                isError = viewModel.repetirPassword.error != null,
                supportingText = viewModel.repetirPassword.error?.let { error ->
                    { Text(text = error) }
                },
                visualTransformation = if (repetirPasswordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = !isLoading
            )

            // Campo Celular
            OutlinedTextField(
                value = viewModel.celular.value,
                onValueChange = {
                    if (it.all { char -> char.isDigit() || char == '+' || char == ' ' }) {
                        viewModel.celular.onChange(it)
                    }
                },
                label = { Text("Celular") },
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = true,
                isError = viewModel.celular.error != null,
                supportingText = viewModel.celular.error?.let { error ->
                    { Text(text = error) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                enabled = !isLoading
            )

            // Campo Fecha de Nacimiento
            OutlinedTextField(
                value = viewModel.fechaNacimiento.value,
                onValueChange = { },
                label = { Text("Fecha de Nacimiento") },
                leadingIcon = {
                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                readOnly = true,
                singleLine = true,
                isError = viewModel.fechaNacimiento.error != null,
                supportingText = viewModel.fechaNacimiento.error?.let { error ->
                    { Text(text = error) }
                },
                trailingIcon = {
                    IconButton(
                        onClick = { if (!isLoading) showDatePicker = true }
                    ) {
                        Icon(Icons.Default.Event, contentDescription = null)
                    }
                }
            )

            // Botón de Registro
            Button(
                onClick = { viewModel.register() },
                modifier = Modifier
                    .width(200.dp)
                    .padding(vertical = 16.dp),
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
                    Text("Registrar")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // DatePicker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { selectedDate ->
                viewModel.fechaNacimiento.onChange(selectedDate)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Date(millis)
                        val formattedDate = dateFormatter.format(date)
                        onDateSelected(formattedDate)
                    }
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    TrameoTheme {
        RegisterScreen(onRegisterSuccess = {})
    }
}