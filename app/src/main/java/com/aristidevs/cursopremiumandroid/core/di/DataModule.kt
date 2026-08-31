package com.aristidevs.cursopremiumandroid.core.di

import com.aristidevs.cursopremiumandroid.core.di.DogApiConfig.BASE_URL
import com.aristidevs.cursopremiumandroid.data.DogRepositoryImpl
import com.aristidevs.cursopremiumandroid.data.api.DogApiServices
import com.aristidevs.cursopremiumandroid.domain.DogRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun providesJson(): Json{
        return Json{
            ignoreUnknownKeys = true
        }
    }

    @Provides
    @Singleton
    fun providesRetrofit(json: Json): Retrofit{
        return Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(
            json.asConverterFactory("application/json".toMediaType())
        ).build()
    }

    @Provides
    @Singleton
    fun providesDogApiServices(retrofit: Retrofit):DogApiServices{
        return retrofit.create(DogApiServices::class.java)
    }

    @Provides
    @Singleton
    fun provideDogRepository(api: DogApiServices): DogRepository{
        return DogRepositoryImpl(api)
    }
}









