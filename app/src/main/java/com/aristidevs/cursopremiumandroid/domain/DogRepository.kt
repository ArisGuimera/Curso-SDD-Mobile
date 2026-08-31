package com.aristidevs.cursopremiumandroid.domain

import com.aristidevs.cursopremiumandroid.domain.model.Dog
import com.aristidevs.cursopremiumandroid.domain.model.DogDetailModel

interface DogRepository {
    suspend fun getDogs(): List<Dog>
    suspend fun getDogDetail(id: Int): DogDetailModel
}