package com.market.trameo.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.market.trameo.features.forgotpassword.ForgotPassWordScreem
import com.market.trameo.features.home.HomeScreen
import com.market.trameo.features.login.LoginScreen
import com.market.trameo.features.register.RegisterScreen

/**
 * Rutas de navegación de la app Trameo.
 */
object Routes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
}

/**
 * Grafo de navegación principal.
 *
 * Flujo:
 *  Home  ──► Login  ──► ForgotPassword
 *        ──► Register
 *  Login ──► Regresar (vuelve a Home)
 *  Login ──► ¿Olvidaste tu contraseña? (va a ForgotPassword)
 */
@Composable
fun TrameoNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        // ── Pantalla Home (inicio) ──────────────────────────
        composable(Routes.HOME) {
            HomeScreen(
                onLoginClick = {
                    navController.navigate(Routes.LOGIN)
                },
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        // ── Pantalla Login ──────────────────────────────────
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    // Después de login exitoso, ir a Home limpiando el back-stack
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    // Botón "Regresar" → vuelve a Home
                    navController.popBackStack()
                },
                onForgotPasswordClick = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                }
            )
        }

        // ── Pantalla Registro ───────────────────────────────
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    // Después de registro exitoso, volver a Home
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        // ── Pantalla Olvidaste tu contraseña ────────────────
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPassWordScreem(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
