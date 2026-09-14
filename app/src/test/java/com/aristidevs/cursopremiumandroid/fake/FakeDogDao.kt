package com.aristidevs.cursopremiumandroid.fake

import com.aristidevs.cursopremiumandroid.data.db.DogDao
import com.aristidevs.cursopremiumandroid.data.db.entity.DogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * AVISO: este doble reimplementa en Kotlin el ORDER BY del DAO real. Si la
 * sentencia SQL cambia, este fake puede seguir en verde mintiendo. El orden se
 * da por válido en DogDaoTest, que corre contra Room de verdad.
 */
class FakeDogDao : DogDao {

    private val dogs = MutableStateFlow<List<DogEntity>>(emptyList())

    var failInsertDog = false

    val current: List<DogEntity> get() = dogs.value

    override fun observeDogs(): Flow<List<DogEntity>> = dogs.map { list ->
        list.sortedWith(
            compareByDescending<DogEntity> { it.isUserCreated }
                .thenBy { if (it.isUserCreated) -it.id else it.id }
        )
    }

    override suspend fun getDogById(id: Int): DogEntity? = dogs.value.find { it.id == id }

    override suspend fun countCatalogDogs(): Int = dogs.value.count { !it.isUserCreated }

    override suspend fun insertCatalog(dogs: List<DogEntity>) {
        this.dogs.value = this.dogs.value + dogs
    }

    override suspend fun insertDog(dog: DogEntity): Long {
        if (failInsertDog) throw IOException("no se pudo escribir")
        val id = if (dog.id == 0) (dogs.value.maxOfOrNull { it.id } ?: 0) + 1 else dog.id
        dogs.value = dogs.value + dog.copy(id = id)
        return id.toLong()
    }

    override suspend fun updateDetail(
        id: Int,
        weight: String,
        origin: String,
        temperament: String
    ) {
        dogs.value = dogs.value.map { dog ->
            if (dog.id == id) {
                dog.copy(weight = weight, origin = origin, temperament = temperament)
            } else {
                dog
            }
        }
    }
}
