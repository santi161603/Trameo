package com.market.trameo.domain.service

import com.market.trameo.domain.model.CreateHomeObjectRequest
import com.market.trameo.domain.model.HomeObject

interface HomeObjectCreationService {
    suspend fun create(request: CreateHomeObjectRequest): HomeObject
}

