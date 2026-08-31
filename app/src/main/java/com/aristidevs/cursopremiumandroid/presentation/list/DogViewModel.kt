package com.aristidevs.cursopremiumandroid.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aristidevs.cursopremiumandroid.domain.model.Dog
import com.aristidevs.cursopremiumandroid.domain.usecase.GetDogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DogViewModel @Inject constructor(private val getDogsUseCase: GetDogsUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow<DogsUiState>(DogsUiState())
    val uiState: StateFlow<DogsUiState> = _uiState.asStateFlow()

    private var allDogs = emptyList<Dog>()

    init {
        loadDogs()
    }

    private fun loadDogs() {
        viewModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true) }
            try {
                allDogs = getDogsUseCase()
                _uiState.update { state -> state.copy(isLoading = false, dogs = allDogs) }
            }catch (e: Exception){
                _uiState.update { state -> state.copy(isLoading = false, error = e.message) }
            }

        }
    }

    fun onQueryChange(query: String) {
        val filteredDogs = allDogs.filter { dog ->
            dog.name.contains(query, ignoreCase = true) ||
                    dog.breed.contains(query, ignoreCase = true)
        }
        _uiState.update { state -> state.copy(query = query, dogs = filteredDogs) }
    }

}

data class DogsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val dogs: List<Dog> = emptyList(),
    val query:String = ""
)