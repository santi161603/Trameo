package com.market.trameo.domain.repository

import com.market.trameo.domain.model.Propuesta
import kotlinx.coroutines.flow.Flow

interface PropuestaRepository {
    suspend fun crearPropuesta(propuesta: Propuesta): Result<Unit>
    fun getMisPropuestasEnviadas(uid: String): Flow<List<Propuesta>>
    fun getPropuestasRecibidas(uid: String): Flow<List<Propuesta>>
    suspend fun actualizarEstadoPropuesta(propuestaId: String, nuevoEstado: String): Result<Unit>
    suspend fun getPropuestaById(id: String): Propuesta?
}
