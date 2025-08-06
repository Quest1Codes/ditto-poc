package com.quest1.demopos.di

import com.quest1.demopos.BuildConfig
import com.quest1.demopos.data.network.PaymentApiService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PaymentModule {


    @Provides
    @Singleton
    @Named("PaymentRetrofit")
    fun providePaymentRetrofit(moshi: Moshi): Retrofit {
        val baseUrl = BuildConfig.PAYMENT_SERVICE_BASE_URL
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }


    @Provides
    @Singleton
    fun providePaymentApiService(@Named("PaymentRetrofit") retrofit: Retrofit): PaymentApiService {
        return retrofit.create(PaymentApiService::class.java)
    }
}
