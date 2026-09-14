package com.aristidevs.cursopremiumandroid.domain

import com.aristidevs.cursopremiumandroid.domain.model.Dog
import com.aristidevs.cursopremiumandroid.domain.model.DogDetailModel
import com.aristidevs.cursopremiumandroid.domain.model.NewDog
import kotlinx.coroutines.flow.Flow

interface DogRepository {

    /** Lista local completa. Emite de nuevo cada vez que cambia la base de datos. */
    fun getDogs(): Flow<List<Dog>>

    /** Descarga el catálogo la primera vez. No hace nada si ya está sembrado. */
    suspend fun seedCatalogIfNeeded()

    /** Lee el detalle de la caché local y solo va a la red si aún no está guardado. */
    suspend fun getDogDetail(id: Int): DogDetailModel

    suspend fun addDog(dog: NewDog)
}
