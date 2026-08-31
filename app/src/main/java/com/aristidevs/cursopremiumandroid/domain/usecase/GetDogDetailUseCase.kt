package com.aristidevs.cursopremiumandroid.domain.usecase

import com.aristidevs.cursopremiumandroid.domain.DogRepository
import com.aristidevs.cursopremiumandroid.domain.model.DogDetailModel
import javax.inject.Inject

class GetDogDetailUseCase @Inject constructor(val repository: DogRepository) {

    suspend operator fun invoke(id:Int): DogDetailModel{
        return repository.getDogDetail(id)
    }
}