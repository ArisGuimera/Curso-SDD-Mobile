package com.aristidevs.cursopremiumandroid.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aristidevs.cursopremiumandroid.domain.model.Dog
import com.aristidevs.cursopremiumandroid.domain.usecase.GetDogsUseCase
import com.aristidevs.cursopremiumandroid.domain.usecase.SeedCatalogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DogViewModel @Inject constructor(
    private val getDogsUseCase: GetDogsUseCase,
    private val seedCatalogUseCase: SeedCatalogUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DogsUiState())
    val uiState: StateFlow<DogsUiState> = _uiState.asStateFlow()

    private var allDogs = emptyList<Dog>()

    init {
        // Dos corrutinas separadas a propósito: si el seed falla, la observación
        // de la base de datos sigue viva y Reintentar solo relanza el seed.
        observeDogs()
        seedCatalog()
    }

    private fun observeDogs() {
        viewModelScope.launch {
            getDogsUseCase().collect { dogs ->
                allDogs = dogs
                _uiState.update { state -> state.copy(dogs = filter(dogs, state.query)) }
            }
        }
    }

    private fun seedCatalog() {
        viewModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true, error = null) }
            try {
                seedCatalogUseCase()
                _uiState.update { state ->
                    state.copy(isLoading = false, error = null, canAddDog = true)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(isLoading = false, error = CONNECTION_ERROR, canAddDog = false)
                }
            }
        }
    }

    fun onRetry() {
        seedCatalog()
    }

    fun onQueryChange(query: String) {
        _uiState.update { state -> state.copy(query = query, dogs = filter(allDogs, query)) }
    }

    /** Tras crear un perro se limpia la búsqueda para que el nuevo sea visible. */
    fun onDogAdded() {
        onQueryChange("")
    }

    private fun filter(dogs: List<Dog>, query: String): List<Dog> {
        if (query.isBlank()) return dogs
        return dogs.filter { dog ->
            dog.name.contains(query, ignoreCase = true) ||
                    dog.breed.contains(query, ignoreCase = true)
        }
    }

    companion object {
        const val CONNECTION_ERROR = "No se pudo conectar. Comprueba tu conexión."
    }
}

data class DogsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val dogs: List<Dog> = emptyList(),
    val query: String = "",
    val canAddDog: Boolean = false
)
