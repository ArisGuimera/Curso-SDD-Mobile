package com.aristidevs.cursopremiumandroid.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.aristidevs.cursopremiumandroid.presentation.add.AddDogScreen
import com.aristidevs.cursopremiumandroid.presentation.detail.DogDetailScreen
import com.aristidevs.cursopremiumandroid.presentation.list.DogsScreen


@Composable
fun AppNavigation() {

    val backstack = rememberNavBackStack(Dog)

    // La lista y el formulario viven en entradas distintas, con un ViewModel
    // cada una. Este puente deja que el alta avise a la lista para que limpie
    // la búsqueda, sin que cancelar el formulario la limpie también.
    var notifyDogAdded by remember { mutableStateOf({}) }

    NavDisplay(backStack = backstack, entryProvider = entryProvider {
        entry<Dog> {
            DogsScreen(
                onDogClicked = { id -> backstack.add(DogDetail(id)) },
                onAddDogClicked = { onDogAdded ->
                    notifyDogAdded = onDogAdded
                    backstack.add(AddDog)
                }
            )
        }

        entry<DogDetail> { params ->
            DogDetailScreen(id = params.id, onBackSelected = { backstack.removeLastOrNull() })
        }

        entry<AddDog> {
            AddDogScreen(
                onDogSaved = {
                    notifyDogAdded()
                    backstack.removeLastOrNull()
                },
                onBackSelected = { backstack.removeLastOrNull() }
            )
        }
    })
}
