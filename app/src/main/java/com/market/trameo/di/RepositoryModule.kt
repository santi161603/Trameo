package com.market.trameo.di

import com.market.trameo.data.repository.HomeRepositoryImpl
import com.market.trameo.data.repository.PropuestaRepositoryImpl
import com.market.trameo.data.repository.SwapRepositoryImpl
import com.market.trameo.data.repository.UserRepositoryImpl
import com.market.trameo.domain.repository.HomeRepository
import com.market.trameo.domain.repository.PropuestaRepository
import com.market.trameo.domain.repository.SwapRepository
import com.market.trameo.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindSwapRepository(
        swapRepositoryImpl: SwapRepositoryImpl
    ): SwapRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository

    @Binds
    @Singleton
    abstract fun bindPropuestaRepository(
        propuestaRepositoryImpl: PropuestaRepositoryImpl
    ): PropuestaRepository
}
