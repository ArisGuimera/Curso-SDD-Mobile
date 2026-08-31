package com.aristidevs.cursopremiumandroid.data

import com.aristidevs.cursopremiumandroid.data.api.DogApiServices
import com.aristidevs.cursopremiumandroid.data.mapper.toDomain
import com.aristidevs.cursopremiumandroid.domain.DogRepository
import com.aristidevs.cursopremiumandroid.domain.model.Dog
import com.aristidevs.cursopremiumandroid.domain.model.DogDetailModel
import jakarta.inject.Inject

class DogRepositoryImpl @Inject constructor(val api: DogApiServices): DogRepository {

    override suspend fun getDogs(): List<Dog> {
        return api.getDogs().map { dogResponse ->  dogResponse.toDomain()}
    }

    override suspend fun getDogDetail(id: Int): DogDetailModel {
        return api.getDogDetail(id).toDomain()
    }
}