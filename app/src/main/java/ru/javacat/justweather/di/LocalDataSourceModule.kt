package ru.javacat.justweather.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.javacat.justweather.data.db.AppDb
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDataSourceModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDb {
        val db = Room.databaseBuilder(context, AppDb::class.java, "app.db").build()
//        db.openHelper.readableDatabase.query(
//            """
//                CREATE TRIGGER IF NOT EXISTS delete_old AFTER INSERT ON forecast_days_table
//                BEGIN
//                DELETE FROM forecast_days_table
//                WHERE date <
//            """
//        )
        return db
    }
}