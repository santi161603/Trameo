package com.market.trameo.features.chat

data class ChatContact(
    val id: Int,
    val name: String,
    val tradeTitle: String,
    val avatarUrl: String,
    val lastMessage: String,
    val lastTime: String,
    val unreadCount: Int
)

data class ChatMessage(
    val id: Int,
    val text: String,
    val time: String,
    val isMine: Boolean
)

object ChatData {
    val contacts: List<ChatContact> = listOf(
        ChatContact(
            id = 1,
            name = "Laura M.",
            tradeTitle = "Bicicleta por guitarra",
            avatarUrl = "https://picsum.photos/seed/chat-laura/200/200",
            lastMessage = "Te parece bien encontrarnos manana?",
            lastTime = "10:42",
            unreadCount = 2
        ),
        ChatContact(
            id = 2,
            name = "Andres C.",
            tradeTitle = "Licuadora por cafetera",
            avatarUrl = "https://picsum.photos/seed/chat-andres/200/200",
            lastMessage = "Listo, te envio fotos del estado.",
            lastTime = "Ayer",
            unreadCount = 0
        ),
        ChatContact(
            id = 3,
            name = "Paula R.",
            tradeTitle = "Libros por audifonos",
            avatarUrl = "https://picsum.photos/seed/chat-paula/200/200",
            lastMessage = "Gracias! Confirmo en la tarde.",
            lastTime = "Lun",
            unreadCount = 1
        )
    )

    fun contactById(id: Int): ChatContact? = contacts.firstOrNull { it.id == id }

    fun messagesByChatId(chatId: Int): List<ChatMessage> = when (chatId) {
        1 -> listOf(
            ChatMessage(1, "Hola! Aun tienes la guitarra?", "09:58", true),
            ChatMessage(2, "Hola, si la tengo disponible.", "10:01", false),
            ChatMessage(3, "Super, te interesa la bici urbana?", "10:04", true),
            ChatMessage(4, "Si, me gusta. Puedes enviar mas fotos?", "10:10", false),
            ChatMessage(5, "Claro, te las mando ahora.", "10:12", true)
        )

        2 -> listOf(
            ChatMessage(1, "La cafetera funciona perfecto.", "18:22", false),
            ChatMessage(2, "Genial, la licuadora tambien esta en buen estado.", "18:25", true),
            ChatMessage(3, "Hacemos el trueque el viernes?", "18:30", false)
        )

        else -> listOf(
            ChatMessage(1, "Hola Paula, aun te interesa?", "15:09", true),
            ChatMessage(2, "Si, me interesa bastante.", "15:12", false),
            ChatMessage(3, "Perfecto, coordinamos por aqui.", "15:13", true)
        )
    }
}
