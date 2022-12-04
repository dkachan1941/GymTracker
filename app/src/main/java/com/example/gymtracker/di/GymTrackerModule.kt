package com.example.gymtracker.di

import android.content.Context
import androidx.room.Room
import com.example.gymtracker.repository.GymTrackerRepository
import com.example.gymtracker.repository.GymTrackerRepositoryImpl
import com.example.gymtracker.room.AppDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DB_NAME = "DB_GYM_TRACKER"

@Module
@InstallIn(SingletonComponent::class)
interface GymTrackerModule {

    companion object {
        @Singleton
        @Provides
        fun provideDB(@ApplicationContext appContext: Context): AppDatabase =
            Room.databaseBuilder(
                appContext,
                AppDatabase::class.java, DB_NAME
            ).build()
    }

    @Binds
    fun bindGymTrackerRepository(gymTrackerRepository: GymTrackerRepositoryImpl): GymTrackerRepository
}