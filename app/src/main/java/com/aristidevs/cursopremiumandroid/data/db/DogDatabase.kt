package com.aristidevs.cursopremiumandroid.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aristidevs.cursopremiumandroid.data.db.entity.DogEntity

@Database(entities = [DogEntity::class], version = 1, exportSchema = false)
abstract class DogDatabase : RoomDatabase() {
    abstract fun dogDao(): DogDao
}
