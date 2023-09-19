package ru.javacat.justweather.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.javacat.justweather.repository.CurrentPlaceRepository
import ru.javacat.justweather.repository.CurrentPlaceSharedPrefsImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CurrentPlaceRepositoryModule {

    @Singleton
    @Binds
    fun bindsCurrentPlaceRepository(impl: CurrentPlaceSharedPrefsImpl): CurrentPlaceRepository
}