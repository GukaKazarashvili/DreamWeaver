package com.example.dreamweaver.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dreamweaver.data.model.Dream
import com.example.dreamweaver.data.repository.DreamRepository
import com.example.dreamweaver.ui.state.DreamFilter
import com.example.dreamweaver.ui.state.DreamScreenState
import com.example.dreamweaver.ui.state.DreamSort
import com.example.dreamweaver.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVVM ViewModel. Holds no Android View/Context references, exposes a
 * single StateFlow<DreamScreenState> that the Compose screen collects.
 */
class DreamViewModel(private val repository: DreamRepository) : ViewModel() {

    private val _state = MutableStateFlow(DreamScreenState())
    val state: StateFlow<DreamScreenState> = _state.asStateFlow()

    init {
        observeDreams()
        loadDailyInsight()
    }

    private fun observeDreams() {
        viewModelScope.launch {
            repository.observeDreams().collect { dreams ->
                _state.update { it.copy(dreams = dreams) }
            }
        }
    }

    fun loadDailyInsight() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingInsight = true) }
            when (val result = repository.fetchDailyInsight()) {
                is Resource.Success -> _state.update {
                    it.copy(isLoadingInsight = false, dailyInsight = result.data)
                }
                is Resource.Error -> _state.update {
                    it.copy(isLoadingInsight = false, dailyInsight = "ვერ ჩაიტვირთა: ${result.message}")
                }
                Resource.Loading -> Unit
            }
        }
    }

    fun addDream(title: String, description: String, mood: String, isLucid: Boolean) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addDream(
                Dream(
                    title = title.trim(),
                    description = description.trim(),
                    mood = mood,
                    isLucid = isLucid
                )
            )
        }
    }

    fun deleteDream(dream: Dream) {
        viewModelScope.launch { repository.deleteDream(dream) }
    }

    fun setSort(sort: DreamSort) {
        _state.update { it.copy(sort = sort) }
    }

    fun setFilter(filter: DreamFilter) {
        _state.update { it.copy(filter = filter) }
    }
}
