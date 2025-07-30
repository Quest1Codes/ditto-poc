package com.quest1.demopos.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.quest1.demopos.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import live.ditto.ditto_wrapper.DittoManager
import live.ditto.ditto_wrapper.DittoStoreManager
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

    @Provides
    @Singleton
    fun provideMasterKeyAlias(): String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    @Provides
    @Singleton
    fun provideEncryptedSharedPreferences(
        @ApplicationContext context: Context,
        masterKeyAlias: String
    ): SharedPreferences {
        return EncryptedSharedPreferences.create(
            "auth_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    @Singleton
    fun provideDittoStoreManager(dittoManager: DittoManager): DittoStoreManager {
        return DittoStoreManager(dittoManager.requireDitto())
    }
}
