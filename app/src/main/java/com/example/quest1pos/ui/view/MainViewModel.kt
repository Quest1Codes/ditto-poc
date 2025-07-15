package com.example.quest1pos.ui.view
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quest1pos.domain.usecase.GetSampleDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getSampleDataUseCase: GetSampleDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        getSampleData()
    }

    private fun getSampleData() {
        getSampleDataUseCase().onEach { data ->
            _uiState.value = UiState.Success(data.map { it.name })
        }.launchIn(viewModelScope)
    }
}

sealed class UiState {
    object Loading : UiState()
    data class Success(val data: List<String>) : UiState()
    data class Error(val message: String) : UiState()
}