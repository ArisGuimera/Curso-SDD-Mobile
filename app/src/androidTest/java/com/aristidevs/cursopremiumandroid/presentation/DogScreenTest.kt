package com.aristidevs.cursopremiumandroid.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.aristidevs.cursopremiumandroid.domain.model.Dog
import com.aristidevs.cursopremiumandroid.presentation.list.DogContent
import com.aristidevs.cursopremiumandroid.presentation.list.DogsUiState
import com.aristidevs.cursopremiumandroid.presentation.list.USER_CREATED_LABEL
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Sobre el composable sin estado: no hace falta Hilt ni base de datos. */
class DogScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val catalogDog = Dog(1, "Luna", 3, "Cariñosa", "https://x/luna.jpg", "Golden Retriever")
    private val userDog = Dog(11, "Toby", 4, "Curioso", "", "Beagle", isUserCreated = true)

    private fun render(
        state: DogsUiState,
        onAddDogClicked: () -> Unit = {},
        onRetry: () -> Unit = {}
    ) {
        composeRule.setContent {
            DogContent(
                uiState = state,
                onQueryChange = {},
                onDogClicked = {},
                onAddDogClicked = onAddDogClicked,
                onRetry = onRetry
            )
        }
    }

    // AC-22
    @Test
    fun elPerroCreadoLlevaUnDistintivoDeTexto() {
        render(DogsUiState(isLoading = false, dogs = listOf(userDog, catalogDog), canAddDog = true))

        composeRule.onNodeWithText(USER_CREATED_LABEL).assertIsDisplayed()
    }

    // AC-22: el placeholder se anuncia como tal, no como una foto
    @Test
    fun elPlaceholderDelPerroSinFotoTieneSuPropiaDescripcion() {
        render(DogsUiState(isLoading = false, dogs = listOf(userDog), canAddDog = true))

        composeRule.onNodeWithContentDescription("Toby, sin foto").assertExists()
        composeRule.onNodeWithText("T").assertIsDisplayed()
    }

    // AC-22: un perro del catálogo no lleva distintivo
    @Test
    fun elPerroDelCatalogoNoLlevaDistintivo() {
        render(DogsUiState(isLoading = false, dogs = listOf(catalogDog), canAddDog = true))

        composeRule.onNodeWithText(USER_CREATED_LABEL).assertDoesNotExist()
    }

    // AC-03
    @Test
    fun sinCatalogoSembradoNoHayAccesoAlAlta() {
        render(DogsUiState(isLoading = false, error = "Sin conexión", canAddDog = false))

        composeRule.onNodeWithContentDescription("Añadir perro").assertDoesNotExist()
    }

    // AC-09
    @Test
    fun conCatalogoSembradoElAccesoAlAltaEstaDisponible() {
        var clicks = 0
        render(
            DogsUiState(isLoading = false, dogs = listOf(catalogDog), canAddDog = true),
            onAddDogClicked = { clicks++ }
        )

        composeRule.onNodeWithContentDescription("Añadir perro").performClick()

        assertEquals(1, clicks)
    }

    // AC-04
    @Test
    fun elEstadoDeErrorOfreceReintentar() {
        var retries = 0
        render(
            DogsUiState(isLoading = false, error = "No se pudo conectar."),
            onRetry = { retries++ }
        )

        composeRule.onNodeWithText("No se pudo conectar.").assertIsDisplayed()
        composeRule.onNodeWithText("Reintentar").performClick()

        assertEquals(1, retries)
    }

    // AC-21
    @Test
    fun unaBusquedaSinCoincidenciasConservaLoEscrito() {
        render(DogsUiState(isLoading = false, dogs = emptyList(), query = "zzz", canAddDog = true))

        composeRule.onNodeWithText("No hay resultados").assertIsDisplayed()
        composeRule.onNodeWithText("zzz").assertIsDisplayed()
    }
}
