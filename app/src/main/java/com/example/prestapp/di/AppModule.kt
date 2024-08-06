package com.example.prestapp.di

import android.content.Context
import androidx.room.Room
import com.example.prestapp.ConnectivityReceiver
import com.example.prestapp.data.local.dao.RutaDao
import com.example.prestapp.data.local.database.PrestAppDb
import com.example.prestapp.data.remote.PrestAppApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun providesPrestamoApi(moshi: Moshi): PrestAppApi {
        return Retrofit.Builder()
            .baseUrl("https://prestappservice.azurewebsites.net/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(PrestAppApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): PrestAppDb {
        return Room.databaseBuilder(
            appContext,
            PrestAppDb::class.java,
            "prestapp.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideRutaDao(database: PrestAppDb): RutaDao {
        return database.rutaDao()
    }

    @Provides
    @Singleton
    fun provideConnectivityReceiver(@ApplicationContext context: Context): ConnectivityReceiver {
        return ConnectivityReceiver(context)
    }
}