package com.aristidevs.cursopremiumandroid.presentation.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aristidevs.cursopremiumandroid.domain.model.NewDog
import com.aristidevs.cursopremiumandroid.domain.usecase.AddDogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** Campos del formulario, en el mismo orden en que aparecen en pantalla. */
enum class AddDogField { NAME, BREED, AGE, DESCRIPTION, WEIGHT, ORIGIN, TEMPERAMENT }

@Serializable
data class AddDogForm(
    val name: String = "",
    val breed: String = "",
    val age: String = "",
    val description: String = "",
    val weight: String = "",
    val origin: String = "",
    val temperament: String = ""
) {
    fun valueOf(field: AddDogField): String = when (field) {
        AddDogField.NAME -> name
        AddDogField.BREED -> breed
        AddDogField.AGE -> age
        AddDogField.DESCRIPTION -> description
        AddDogField.WEIGHT -> weight
        AddDogField.ORIGIN -> origin
        AddDogField.TEMPERAMENT -> temperament
    }

    fun withValue(field: AddDogField, value: String): AddDogForm = when (field) {
        AddDogField.NAME -> copy(name = value)
        AddDogField.BREED -> copy(breed = value)
        AddDogField.AGE -> copy(age = value)
        AddDogField.DESCRIPTION -> copy(description = value)
        AddDogField.WEIGHT -> copy(weight = value)
        AddDogField.ORIGIN -> copy(origin = value)
        AddDogField.TEMPERAMENT -> copy(temperament = value)
    }

    fun hasContent(): Boolean = AddDogField.entries.any { field -> valueOf(field).isNotBlank() }
}

data class AddDogUiState(
    val form: AddDogForm = AddDogForm(),
    val errors: Map<AddDogField, String> = emptyMap(),
    val isSaving: Boolean = false,
    val saveError: String? = null,
    /** Primer campo con error: la pantalla lo enfoca y luego lo consume. */
    val focusTarget: AddDogField? = null,
    val showDiscardDialog: Boolean = false,
    val saved: Boolean = false
)

@HiltViewModel
class AddDogViewModel @Inject constructor(
    private val addDogUseCase: AddDogUseCase,
    private val json: Json,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddDogUiState(form = restoreForm()))
    val uiState: StateFlow<AddDogUiState> = _uiState.asStateFlow()

    /**
     * El borrador se recupera tras la muerte del proceso. Los errores no se
     * guardan: sobreviven a la rotación porque el ViewModel sobrevive, y no
     * tiene sentido reprochar al usuario un error que ya no está viendo.
     */
    private fun restoreForm(): AddDogForm {
        val stored = savedStateHandle.get<String>(KEY_FORM) ?: return AddDogForm()
        return runCatching { json.decodeFromString<AddDogForm>(stored) }.getOrElse { AddDogForm() }
    }

    fun onFieldChange(field: AddDogField, value: String) {
        _uiState.update { state ->
            val form = state.form.withValue(field, value)
            savedStateHandle[KEY_FORM] = json.encodeToString(form)
            state.copy(form = form, errors = state.errors - field, saveError = null)
        }
    }

    fun onSave() {
        // Guarda de reentrada en el ViewModel, no solo en el botón: un botón
        // deshabilitado en recomposición no impide dos pulsaciones seguidas.
        // También bloquea tras un guardado correcto, porque entre la inserción y
        // la navegación de vuelta hay una ventana en la que se puede volver a pulsar.
        if (_uiState.value.isSaving || _uiState.value.saved) return

        val form = _uiState.value.form
        val errors = validate(form)
        if (errors.isNotEmpty()) {
            _uiState.update { state ->
                state.copy(
                    errors = errors,
                    focusTarget = AddDogField.entries.first { field -> field in errors }
                )
            }
            return
        }

        _uiState.update { state ->
            state.copy(isSaving = true, errors = emptyMap(), saveError = null)
        }
        viewModelScope.launch {
            try {
                addDogUseCase(form.toNewDog())
                _uiState.update { state -> state.copy(isSaving = false, saved = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { state -> state.copy(isSaving = false, saveError = SAVE_ERROR) }
            }
        }
    }

    fun onFocusHandled() {
        _uiState.update { state -> state.copy(focusTarget = null) }
    }

    fun onDiscardRequested() {
        _uiState.update { state -> state.copy(showDiscardDialog = true) }
    }

    fun onDiscardDismissed() {
        _uiState.update { state -> state.copy(showDiscardDialog = false) }
    }

    private fun validate(form: AddDogForm): Map<AddDogField, String> {
        val errors = mutableMapOf<AddDogField, String>()
        AddDogField.entries.filter { field -> field != AddDogField.AGE }
            .forEach { field ->
                if (form.valueOf(field).isBlank()) errors[field] = REQUIRED_ERROR
            }
        val age = form.age.trim()
        val ageValue = age.toIntOrNull()
        when {
            age.isBlank() -> errors[AddDogField.AGE] = REQUIRED_ERROR
            ageValue == null -> errors[AddDogField.AGE] = AGE_NOT_A_NUMBER_ERROR
            ageValue !in MIN_AGE..MAX_AGE -> errors[AddDogField.AGE] = AGE_RANGE_ERROR
        }
        return errors
    }

    private fun AddDogForm.toNewDog(): NewDog = NewDog(
        name = name.trim(),
        breed = breed.trim(),
        age = age.trim().toInt(),
        description = description.trim(),
        weight = weight.trim(),
        origin = origin.trim(),
        temperament = temperament.trim()
    )

    companion object {
        const val KEY_FORM = "add_dog_form"
        const val MIN_AGE = 0
        const val MAX_AGE = 30
        const val REQUIRED_ERROR = "Este campo es obligatorio"
        const val AGE_NOT_A_NUMBER_ERROR = "La edad debe ser un número"
        const val AGE_RANGE_ERROR = "La edad debe estar entre $MIN_AGE y $MAX_AGE"
        const val SAVE_ERROR = "No se pudo guardar el perro."
    }
}
