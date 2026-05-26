package com.market.trameo.features.misobjetos

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.trameo.core.session.SessionDataStore
import com.market.trameo.domain.model.HomeObject
import com.market.trameo.domain.model.ModerationStatus
import com.market.trameo.domain.model.ObjectCategory
import com.market.trameo.domain.model.ObjectCondition
import com.market.trameo.domain.model.SwapObject
import com.market.trameo.domain.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class MyObjectsViewModel @Inject constructor(
    homeRepository: HomeRepository,
    sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatuses = MutableStateFlow<Set<ModerationStatus>>(emptySet())
    val selectedStatuses: StateFlow<Set<ModerationStatus>> = _selectedStatuses.asStateFlow()

    val items: StateFlow<List<SwapObject>> = sessionDataStore.userId
        .flatMapLatest { userId ->
            if (userId.isNullOrBlank()) {
                flowOf(emptyList())
            } else {
                homeRepository.getObjectsByOwner(userId)
                    .map { objects -> objects.map { it.toSwapObject() } }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val filteredItems: StateFlow<List<SwapObject>> = combine(
        items,
        searchQuery,
        selectedStatuses
    ) { currentItems, query, statuses ->
        val normalizedQuery = query.trim().lowercase()
        currentItems.filter { item ->
            val matchesQuery = normalizedQuery.isBlank() ||
                item.name.lowercase().contains(normalizedQuery) ||
                item.description.lowercase().contains(normalizedQuery) ||
                item.exchangePreferences.lowercase().contains(normalizedQuery)
            val matchesStatus = statuses.isEmpty() || statuses.contains(item.moderationStatus)
            matchesQuery && matchesStatus
        }.distinctBy { it.id }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun updateSearchQuery(value: String) {
        _searchQuery.value = value
    }

    fun toggleStatus(status: ModerationStatus) {
        _selectedStatuses.value = if (_selectedStatuses.value.contains(status)) {
            _selectedStatuses.value - status
        } else {
            _selectedStatuses.value + status
        }
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedStatuses.value = emptySet()
    }

    private fun HomeObject.toSwapObject(): SwapObject {
        return SwapObject(
            id = id,
            ownerId = ownerId,
            photos = photos.map { Uri.parse(it) },
            name = name,
            description = description,
            category = parseCategory(category),
            condition = parseCondition(condition),
            exchangePreferences = exchangePreferences,
            moderationStatus = moderationStatus
        )
    }

    private fun parseCategory(value: String): ObjectCategory {
        return runCatching { ObjectCategory.valueOf(value) }.getOrNull()
            ?: ObjectCategory.entries.first()
    }

    private fun parseCondition(value: String): ObjectCondition {
        return runCatching { ObjectCondition.valueOf(value) }.getOrNull()
            ?: ObjectCondition.entries.first()
    }
}
