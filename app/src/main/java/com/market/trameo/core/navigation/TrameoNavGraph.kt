package com.market.trameo.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.market.trameo.features.forgotpassword.ForgotPassWordScreem
import com.market.trameo.features.home.HomeScreen
import com.market.trameo.features.login.LoginScreen
import com.market.trameo.features.register.RegisterScreen
import com.market.trameo.features.resetpassword.ResetPasswordScreen
import com.market.trameo.features.splash.SplashScreen
import com.market.trameo.features.verifycode.VerifyCodeScreen

/**
 * Rutas de navegación de la app Trameo.
 */
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val HOME = "home"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val VERIFY_CODE = "verify_code"
    const val RESET_PASSWORD = "reset_password"
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
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                },
                onForgotPasswordClick = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen()
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
                },
                onCodeSent = {
                    navController.navigate(Routes.VERIFY_CODE)
                }
            )
        }

        composable(Routes.VERIFY_CODE) {
            VerifyCodeScreen(
                onCodeVerified = {
                    navController.navigate(Routes.RESET_PASSWORD)
                }
            )
        }

        composable(Routes.RESET_PASSWORD) {
            ResetPasswordScreen(
                onPasswordResetSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.FORGOT_PASSWORD) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}