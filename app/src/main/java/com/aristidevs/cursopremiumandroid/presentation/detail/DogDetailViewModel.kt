package com.aristidevs.cursopremiumandroid.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aristidevs.cursopremiumandroid.domain.model.DogDetailModel
import com.aristidevs.cursopremiumandroid.domain.usecase.GetDogDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun loadDog(id: Int) {
        viewModelScope.launch {
            _uiState.value = DogDetailUiState.Loading
            try {
                val dogDetail = dogDetailUseCase(id)
                _uiState.value = DogDetailUiState.Success(dogDetail)

            } catch (e: Exception) {
                _uiState.value = DogDetailUiState.Error(e.message.orEmpty())
            }
        }
    }

}

sealed interface DogDetailUiState {
    data object Loading : DogDetailUiState
    data class Success(val dogDetail: DogDetailModel) : DogDetailUiState
    data class Error(val message: String) : DogDetailUiState
}