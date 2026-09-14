package com.aristidevs.cursopremiumandroid.data

import com.aristidevs.cursopremiumandroid.data.api.DogApiServices
import com.aristidevs.cursopremiumandroid.data.db.DogDao
import com.aristidevs.cursopremiumandroid.data.mapper.toCatalogEntity
import com.aristidevs.cursopremiumandroid.data.mapper.toDomain
import com.aristidevs.cursopremiumandroid.data.mapper.toDomainDetail
import com.aristidevs.cursopremiumandroid.data.mapper.toEntity
import com.aristidevs.cursopremiumandroid.domain.DogNotFoundException
import com.aristidevs.cursopremiumandroid.domain.DogRepository
import com.aristidevs.cursopremiumandroid.domain.model.Dog
import com.aristidevs.cursopremiumandroid.domain.model.DogDetailModel
import com.aristidevs.cursopremiumandroid.domain.model.NewDog
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DogRepositoryImpl @Inject constructor(
    private val api: DogApiServices,
    private val dao: DogDao
) : DogRepository {

    override fun getDogs(): Flow<List<Dog>> {
        return dao.observeDogs().map { entities -> entities.map { entity -> entity.toDomain() } }
    }

    override suspend fun seedCatalogIfNeeded() {
        if (dao.countCatalogDogs() > 0) return
        val catalog = api.getDogs().map { response -> response.toDomain().toCatalogEntity() }
        dao.insertCatalog(catalog)
    }

    override suspend fun getDogDetail(id: Int): DogDetailModel {
        val cached = dao.getDogById(id) ?: throw DogNotFoundException(id)
        cached.toDomainDetail()?.let { return it }

        // Solo llega aquí un perro del catálogo cuyo detalle nunca se ha abierto:
        // los creados por el usuario tienen los tres campos rellenos desde el alta.
        val remote = api.getDogDetail(id)
        dao.updateDetail(id, remote.weight, remote.origin, remote.temperament)
        return dao.getDogById(id)?.toDomainDetail() ?: throw DogNotFoundException(id)
    }

    override suspend fun addDog(dog: NewDog) {
        dao.insertDog(dog.toEntity())
    }
}
