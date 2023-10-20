package ru.javacat.justweather.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.javacat.justweather.domain.repos.CurrentPlaceRepository
import ru.javacat.justweather.data.impl.CurrentPlaceSharedPrefsImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CurrentPlaceRepositoryModule {

    @Singleton
    @Binds
    fun bindsCurrentPlaceRepository(impl: CurrentPlaceSharedPrefsImpl): CurrentPlaceRepository
}