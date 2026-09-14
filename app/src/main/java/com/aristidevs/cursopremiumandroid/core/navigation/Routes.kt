package com.aristidevs.cursopremiumandroid.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Dog: NavKey

@Serializable
data class DogDetail(val id:Int): NavKey

@Serializable
data object AddDog: NavKey
