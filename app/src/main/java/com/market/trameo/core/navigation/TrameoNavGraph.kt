package com.market.trameo.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.market.trameo.features.admin.AdminScreen
import com.market.trameo.features.chat.ChatConversationScreen
import com.market.trameo.features.chat.ChatListScreen
import com.market.trameo.features.detalle.DetalleObjetoScreen
import com.market.trameo.features.forgotpassword.ForgotPassWordScreem
import com.market.trameo.features.home.HomeScreen
import com.market.trameo.features.intercambios.MisIntercambiosScreen
import com.market.trameo.features.login.LoginScreen
import com.market.trameo.features.mapa.MapaTruequesMockScreen
import com.market.trameo.features.misobjetos.MisObjetosScreen
import com.market.trameo.features.perfil.PerfilScreen
import com.market.trameo.features.proponerintercambio.ProponerIntercambioScreen
import com.market.trameo.features.publicar.PublicarScreen
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
    const val DETALLE_OBJETO = "detalle_objeto"
    const val CHAT_LIST = "chat_list"
    const val CHAT_CONVERSACION = "chat_conversacion"
    const val MIS_OBJETOS = "mis_objetos"
    const val TRUEQUES = "trueques"
    const val PERFIL = "perfil"
    const val PROPONER_INTERCAMBIO = "proponer_intercambio"
    const val PUBLICAR = "publicar"
    const val MAPA_TRUEQUES = "mapa_trueques"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val VERIFY_CODE = "verify_code"
    const val RESET_PASSWORD = "reset_password"
    const val ADMIN = "admin"

    fun detalleObjetoRoute(id: String): String = "$DETALLE_OBJETO/$id"
    fun proponerIntercambioRoute(id: Int): String = "$PROPONER_INTERCAMBIO/$id"
    fun chatConversacionRoute(chatId: Int): String = "$CHAT_CONVERSACION/$chatId"
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
            HomeScreen(
                onTruequesClick = {
                    navController.navigate(Routes.TRUEQUES) {
                        launchSingleTop = true
                    }
                },
                onPerfilClick = {
                    navController.navigate(Routes.PERFIL) {
                        launchSingleTop = true
                    }
                },
                onMisObjetosClick = {
                    navController.navigate(Routes.MIS_OBJETOS) {
                        launchSingleTop = true
                    }
                },
                onObjectClick = { id ->
                    navController.navigate(Routes.detalleObjetoRoute(id)) {
                        launchSingleTop = true
                    }
                },
                onPublicarClick = {
                    navController.navigate(Routes.PUBLICAR) {
                        launchSingleTop = true
                    }
                },
                onMapaClick = {
                    navController.navigate(Routes.MAPA_TRUEQUES) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.MAPA_TRUEQUES) {
            MapaTruequesMockScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.CHAT_LIST) {
            ChatListScreen(
                onBackClick = { navController.popBackStack() },
                onChatClick = { chatId ->
                    navController.navigate(Routes.chatConversacionRoute(chatId)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = "${Routes.CHAT_CONVERSACION}/{chatId}",
            arguments = listOf(navArgument("chatId") { type = NavType.IntType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getInt("chatId") ?: -1
            ChatConversationScreen(
                chatId = chatId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.DETALLE_OBJETO}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            DetalleObjetoScreen(
                objectId = id,
                onBackClick = { navController.popBackStack() },
                onProponerIntercambioClick = { objectId ->
                    navController.navigate(Routes.proponerIntercambioRoute(objectId.hashCode())) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = "${Routes.PROPONER_INTERCAMBIO}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: -1
            ProponerIntercambioScreen(
                objectId = id,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.TRUEQUES) {
            MisIntercambiosScreen(
                onHomeClick = {
                    navController.navigate(Routes.HOME) {
                        launchSingleTop = true
                    }
                },
                onPerfilClick = {
                    navController.navigate(Routes.PERFIL) {
                        launchSingleTop = true
                    }
                },
                onMisObjetosClick = {
                    navController.navigate(Routes.MIS_OBJETOS) {
                        launchSingleTop = true
                    }
                },
                onPublicarClick = {
                    navController.navigate(Routes.PUBLICAR) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.PERFIL) {
            PerfilScreen(
                onHomeClick = {
                    navController.navigate(Routes.HOME) {
                        launchSingleTop = true
                    }
                },
                onTruequesClick = {
                    navController.navigate(Routes.TRUEQUES) {
                        launchSingleTop = true
                    }
                },
                onMisObjetosClick = {
                    navController.navigate(Routes.MIS_OBJETOS) {
                        launchSingleTop = true
                    }
                },
                onPublicarClick = {
                    navController.navigate(Routes.PUBLICAR) {
                        launchSingleTop = true
                    }
                },
                onChatsClick = {
                    navController.navigate(Routes.CHAT_LIST) {
                        launchSingleTop = true
                    }
                },
                onAdminOptionsClick = {
                    navController.navigate(Routes.ADMIN) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.ADMIN) {
            AdminScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.MIS_OBJETOS) {
            MisObjetosScreen(
                onHomeClick = {
                    navController.navigate(Routes.HOME) {
                        launchSingleTop = true
                    }
                },
                onTruequesClick = {
                    navController.navigate(Routes.TRUEQUES) {
                        launchSingleTop = true
                    }
                },
                onPerfilClick = {
                    navController.navigate(Routes.PERFIL) {
                        launchSingleTop = true
                    }
                },
                onPublicarClick = {
                    navController.navigate(Routes.PUBLICAR) {
                        launchSingleTop = true
                    }
                },
                onObjectClick = { id ->
                    navController.navigate(Routes.detalleObjetoRoute(id)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.PUBLICAR) {
            PublicarScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // ── Pantalla Registro ───────────────────────────────
        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
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