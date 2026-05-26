package com.market.trameo.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object SplashDestination

@Serializable
object LoginDestination

@Serializable
object HomeDestination

@Serializable
object ChatListDestination

@Serializable
data class ChatConversationDestination(val chatId: Int)

@Serializable
data class DetalleObjetoDestination(val id: String)

@Serializable
data class ProponerIntercambioDestination(val objetoDeseadoUid: String, val receptorUid: String)

@Serializable
object TruequesDestination

@Serializable
data class DetalleIntercambioDestination(val intercambioId: String)

@Serializable
object PerfilDestination

@Serializable
object AdminDestination

@Serializable
object MisObjetosDestination

@Serializable
object PublicarDestination

@Serializable
object MapaTruequesDestination

@Serializable
object RegisterDestination

@Serializable
object ForgotPasswordDestination

@Serializable
object VerifyCodeDestination

@Serializable
object ResetPasswordDestination
