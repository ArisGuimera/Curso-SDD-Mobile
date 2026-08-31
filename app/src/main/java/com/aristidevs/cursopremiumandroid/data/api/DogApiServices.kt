package com.aristidevs.cursopremiumandroid.data.api

import com.aristidevs.cursopremiumandroid.data.api.response.DogDetailResponse
import com.aristidevs.cursopremiumandroid.data.api.response.DogResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface DogApiServices {

    @GET("dogs.json")
    suspend fun getDogs():List<DogResponse>

    @GET("details/{id}.json")
    suspend fun getDogDetail(@Path("id") id:Int): DogDetailResponse
}