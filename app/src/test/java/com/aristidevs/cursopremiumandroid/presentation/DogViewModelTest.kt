package com.aristidevs.cursopremiumandroid.presentation

import com.aristidevs.cursopremiumandroid.domain.model.NewDog
import com.aristidevs.cursopremiumandroid.domain.usecase.GetDogsUseCase
import com.aristidevs.cursopremiumandroid.domain.usecase.SeedCatalogUseCase
import com.aristidevs.cursopremiumandroid.fake.FakeDogRepository
import com.aristidevs.cursopremiumandroid.presentation.list.DogViewModel
import com.aristidevs.cursopremiumandroid.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DogViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeDogRepository

    @Before
    fun setUp() {
        repository = FakeDogRepository()
    }

    private fun viewModel() = DogViewModel(
        GetDogsUseCase(repository),
        SeedCatalogUseCase(repository)
    )

    // AC-01
    @Test
    fun `al abrir con conexion se siembra y se muestra el catalogo`() = runTest {
        val state = viewModel().uiState.value

        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(listOf("Luna", "Max"), state.dogs.map { it.name })
    }

    // AC-01 / AC-09: el acceso a crear solo aparece con el catálogo sembrado
    @Test
    fun `tras sembrar se habilita el alta`() = runTest {
        assertTrue(viewModel().uiState.value.canAddDog)
    }

    // AC-03
    @Test
    fun `si el seed falla se muestra error y el alta queda bloqueada`() = runTest {
        repository.failSeed = true

        val state = viewModel().uiState.value

        assertFalse(state.isLoading)
        assertEquals(DogViewModel.CONNECTION_ERROR, state.error)
        assertFalse(state.canAddDog)
    }

    // AC-04
    @Test
    fun `reintentar tras recuperar la conexion siembra y habilita el alta`() = runTest {
        repository.failSeed = true
        val viewModel = viewModel()

        repository.failSeed = false
        viewModel.onRetry()

        val state = viewModel.uiState.value
        assertNull(state.error)
        assertTrue(state.canAddDog)
        assertEquals(listOf("Luna", "Max"), state.dogs.map { it.name })
    }

    // AC-04: el reintento no duplica el catálogo
    @Test
    fun `reintentar cuando ya estaba sembrado no duplica la lista`() = runTest {
        val viewModel = viewModel()

        viewModel.onRetry()

        assertEquals(2, viewModel.uiState.value.dogs.size)
        assertEquals(2, repository.seedCalls)
    }

    // AC-12 / AC-20
    @Test
    fun `el perro creado aparece el primero de la lista`() = runTest {
        val viewModel = viewModel()

        repository.addDog(newDog())

        val dogs = viewModel.uiState.value.dogs
        assertEquals(listOf("Toby", "Luna", "Max"), dogs.map { it.name })
        assertTrue(dogs.first().isUserCreated)
    }

    // AC-21
    @Test
    fun `la busqueda filtra sobre catalogo y creados`() = runTest {
        val viewModel = viewModel()
        repository.addDog(newDog(name = "Lucas"))

        viewModel.onQueryChange("lu")

        assertEquals(listOf("Lucas", "Luna"), viewModel.uiState.value.dogs.map { it.name })
    }

    // AC-21
    @Test
    fun `la busqueda tambien filtra por raza`() = runTest {
        val viewModel = viewModel()

        viewModel.onQueryChange("labrador")

        assertEquals(listOf("Max"), viewModel.uiState.value.dogs.map { it.name })
    }

    // AC-21
    @Test
    fun `una busqueda sin coincidencias deja la lista vacia y conserva el texto`() = runTest {
        val viewModel = viewModel()

        viewModel.onQueryChange("zzz")

        assertTrue(viewModel.uiState.value.dogs.isEmpty())
        assertEquals("zzz", viewModel.uiState.value.query)
    }

    // AC-12
    @Test
    fun `al crear un perro se limpia la busqueda para que sea visible`() = runTest {
        val viewModel = viewModel()
        viewModel.onQueryChange("max")
        repository.addDog(newDog())

        viewModel.onDogAdded()

        assertEquals("", viewModel.uiState.value.query)
        assertEquals(listOf("Toby", "Luna", "Max"), viewModel.uiState.value.dogs.map { it.name })
    }

    // AC-21: un perro nuevo que no casa con la búsqueda activa no la rompe
    @Test
    fun `con busqueda activa el perro creado solo aparece si coincide`() = runTest {
        val viewModel = viewModel()
        viewModel.onQueryChange("max")

        repository.addDog(newDog())

        assertEquals(listOf("Max"), viewModel.uiState.value.dogs.map { it.name })
    }

    private fun newDog(name: String = "Toby") = NewDog(
        name = name,
        breed = "Beagle",
        age = 4,
        description = "Curioso",
        weight = "12 kg",
        origin = "Inglaterra",
        temperament = "Alegre"
    )
}
