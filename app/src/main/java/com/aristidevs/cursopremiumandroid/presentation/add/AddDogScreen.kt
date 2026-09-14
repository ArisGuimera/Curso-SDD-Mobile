package com.aristidevs.cursopremiumandroid.presentation.add

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aristidevs.cursopremiumandroid.R
import com.aristidevs.cursopremiumandroid.ui.theme.BackgroundApp
import com.aristidevs.cursopremiumandroid.ui.theme.BackgroundComponent
import com.aristidevs.cursopremiumandroid.ui.theme.ControlColor
import com.aristidevs.cursopremiumandroid.ui.theme.PrimaryButton
import com.aristidevs.cursopremiumandroid.ui.theme.SecondaryText

@Composable
fun AddDogScreen(
    onDogSaved: () -> Unit,
    onBackSelected: () -> Unit,
    viewModel: AddDogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onDogSaved()
    }

    AddDogContent(
        uiState = uiState,
        onFieldChange = { field, value -> viewModel.onFieldChange(field, value) },
        onSave = { viewModel.onSave() },
        onFocusHandled = { viewModel.onFocusHandled() },
        onDiscardRequested = { viewModel.onDiscardRequested() },
        onDiscardDismissed = { viewModel.onDiscardDismissed() },
        onBackSelected = onBackSelected
    )
}

private data class FieldSpec(
    val field: AddDogField,
    val label: String,
    val singleLine: Boolean = true,
    val keyboardType: KeyboardType = KeyboardType.Text
)

private val FIELDS = listOf(
    FieldSpec(AddDogField.NAME, "Nombre"),
    FieldSpec(AddDogField.BREED, "Raza"),
    FieldSpec(AddDogField.AGE, "Edad", keyboardType = KeyboardType.Number),
    FieldSpec(AddDogField.DESCRIPTION, "Descripción", singleLine = false),
    FieldSpec(AddDogField.WEIGHT, "Peso"),
    FieldSpec(AddDogField.ORIGIN, "Origen"),
    FieldSpec(AddDogField.TEMPERAMENT, "Temperamento")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDogContent(
    uiState: AddDogUiState,
    onFieldChange: (AddDogField, String) -> Unit,
    onSave: () -> Unit,
    onFocusHandled: () -> Unit,
    onDiscardRequested: () -> Unit,
    onDiscardDismissed: () -> Unit,
    onBackSelected: () -> Unit
) {
    val hasContent = uiState.form.hasContent()

    // Con el formulario vacío no se intercepta nada: el atrás sale directo.
    BackHandler(enabled = hasContent && !uiState.showDiscardDialog) {
        onDiscardRequested()
    }

    val focusRequesters = remember { AddDogField.entries.associateWith { FocusRequester() } }

    LaunchedEffect(uiState.focusTarget) {
        uiState.focusTarget?.let { field ->
            focusRequesters[field]?.requestFocus()
            onFocusHandled()
        }
    }

    if (uiState.showDiscardDialog) {
        AlertDialog(
            onDismissRequest = onDiscardDismissed,
            containerColor = BackgroundComponent,
            title = { Text("¿Descartar el perro?", color = Color.White) },
            text = { Text("Se perderá lo que has escrito.", color = SecondaryText) },
            confirmButton = {
                TextButton(onClick = {
                    onDiscardDismissed()
                    onBackSelected()
                }) {
                    Text("Descartar", color = PrimaryButton)
                }
            },
            dismissButton = {
                TextButton(onClick = onDiscardDismissed) {
                    Text("Seguir editando", color = SecondaryText)
                }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundApp,
        topBar = {
            TopAppBar(
                title = { Text("Añadir perro") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (hasContent) onDiscardRequested() else onBackSelected()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow),
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundApp,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                // Desplazable para que con el teclado abierto y la fuente del
                // sistema al máximo sigan alcanzándose los 7 campos y el botón.
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            FIELDS.forEach { spec ->
                DogFormField(
                    spec = spec,
                    value = uiState.form.valueOf(spec.field),
                    error = uiState.errors[spec.field],
                    focusRequester = focusRequesters.getValue(spec.field),
                    onValueChange = { value -> onFieldChange(spec.field, value) }
                )
                Spacer(Modifier.height(12.dp))
            }

            uiState.saveError?.let { message ->
                Text(message, color = PrimaryButton, modifier = Modifier.testTag(TAG_SAVE_ERROR))
                Spacer(Modifier.height(12.dp))
            }

            Button(
                onClick = onSave,
                enabled = !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TAG_SAVE_BUTTON),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryButton)
            ) {
                Text("Guardar", color = Color.White)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DogFormField(
    spec: FieldSpec,
    value: String,
    error: String?,
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(spec.label) },
            singleLine = spec.singleLine,
            isError = error != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = spec.keyboardType,
                imeAction = if (spec.singleLine) ImeAction.Next else ImeAction.Default
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = SecondaryText,
                cursorColor = ControlColor,
                errorTextColor = Color.White
            )
        )
        if (error != null) {
            // El error va como texto bajo el campo, no solo como color de borde.
            Text(
                text = error,
                color = PrimaryButton,
                modifier = Modifier.testTag(errorTagFor(spec.field))
            )
        }
    }
}

const val TAG_SAVE_BUTTON = "add_dog_save"
const val TAG_SAVE_ERROR = "add_dog_save_error"

fun errorTagFor(field: AddDogField): String = "add_dog_error_${field.name}"
