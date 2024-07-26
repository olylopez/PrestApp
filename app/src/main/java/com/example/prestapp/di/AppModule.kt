package com.example.prestapp.di

import android.content.Context
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

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    /*@Provides
    @Singleton
    fun providesServicioDatabase(@ApplicationContext appContext: Context): ServicioDb =
        Room.databaseBuilder(
            appContext,
            ServicioDb::class.java,
            "Prestamo.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun providesServicioDao(db: ServicioDb) = db.servicioDao()*/

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


}