package ru.javacat.justweather.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.javacat.justweather.domain.repos.Repository
import ru.javacat.justweather.data.impl.RepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindsRepository(impl: RepositoryImpl): Repository
}