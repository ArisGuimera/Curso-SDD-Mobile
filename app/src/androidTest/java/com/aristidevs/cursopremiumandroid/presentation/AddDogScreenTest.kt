package com.aristidevs.cursopremiumandroid.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.aristidevs.cursopremiumandroid.presentation.add.AddDogContent
import com.aristidevs.cursopremiumandroid.presentation.add.AddDogField
import com.aristidevs.cursopremiumandroid.presentation.add.AddDogForm
import com.aristidevs.cursopremiumandroid.presentation.add.AddDogUiState
import com.aristidevs.cursopremiumandroid.presentation.add.AddDogViewModel
import com.aristidevs.cursopremiumandroid.presentation.add.TAG_SAVE_BUTTON
import com.aristidevs.cursopremiumandroid.presentation.add.errorTagFor
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AddDogScreenTest {

    // Activity real para que BackHandler tenga un OnBackPressedDispatcher.
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun render(
        state: AddDogUiState = AddDogUiState(),
        onSave: () -> Unit = {},
        onDiscardRequested: () -> Unit = {},
        onDiscardDismissed: () -> Unit = {},
        onBackSelected: () -> Unit = {}
    ) {
        composeRule.setContent {
            AddDogContent(
                uiState = state,
                onFieldChange = { _, _ -> },
                onSave = onSave,
                onFocusHandled = {},
                onDiscardRequested = onDiscardRequested,
                onDiscardDismissed = onDiscardDismissed,
                onBackSelected = onBackSelected
            )
        }
    }

    private fun pressBack() {
        composeRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        composeRule.waitForIdle()
    }

    private val filledForm = AddDogForm(
        name = "Toby", breed = "Beagle", age = "4", description = "Curioso",
        weight = "12 kg", origin = "Inglaterra", temperament = "Alegre"
    )

    // AC-09
    @Test
    fun elFormularioMuestraLosSieteCampos() {
        render()

        listOf("Nombre", "Raza", "Edad", "Descripción", "Peso", "Origen", "Temperamento")
            .forEach { label ->
                composeRule.onNodeWithText(label).performScrollTo().assertIsDisplayed()
            }
    }

    // AC-10
    @Test
    fun losErroresSeMuestranBajoCadaCampo() {
        render(
            AddDogUiState(
                errors = mapOf(
                    AddDogField.NAME to AddDogViewModel.REQUIRED_ERROR,
                    AddDogField.AGE to AddDogViewModel.AGE_RANGE_ERROR
                )
            )
        )

        composeRule.onNodeWithTag(errorTagFor(AddDogField.NAME)).performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithTag(errorTagFor(AddDogField.AGE)).performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithText(AddDogViewModel.AGE_RANGE_ERROR).assertIsDisplayed()
    }

    // AC-10
    @Test
    fun guardarInvocaAlViewModel() {
        var saves = 0
        render(onSave = { saves++ })

        composeRule.onNodeWithTag(TAG_SAVE_BUTTON).performScrollTo().performClick()

        assertEquals(1, saves)
    }

    // AC-15: mientras se guarda el botón no admite más pulsaciones
    @Test
    fun elBotonGuardarSeDeshabilitaMientrasSeGuarda() {
        render(AddDogUiState(form = filledForm, isSaving = true))

        composeRule.onNodeWithTag(TAG_SAVE_BUTTON).performScrollTo().assertIsNotEnabled()
    }

    @Test
    fun elBotonGuardarEstaActivoEnReposo() {
        render(AddDogUiState(form = filledForm))

        composeRule.onNodeWithTag(TAG_SAVE_BUTTON).performScrollTo().assertIsEnabled()
    }

    // AC-16
    @Test
    fun atrasConDatosEscritosPideConfirmacion() {
        var discardRequests = 0
        var backs = 0
        render(
            AddDogUiState(form = filledForm),
            onDiscardRequested = { discardRequests++ },
            onBackSelected = { backs++ }
        )

        pressBack()

        assertEquals(1, discardRequests)
        assertEquals("no debe salir sin confirmar", 0, backs)
    }

    // AC-17
    @Test
    fun atrasConElFormularioVacioSaleDirecto() {
        var discardRequests = 0
        render(AddDogUiState(), onDiscardRequested = { discardRequests++ })

        pressBack()

        assertEquals("el formulario vacío no intercepta el atrás", 0, discardRequests)
    }

    // AC-16
    @Test
    fun elBotonDeLaBarraTambienPideConfirmacion() {
        var discardRequests = 0
        render(AddDogUiState(form = filledForm), onDiscardRequested = { discardRequests++ })

        composeRule.onNodeWithContentDescription("Volver").performClick()

        assertEquals(1, discardRequests)
    }

    // AC-16
    @Test
    fun confirmarElDescarteVuelveAtras() {
        var backs = 0
        render(
            AddDogUiState(form = filledForm, showDiscardDialog = true),
            onBackSelected = { backs++ }
        )

        composeRule.onNodeWithText("¿Descartar el perro?").assertIsDisplayed()
        composeRule.onNodeWithText("Descartar").performClick()

        assertEquals(1, backs)
    }

    // AC-16
    @Test
    fun cancelarElDescarteNoSale() {
        var backs = 0
        var dismisses = 0
        render(
            AddDogUiState(form = filledForm, showDiscardDialog = true),
            onDiscardDismissed = { dismisses++ },
            onBackSelected = { backs++ }
        )

        composeRule.onNodeWithText("Seguir editando").performClick()

        assertEquals(1, dismisses)
        assertEquals(0, backs)
    }
}
