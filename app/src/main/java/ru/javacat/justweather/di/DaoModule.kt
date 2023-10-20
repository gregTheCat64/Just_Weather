package ru.javacat.justweather.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.javacat.justweather.data.db.AppDb
import ru.javacat.justweather.data.db.dao.WeatherDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    @Singleton
    fun providesDao(db: AppDb) : WeatherDao = db.weatherDao
}