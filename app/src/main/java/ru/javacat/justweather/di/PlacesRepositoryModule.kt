package ru.javacat.justweather.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.javacat.justweather.repository.PlacesRepository
import ru.javacat.justweather.repository.PlacesRepositorySharedPrefsImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PlacesRepositoryModule {

    @Singleton
    @Binds
    fun bindsPlacesRepository(impl: PlacesRepositorySharedPrefsImpl): PlacesRepository

}