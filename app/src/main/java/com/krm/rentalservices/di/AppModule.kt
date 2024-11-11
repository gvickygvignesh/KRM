package com.krm.rentalservices.di

import com.krm.rentalservices.Constants
import com.krm.rentalservices.data.GoogleSheetApi
import com.krm.rentalservices.data.IGoogleSheetRepo
import com.krm.rentalservices.data.GoogleSheetRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Singleton
    @Provides
    fun invRepo(api: GoogleSheetApi) = GoogleSheetRepo(api) as IGoogleSheetRepo

    @Singleton
    @Provides
    fun injectBackendRetrofitApi(): GoogleSheetApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.BASEURL)
            .build()
            .create(GoogleSheetApi::class.java)
    }
}