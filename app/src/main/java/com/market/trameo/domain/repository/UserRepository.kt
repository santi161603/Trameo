package com.market.trameo.domain.repository

import com.market.trameo.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val users: StateFlow<List<User>>
    suspend fun save(user: User): User
    suspend fun login(email: String, password: String): User?
    fun findById(userId: String): User?
}
