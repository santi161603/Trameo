package com.market.trameo.data.repository

import com.market.trameo.domain.model.User
import com.market.trameo.domain.repository.UserRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class UserRepositoryImpl @Inject constructor() : UserRepository {

    private val userList = mutableListOf<User>()

    override suspend fun save(user: User): User = withContext(Dispatchers.IO) {
        val normalizedUser = user.copy(
            id = user.id.ifBlank { UUID.randomUUID().toString() },
            email = user.email.trim()
        )
        userList.add(normalizedUser)
        normalizedUser
    }

    override suspend fun login(email: String, password: String): User? = withContext(Dispatchers.IO) {
        userList.firstOrNull { user ->
            user.email.equals(email.trim(), ignoreCase = true) && user.password == password
        }
    }
}
