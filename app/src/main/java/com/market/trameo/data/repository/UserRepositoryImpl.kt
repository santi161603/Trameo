package com.market.trameo.data.repository

import com.market.trameo.data.model.UserLocal
import com.market.trameo.domain.model.User
import com.market.trameo.domain.model.UserRole
import com.market.trameo.domain.repository.UserRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

@Singleton
class UserRepositoryImpl @Inject constructor() : UserRepository {

    private companion object {
        const val ADMIN_EMAIL = "santiagoaceroospina@gmail.com"
    }

    private val userList = mutableListOf(
        UserLocal(
            id = "seed-user-1",
            name = "Santiago Acero",
            email = "santiagoaceroospina@gmail.com",
            password = "123456",
            city = "Bogota",
            address = "Calle 123 #45-67",
            phoneNumber = "3001234567",
            profilePhotoUri = "https://picsum.photos/seed/trameo-profile-seed/200/200",
            role = UserRole.ADMIN
        )
    )

    private val _users = MutableStateFlow(userList.map { it.toDomain() })
    override val users: StateFlow<List<User>> = _users.asStateFlow()

    override suspend fun save(user: User): User = withContext(Dispatchers.IO) {
        val normalizedUser = user.copy(
            id = user.id.ifBlank { UUID.randomUUID().toString() },
            email = user.email.trim(),
            role = if (user.email.trim().equals(ADMIN_EMAIL, ignoreCase = true)) UserRole.ADMIN else user.role
        )
        userList.add(normalizedUser.toLocal())
        _users.value = userList.map { it.toDomain() }
        normalizedUser
    }

    override suspend fun login(email: String, password: String): User? = withContext(Dispatchers.IO) {
        userList
            .firstOrNull { user ->
                user.email.equals(email.trim(), ignoreCase = true) && user.password == password
            }
            ?.toDomain()
    }

    override fun findById(userId: String): User? {
        return userList.firstOrNull { it.id == userId }?.toDomain()
    }

    private fun UserLocal.toDomain(): User {
        return User(
            id = id,
            name = name,
            email = email,
            password = password,
            city = city,
            address = address,
            phoneNumber = phoneNumber,
            profilePhotoUri = profilePhotoUri,
            role = role
        )
    }

    private fun User.toLocal(): UserLocal {
        return UserLocal(
            id = id,
            name = name,
            email = email,
            password = password,
            city = city,
            address = address,
            phoneNumber = phoneNumber,
            profilePhotoUri = profilePhotoUri,
            role = role
        )
    }
}
