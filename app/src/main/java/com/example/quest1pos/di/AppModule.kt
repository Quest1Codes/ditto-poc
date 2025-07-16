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
        val dittoAppId = "ba6c2ddb-1c8f-4cf2-ae0d-35175ecc1428"
        val dittoToken = "2f88e0dd-ff1a-4abf-a63e-b92c7e9936e7"
        val dittoAuthUrl = "https://i83inp.cloud.dittolive.app"

        return DittoManager(
            context = context,
            dittoAppId = dittoAppId,
            dittoToken = dittoToken,
            dittoAuthUrl=dittoAuthUrl
        )
    }
}
