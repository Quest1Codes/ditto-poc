package com.example.quest1pos.di

import android.content.Context
import com.example.quest1pos.data.repository.SampleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import live.ditto.ditto_wrapper.DittoManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSampleRepository(dittoManager: DittoManager): SampleRepository {
        return SampleRepository(dittoManager)
    }
// In AppModule.kt

    @Provides
    @Singleton
    fun provideDittoManager(@ApplicationContext context: Context): DittoManager {
        // To resolve the authentication error, you must use a fresh App ID and Token
        // from a brand new app created in the Ditto Portal.
        val dittoAppId = "Add-your-app-id"
        val dittoToken = "Add-your-token"
        val dittoAuthUrl = "Add-your-auth-url"

        return DittoManager(
            context = context,
            dittoAppId = dittoAppId,
            dittoToken = dittoToken,
            dittoAuthUrl=dittoAuthUrl
        )
    }
}
