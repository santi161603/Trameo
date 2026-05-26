package com.market.trameo.di

import com.market.trameo.data.service.HomeObjectCreationServiceImpl
import com.market.trameo.domain.service.HomeObjectCreationService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    abstract fun bindHomeObjectCreationService(
        homeObjectCreationServiceImpl: HomeObjectCreationServiceImpl
    ): HomeObjectCreationService
}

