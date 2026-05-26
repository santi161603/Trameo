package com.market.trameo.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.toRoute
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.market.trameo.features.admin.AdminScreen
import com.market.trameo.features.chat.ChatConversationScreen
import com.market.trameo.features.chat.ChatListScreen
import com.market.trameo.features.detalle.DetalleObjetoScreen
import com.market.trameo.features.forgotpassword.ForgotPassWordScreem
import com.market.trameo.features.home.HomeScreen
import com.market.trameo.features.intercambios.MisIntercambiosScreen
import com.market.trameo.features.intercambios.detalle.IntercambioDetalleScreen
import com.market.trameo.features.login.LoginScreen
import com.market.trameo.features.mapa.MapaTruequesMockScreen
import com.market.trameo.features.misobjetos.MisObjetosScreen
import com.market.trameo.features.perfil.PerfilScreen
import com.market.trameo.features.proponerintercambio.PropuestaScreen
import com.market.trameo.features.publicar.PublicarScreen
import com.market.trameo.features.register.RegisterScreen
import com.market.trameo.features.resetpassword.ResetPasswordScreen
import com.market.trameo.features.splash.SplashScreen
import com.market.trameo.features.splash.SplashViewModel
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
        startDestination = SplashDestination
    ) {
        // ── Pantalla Splash ─────────────────────────────────
        composable<SplashDestination> {
            val splashViewModel: SplashViewModel = hiltViewModel()
            val hasSession by splashViewModel.hasSession.collectAsState()
            SplashScreen(
                onFinish = {
                    val destination = if (hasSession == true) HomeDestination else LoginDestination
                    navController.navigate(destination) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // ── Pantalla Login ──────────────────────────────────
        composable<LoginDestination> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(HomeDestination) {

                        // Evita volver a Login con el botón atrás después de autenticar.
                        popUpTo(LoginDestination) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegisterClick = {
                    navController.navigate(RegisterDestination)
                },
                onForgotPasswordClick = {
                    navController.navigate(ForgotPasswordDestination)
                }
            )
        }

        composable<HomeDestination> {
            HomeScreen(
                onTruequesClick = {
                    navController.navigate(TruequesDestination) {
                        launchSingleTop = true
                    }
                },
                onPerfilClick = {
                    navController.navigate(PerfilDestination) {
                        launchSingleTop = true
                    }
                },
                onMisObjetosClick = {
                    navController.navigate(MisObjetosDestination) {
                        launchSingleTop = true
                    }
                },
                onObjectClick = { id ->
                    navController.navigate(DetalleObjetoDestination(id)) {
                        launchSingleTop = true
                    }
                },
                onPublicarClick = {
                    navController.navigate(PublicarDestination) {
                        launchSingleTop = true
                    }
                },
                onMapaClick = {
                    navController.navigate(MapaTruequesDestination) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<MapaTruequesDestination> {
            MapaTruequesMockScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<ChatListDestination> {
            ChatListScreen(
                onBackClick = { navController.popBackStack() },
                onChatClick = { chatId ->
                    navController.navigate(ChatConversationDestination(chatId)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<ChatConversationDestination> { backStackEntry ->
            val args = backStackEntry.toRoute<ChatConversationDestination>()
            ChatConversationScreen(
                chatId = args.chatId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<DetalleObjetoDestination> { backStackEntry ->
            val args = backStackEntry.toRoute<DetalleObjetoDestination>()
            DetalleObjetoScreen(
                objectId = args.id,
                onBackClick = { navController.popBackStack() },
                onProponerIntercambioClick = { objectId, receptorUid ->
                    navController.navigate(ProponerIntercambioDestination(objectId, receptorUid)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<ProponerIntercambioDestination> { backStackEntry ->
            val args = backStackEntry.toRoute<ProponerIntercambioDestination>()
            PropuestaScreen(
                objetoDeseadoUid = args.objetoDeseadoUid,
                receptorUid = args.receptorUid,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<TruequesDestination> {
            MisIntercambiosScreen(
                onHomeClick = {
                    navController.navigate(HomeDestination) {
                        launchSingleTop = true
                    }
                },
                onPerfilClick = {
                    navController.navigate(PerfilDestination) {
                        launchSingleTop = true
                    }
                },
                onMisObjetosClick = {
                    navController.navigate(MisObjetosDestination) {
                        launchSingleTop = true
                    }
                },
                onPublicarClick = {
                    navController.navigate(PublicarDestination) {
                        launchSingleTop = true
                    }
                },
                onIntercambioClick = { id ->
                    navController.navigate(DetalleIntercambioDestination(id)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<DetalleIntercambioDestination> { backStackEntry ->
            val args = backStackEntry.toRoute<DetalleIntercambioDestination>()
            IntercambioDetalleScreen(
                intercambioId = args.intercambioId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<PerfilDestination> {
            PerfilScreen(
                onHomeClick = {
                    navController.navigate(HomeDestination) {
                        launchSingleTop = true
                    }
                },
                onTruequesClick = {
                    navController.navigate(TruequesDestination) {
                        launchSingleTop = true
                    }
                },
                onMisObjetosClick = {
                    navController.navigate(MisObjetosDestination) {
                        launchSingleTop = true
                    }
                },
                onPublicarClick = {
                    navController.navigate(PublicarDestination) {
                        launchSingleTop = true
                    }
                },
                onChatsClick = {
                    navController.navigate(ChatListDestination) {
                        launchSingleTop = true
                    }
                },
                onAdminOptionsClick = {
                    navController.navigate(AdminDestination) {
                        launchSingleTop = true
                    }
                },
                onLogoutSuccess = {
                    navController.navigate(LoginDestination) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<AdminDestination> {
            AdminScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<MisObjetosDestination> {
            MisObjetosScreen(
                onHomeClick = {
                    navController.navigate(HomeDestination) {
                        launchSingleTop = true
                    }
                },
                onTruequesClick = {
                    navController.navigate(TruequesDestination) {
                        launchSingleTop = true
                    }
                },
                onPerfilClick = {
                    navController.navigate(PerfilDestination) {
                        launchSingleTop = true
                    }
                },
                onPublicarClick = {
                    navController.navigate(PublicarDestination) {
                        launchSingleTop = true
                    }
                },
                onObjectClick = { id ->
                    navController.navigate(DetalleObjetoDestination(id)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<PublicarDestination> {
            PublicarScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // ── Pantalla Registro ───────────────────────────────
        composable<RegisterDestination> {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(LoginDestination) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // ── Pantalla Olvidaste tu contraseña ────────────────
        composable<ForgotPasswordDestination> {
            ForgotPassWordScreem(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCodeSent = {
                    navController.navigate(VerifyCodeDestination)
                }
            )
        }

        composable<VerifyCodeDestination> {
            VerifyCodeScreen(
                onCodeVerified = {
                    navController.navigate(ResetPasswordDestination)
                }
            )
        }

        composable<ResetPasswordDestination> {
            ResetPasswordScreen(
                onPasswordResetSuccess = {
                    navController.navigate(LoginDestination) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}