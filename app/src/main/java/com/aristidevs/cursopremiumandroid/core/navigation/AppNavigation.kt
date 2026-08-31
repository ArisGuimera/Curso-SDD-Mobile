package com.aristidevs.cursopremiumandroid.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.aristidevs.cursopremiumandroid.presentation.detail.DogDetailScreen
import com.aristidevs.cursopremiumandroid.presentation.list.DogsScreen


@Composable
fun AppNavigation() {

    val backstack = rememberNavBackStack(Dog)

    NavDisplay(backStack = backstack, entryProvider = entryProvider {
        entry<Dog> {
            DogsScreen(onDogClicked = { id ->
                backstack.add(DogDetail(id))
            })
        }

        entry<DogDetail> { params ->
            DogDetailScreen(id = params.id, onBackSelected = { backstack.removeLastOrNull() })
        }
    })
}