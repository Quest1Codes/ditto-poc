package com.quest1.demopos.di

import android.content.Context
// ADDED the missing import for DittoRepository
import com.quest1.demopos.data.repository.DittoRepository
import com.quest1.demopos.BuildConfig
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
    fun provideDittoManager(@ApplicationContext context: Context): DittoManager {
        val dittoAppId = BuildConfig.DITTO_APP_ID
        val dittoAuthUrl = BuildConfig.DITTO_AUTH_URL
        val dittoWsUrl = BuildConfig.DITTO_WS_URL
        return DittoManager(
            context = context,
            dittoAppId = dittoAppId,
            dittoAuthUrl= dittoAuthUrl,
            dittoWsUrl = dittoWsUrl

        )
    }

}