package com.market.trameo.features.swap.create

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.trameo.core.session.SessionDataStore
import com.market.trameo.core.utils.RequestResult
import com.market.trameo.core.utils.ValidatedField
import com.market.trameo.domain.model.CreateHomeObjectRequest
import com.market.trameo.domain.model.ObjectCategory
import com.market.trameo.domain.model.ObjectCondition
import com.market.trameo.domain.service.HomeObjectCreationService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CreateObjectViewModel @Inject constructor(
    private val homeObjectCreationService: HomeObjectCreationService,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    val name = ValidatedField("") { value ->
        when {
            value.isBlank() -> "El nombre del objeto es obligatorio"
            value.trim().length < 3 -> "Minimo 3 caracteres"
            else -> null
        }
    }

    val description = ValidatedField("") { value ->
        when {
            value.isBlank() -> "La descripcion es obligatoria"
            value.trim().length < 10 -> "Describe un poco mas tu objeto"
            else -> null
        }
    }

    val category = ValidatedField<ObjectCategory?>(null) { value ->
        if (value == null) "Selecciona una categoria" else null
    }

    val condition = ValidatedField<ObjectCondition?>(null) { value ->
        if (value == null) "Selecciona el estado" else null
    }

    val exchangePreferences = ValidatedField("") { value ->
        when {
            value.isBlank() -> "Indica que te gustaria recibir"
            value.trim().length < 5 -> "Agrega mas detalle"
            else -> null
        }
    }

    private val _photos = MutableStateFlow<List<Uri>>(emptyList())
    val photos: StateFlow<List<Uri>> = _photos.asStateFlow()

    private val _publishResult = MutableStateFlow<RequestResult?>(null)
    val publishResult: StateFlow<RequestResult?> = _publishResult.asStateFlow()

    private val _isPublishing = MutableStateFlow(false)
    val isPublishing: StateFlow<Boolean> = _isPublishing.asStateFlow()

    val canPublish: Boolean
        get() = name.isValid &&
            description.isValid &&
            category.isValid &&
            condition.isValid &&
            exchangePreferences.isValid &&
            photos.value.isNotEmpty() &&
            !_isPublishing.value

    fun addPhoto(uri: Uri) {
        _photos.update { current ->
            if (current.size >= MAX_PHOTOS || current.contains(uri)) current else current + uri
        }
    }

    fun removePhoto(uri: Uri) {
        _photos.update { current -> current - uri }
    }

    fun publishObject() {
        val validForm = canPublish
        if (!validForm) {
            name.forceShowError()
            description.forceShowError()
            category.forceShowError()
            condition.forceShowError()
            exchangePreferences.forceShowError()
            if (_photos.value.isEmpty()) {
                _publishResult.value = RequestResult.Failure("Debes agregar al menos una foto")
            }
            return
        }

        viewModelScope.launch {
            _isPublishing.value = true
            _publishResult.value = null
            runCatching {
                sessionDataStore.getCurrentUserId()
                    ?: error("Debes iniciar sesion para publicar")
                homeObjectCreationService.create(
                    CreateHomeObjectRequest(
                        photos = photos.value.map(::requireLocalUri),
                        name = name.value.trim(),
                        description = description.value.trim(),
                        category = category.value!!.name,
                        condition = condition.value!!.name,
                        exchangePreferences = exchangePreferences.value.trim()
                    )
                )
            }.onSuccess {
                _publishResult.value = RequestResult.Success("Objeto publicado con exito")
                resetForm()
            }.onFailure { error ->
                _publishResult.value = RequestResult.Failure(error.message ?: "No se pudo publicar")
            }
            _isPublishing.value = false
        }
    }

    private fun requireLocalUri(uri: Uri): String {
        val scheme = uri.scheme?.lowercase()
        if (scheme != "content" && scheme != "file") {
            error("Solo se permiten URIs locales")
        }
        return uri.toString()
    }

    fun resetPublishResult() {
        _publishResult.value = null
    }

    private fun resetForm() {
        name.reset()
        description.reset()
        category.reset()
        condition.reset()
        exchangePreferences.reset()
        _photos.value = emptyList()
    }

    companion object {
        const val MAX_PHOTOS = 5
    }
}
