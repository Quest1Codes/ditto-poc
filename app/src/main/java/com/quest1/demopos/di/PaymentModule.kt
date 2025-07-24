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

    /**
     * Provides a dedicated Retrofit instance for the Payment Service.
     * This is separate from the one used for authentication to avoid conflicts.
     * It connects to the payment server on port 5002.
     */
    @Provides
    @Singleton
    @Named("PaymentRetrofit") // Use a name to distinguish from the auth Retrofit instance
    fun providePaymentRetrofit(moshi: Moshi): Retrofit {
        // This URL points to your new payment server running on port 5002.
        val baseUrl = BuildConfig.PAYMENT_SERVICE_BASE_URL
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * Provides the PaymentApiService using the dedicated payment Retrofit instance.
     */
    @Provides
    @Singleton
    fun providePaymentApiService(@Named("PaymentRetrofit") retrofit: Retrofit): PaymentApiService {
        return retrofit.create(PaymentApiService::class.java)
    }
}
