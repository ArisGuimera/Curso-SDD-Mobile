package com.aristidevs.cursopremiumandroid.presentation

import androidx.lifecycle.SavedStateHandle
import com.aristidevs.cursopremiumandroid.domain.usecase.AddDogUseCase
import com.aristidevs.cursopremiumandroid.fake.FakeDogRepository
import com.aristidevs.cursopremiumandroid.presentation.add.AddDogField
import com.aristidevs.cursopremiumandroid.presentation.add.AddDogForm
import com.aristidevs.cursopremiumandroid.presentation.add.AddDogViewModel
import com.aristidevs.cursopremiumandroid.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddDogViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeDogRepository
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setUp() {
        repository = FakeDogRepository()
    }

    private fun viewModel(savedState: SavedStateHandle = SavedStateHandle()) =
        AddDogViewModel(AddDogUseCase(repository), json, savedState)

    private fun AddDogViewModel.fillValid(age: String = "4") {
        onFieldChange(AddDogField.NAME, "Toby")
        onFieldChange(AddDogField.BREED, "Beagle")
        onFieldChange(AddDogField.AGE, age)
        onFieldChange(AddDogField.DESCRIPTION, "Curioso")
        onFieldChange(AddDogField.WEIGHT, "12 kg")
        onFieldChange(AddDogField.ORIGIN, "Inglaterra")
        onFieldChange(AddDogField.TEMPERAMENT, "Alegre")
    }

    // AC-09
    @Test
    fun `el formulario empieza vacio y sin errores`() {
        val state = viewModel().uiState.value

        assertEquals(AddDogForm(), state.form)
        assertTrue(state.errors.isEmpty())
        assertFalse(state.saved)
    }

    // AC-10
    @Test
    fun `guardar el formulario vacio marca los siete campos y no inserta`() = runTest {
        val viewModel = viewModel()

        viewModel.onSave()

        val state = viewModel.uiState.value
        assertEquals(7, state.errors.size)
        assertEquals(AddDogViewModel.REQUIRED_ERROR, state.errors[AddDogField.NAME])
        assertTrue(repository.added.isEmpty())
        assertFalse(state.saved)
    }

    // AC-10
    @Test
    fun `un campo con solo espacios cuenta como vacio`() = runTest {
        val viewModel = viewModel()
        viewModel.fillValid()
        viewModel.onFieldChange(AddDogField.ORIGIN, "   ")

        viewModel.onSave()

        assertEquals(
            AddDogViewModel.REQUIRED_ERROR,
            viewModel.uiState.value.errors[AddDogField.ORIGIN]
        )
        assertTrue(repository.added.isEmpty())
    }

    // AC-10: el foco va al primer campo con error en orden de pantalla
    @Test
    fun `el foco apunta al primer campo con error`() = runTest {
        val viewModel = viewModel()
        viewModel.fillValid()
        viewModel.onFieldChange(AddDogField.BREED, "")
        viewModel.onFieldChange(AddDogField.ORIGIN, "")

        viewModel.onSave()

        assertEquals(AddDogField.BREED, viewModel.uiState.value.focusTarget)
    }

    // AC-11
    @Test
    fun `la edad acepta los extremos del rango`() = runTest {
        listOf("0", "30").forEach { age ->
            repository = FakeDogRepository()
            val viewModel = viewModel()
            viewModel.fillValid(age = age)

            viewModel.onSave()

            assertNull("edad $age debería ser válida", viewModel.uiState.value.errors[AddDogField.AGE])
            assertEquals(1, repository.added.size)
        }
    }

    // AC-11
    @Test
    fun `la edad fuera de rango se rechaza`() = runTest {
        listOf("31", "-1", "100").forEach { age ->
            val viewModel = viewModel()
            viewModel.fillValid(age = age)

            viewModel.onSave()

            assertEquals(
                "edad $age debería rechazarse",
                AddDogViewModel.AGE_RANGE_ERROR,
                viewModel.uiState.value.errors[AddDogField.AGE]
            )
        }
        assertTrue(repository.added.isEmpty())
    }

    // AC-11
    @Test
    fun `la edad no numerica se rechaza con su propio mensaje`() = runTest {
        val viewModel = viewModel()
        viewModel.fillValid(age = "tres")

        viewModel.onSave()

        assertEquals(
            AddDogViewModel.AGE_NOT_A_NUMBER_ERROR,
            viewModel.uiState.value.errors[AddDogField.AGE]
        )
        assertTrue(repository.added.isEmpty())
    }

    // AC-12
    @Test
    fun `guardar con datos validos inserta el perro y avisa`() = runTest {
        val viewModel = viewModel()
        viewModel.fillValid()

        viewModel.onSave()

        assertTrue(viewModel.uiState.value.saved)
        assertEquals(1, repository.added.size)
        assertEquals("Toby", repository.added.first().name)
        assertEquals(4, repository.added.first().age)
    }

    // AC-12: los valores se recortan antes de guardarse
    @Test
    fun `los valores se guardan sin espacios sobrantes`() = runTest {
        val viewModel = viewModel()
        viewModel.fillValid()
        viewModel.onFieldChange(AddDogField.NAME, "  Toby  ")

        viewModel.onSave()

        assertEquals("Toby", repository.added.first().name)
    }

    // AC-15
    @Test
    fun `pulsar guardar tres veces seguidas inserta un solo perro`() = runTest {
        val viewModel = viewModel()
        viewModel.fillValid()

        viewModel.onSave()
        viewModel.onSave()
        viewModel.onSave()

        assertEquals(1, repository.added.size)
    }

    // AC-10: al corregir un campo desaparece su error
    @Test
    fun `editar un campo con error lo limpia`() = runTest {
        val viewModel = viewModel()
        viewModel.onSave()

        viewModel.onFieldChange(AddDogField.NAME, "Toby")

        assertNull(viewModel.uiState.value.errors[AddDogField.NAME])
        assertEquals(6, viewModel.uiState.value.errors.size)
    }

    // AC-19
    @Test
    fun `el borrador se recupera del estado guardado`() = runTest {
        val savedState = SavedStateHandle()
        viewModel(savedState).fillValid()

        val restored = viewModel(savedState).uiState.value.form

        assertEquals("Toby", restored.name)
        assertEquals("Inglaterra", restored.origin)
        assertEquals("4", restored.age)
    }

    // AC-19: un estado guardado corrupto no rompe la pantalla
    @Test
    fun `un borrador ilegible se descarta sin fallar`() = runTest {
        val savedState = SavedStateHandle(mapOf(AddDogViewModel.KEY_FORM to "no es json"))

        assertEquals(AddDogForm(), viewModel(savedState).uiState.value.form)
    }

    // Error de escritura: no se navega y no se pierde lo escrito
    @Test
    fun `si falla el guardado se conserva el formulario y se avisa`() = runTest {
        repository.failAdd = true
        val viewModel = viewModel()
        viewModel.fillValid()

        viewModel.onSave()

        val state = viewModel.uiState.value
        assertEquals(AddDogViewModel.SAVE_ERROR, state.saveError)
        assertFalse(state.saved)
        assertFalse(state.isSaving)
        assertEquals("Toby", state.form.name)
    }

    // AC-16 / AC-17
    @Test
    fun `hasContent distingue el formulario vacio del que tiene datos`() {
        val viewModel = viewModel()
        assertFalse(viewModel.uiState.value.form.hasContent())

        viewModel.onFieldChange(AddDogField.NAME, "T")

        assertTrue(viewModel.uiState.value.form.hasContent())
    }
}
