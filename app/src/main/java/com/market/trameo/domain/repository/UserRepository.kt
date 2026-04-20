package com.market.trameo.domain.repository

import com.market.trameo.domain.model.User

interface UserRepository {
    suspend fun save(user: User): User
    suspend fun login(email: String, password: String): User?
}
