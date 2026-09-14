package com.aristidevs.cursopremiumandroid.fake

import com.aristidevs.cursopremiumandroid.domain.DogRepository
import com.aristidevs.cursopremiumandroid.domain.model.Dog
import com.aristidevs.cursopremiumandroid.domain.model.DogDetailModel
import com.aristidevs.cursopremiumandroid.domain.model.NewDog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.IOException

class FakeDogRepository : DogRepository {

    private val dogs = MutableStateFlow<List<Dog>>(emptyList())

    var failSeed = false
    var failAdd = false
    var seedCalls = 0
        private set
    val added = mutableListOf<NewDog>()

    var catalog: List<Dog> = listOf(
        Dog(1, "Luna", 3, "Cariñosa", "url/luna.jpg", "Golden Retriever"),
        Dog(2, "Max", 5, "Juguetón", "url/max.jpg", "Labrador Retriever")
    )

    override fun getDogs(): Flow<List<Dog>> = dogs

    override suspend fun seedCatalogIfNeeded() {
        seedCalls++
        if (failSeed) throw IOException("sin conexión")
        if (dogs.value.none { !it.isUserCreated }) {
            dogs.value = dogs.value + catalog
        }
    }

    override suspend fun getDogDetail(id: Int): DogDetailModel = error("no usado en estos tests")

    override suspend fun addDog(dog: NewDog) {
        if (failAdd) throw IOException("no se pudo escribir")
        added += dog
        val id = (dogs.value.maxOfOrNull { it.id } ?: 0) + 1
        // Los creados se ordenan antes del catálogo, del más reciente al más antiguo.
        dogs.value = listOf(
            Dog(id, dog.name, dog.age, dog.description, "", dog.breed, isUserCreated = true)
        ) + dogs.value
    }
}
