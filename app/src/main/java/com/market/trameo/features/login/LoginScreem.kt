package com.market.trameo.features.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.market.trameo.R



@Composable
fun LoginScreen(
    onLoginClick: (email: String, password: String) -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Image(
            modifier = Modifier.size(300.dp),
            contentDescription = "Logo de la aplicacion Trameo",
            painter = painterResource(id = R.mipmap.ic_launcher)
        )


        Spacer(modifier = Modifier.height(20.dp))

        // Campo de email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = {
                //Text(stringResource(R.string.correo_electr_nico))
                Text(text = "Correo electrónico")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
            )
        )

        // Espaciador entre campos (equivalente a marginTop="30dp")
        Spacer(modifier = Modifier.height(30.dp))

        // Campo de contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(text = "Contraseña")
              //  Text(stringResource(R.string.contrase_a))
                    },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Password
            )
        )

        // Espaciador antes de botones (equivalente a marginTop="40dp")
        Spacer(modifier = Modifier.height(40.dp))

        // Fila con los dos botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Botón de login
            Button(
                onClick = { onLoginClick(email, password) },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                shape = RoundedCornerShape(8.dp) // Estilo redondeado
            ) {
                Text(text = "Iniciar sesión")
            // Text(stringResource(R.string.iniciar_sesi_n))
            }

            // Botón de registro
            Button(
                onClick = onRegisterClick,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                shape = RoundedCornerShape(8.dp) // Estilo redondeado
            ) {
                Text(text = "Registrarse")
              //  Text(stringResource(R.string.registro))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLoginClick = { _, _ -> }, onRegisterClick = {})
}