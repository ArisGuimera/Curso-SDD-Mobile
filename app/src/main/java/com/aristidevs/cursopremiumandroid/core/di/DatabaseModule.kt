package com.aristidevs.cursopremiumandroid.core.di

import android.content.Context
import androidx.room.Room
import com.aristidevs.cursopremiumandroid.data.db.DogDao
import com.aristidevs.cursopremiumandroid.data.db.DogDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "dogs.db"

    @Provides
    @Singleton
    fun providesDogDatabase(@ApplicationContext context: Context): DogDatabase {
        return Room.databaseBuilder(context, DogDatabase::class.java, DATABASE_NAME).build()
    }

    @Provides
    @Singleton
    fun providesDogDao(database: DogDatabase): DogDao {
        return database.dogDao()
    }
}
