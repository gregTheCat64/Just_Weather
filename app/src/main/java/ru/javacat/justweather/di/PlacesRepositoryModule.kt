package ru.javacat.justweather.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.javacat.justweather.domain.repos.PlacesRepository
import ru.javacat.justweather.data.impl.PlacesRepositorySharedPrefsImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PlacesRepositoryModule {

    @Singleton
    @Binds
    fun bindsPlacesRepository(impl: PlacesRepositorySharedPrefsImpl): PlacesRepository

}