package com.cartrack.challenge.dagger.hilt

import com.cartrack.challenge.BuildConfig
import com.cartrack.challenge.api.ApiCore
import com.cartrack.challenge.api.common.scheduler.AppSchedulerProvider
import com.cartrack.challenge.api.common.scheduler.SchedulerProvider
import com.cartrack.challenge.api.crimes.service.CrimeDataService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideRetrofit() = ApiCore.getRetrofit(BuildConfig.API_URL)

    @Singleton
    @Provides
    fun provideScheduler():SchedulerProvider = AppSchedulerProvider()



    @Singleton
    @Provides
    fun provideCrimeDataService(retrofit : Retrofit):CrimeDataService
            = retrofit.create(CrimeDataService::class.java)
}