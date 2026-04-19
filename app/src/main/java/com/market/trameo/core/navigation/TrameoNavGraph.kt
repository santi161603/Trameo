package com.trameo.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.trameo.features.login.ForgotPassWordScreem
import com.trameo.features.login.LoginScreen
import com.trameo.features.register.RegisterScreen
import com.trameo.features.home.SplashScreen

/**
 * Rutas de navegación de la app Trameo.
 */
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
}

/**
 * Grafo de navegación principal.
 *
 * Flujo:
 *  Splash ──► Login
 *  Login  ──► Register
 *         ──► ForgotPassword
 *  Register ──► Login
 */
@Composable
fun TrameoNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        // ── Pantalla Splash ─────────────────────────────────
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinish = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // ── Pantalla Login ──────────────────────────────────
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    // Login es la pantalla raíz actual. Se deja el flujo autenticado para futura pantalla.
                },
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
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
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackClick = {
                    navController.popBackStack()
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
