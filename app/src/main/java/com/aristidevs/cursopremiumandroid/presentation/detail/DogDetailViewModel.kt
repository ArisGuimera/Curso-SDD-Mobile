package com.aristidevs.cursopremiumandroid.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aristidevs.cursopremiumandroid.domain.model.DogDetailModel
import com.aristidevs.cursopremiumandroid.domain.usecase.GetDogDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogDetailViewModel @Inject constructor(private val dogDetailUseCase: GetDogDetailUseCase) :
    ViewModel() {

    private val _uiState = MutableStateFlow<DogDetailUiState>(DogDetailUiState.Loading)
    val uiState: StateFlow<DogDetailUiState> = _uiState.asStateFlow()

    private var currentId: Int? = null

    fun loadDog(id: Int) {
        currentId = id
        viewModelScope.launch {
            _uiState.value = DogDetailUiState.Loading
            try {
                _uiState.value = DogDetailUiState.Success(dogDetailUseCase(id))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = DogDetailUiState.Error(CONNECTION_ERROR)
            }
        }
    }

    fun onRetry() {
        currentId?.let { id -> loadDog(id) }
    }

    companion object {
        const val CONNECTION_ERROR = "No se pudo conectar. Comprueba tu conexión."
    }
}

sealed interface DogDetailUiState {
    data object Loading : DogDetailUiState
    data class Success(val dogDetail: DogDetailModel) : DogDetailUiState
    data class Error(val message: String) : DogDetailUiState
}
