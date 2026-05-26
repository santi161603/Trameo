package com.market.trameo.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.trameo.core.session.SessionDataStore
import com.market.trameo.domain.model.HomeObject
import com.market.trameo.domain.model.ObjectCategory
import com.market.trameo.domain.model.User
import com.market.trameo.domain.repository.HomeRepository
import com.market.trameo.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HomeViewModel @Inject constructor(
    homeRepository: HomeRepository,
    private val userRepository: UserRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<ObjectCategory?>(null)
    val selectedCategory: StateFlow<ObjectCategory?> = _selectedCategory.asStateFlow()

    val items: StateFlow<List<HomeObject>> = combine(
        homeRepository.getPublishedObjects(),
        _searchQuery,
        _selectedCategory
    ) { objects, query, selectedCategory ->
        val normalizedQuery = query.trim().lowercase()
        objects.filter { item ->
            val matchesCategory = selectedCategory == null || 
                item.category.equals(selectedCategory.name, ignoreCase = true)
            val matchesQuery = normalizedQuery.isBlank() ||
                item.name.lowercase().contains(normalizedQuery) ||
                item.description.lowercase().contains(normalizedQuery)
            matchesCategory && matchesQuery
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUser: StateFlow<User?> = sessionDataStore.userId
        .flatMapLatest { userId ->
            flow {
                if (userId != null) {
                    emit(userRepository.findById(userId))
                } else {
                    emit(null)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun onSearchQueryChange(value: String) {
        _searchQuery.value = value
    }

    fun onCategoryFilterChange(value: ObjectCategory?) {
        _selectedCategory.value = value
    }
}
