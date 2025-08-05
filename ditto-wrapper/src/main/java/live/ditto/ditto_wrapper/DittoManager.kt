package live.ditto.ditto_wrapper

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import live.ditto.Ditto
import live.ditto.DittoSmallPeerInfoSyncScope
import live.ditto.DittoAuthenticator
import live.ditto.DittoError
import live.ditto.DittoIdentity
import live.ditto.DittoLogLevel
import live.ditto.DittoLogger
import live.ditto.android.DefaultAndroidDittoDependencies
import live.ditto.DittoAuthenticationCallback

class DittoManager(
    val context: Context,
    private val dittoAppId: String,
    private val dittoAuthUrl: String,
    private val dittoWsUrl: String
) {
    private var ditto: Ditto? = null
    private val TAG = "DittoManager"
    private var authenticator: DittoAuthenticator? = null

    private val _isAuthenticationRequired = MutableStateFlow(false)
    val isAuthenticationRequired = _isAuthenticationRequired.asStateFlow()

    inner class AuthCallback : DittoAuthenticationCallback {
        override fun authenticationRequired(authenticator: DittoAuthenticator) {
            Log.d(TAG, "Ditto authentication required.")
            this@DittoManager.authenticator = authenticator
            _isAuthenticationRequired.value = true
        }

        override fun authenticationExpiringSoon(
            authenticator: DittoAuthenticator,
            secondsRemaining: Long
        ) {
            Log.d(TAG, "Ditto token expiring in $secondsRemaining seconds.")
            this@DittoManager.authenticator = authenticator
            _isAuthenticationRequired.value = true
        }
    }

    init {
        try {
            Log.d(TAG, "Attempting to initialize Ditto in ONLINE WITH AUTHENTICATION mode.")
            Log.d(TAG, "Using App ID: $dittoAppId")
            DittoLogger.minimumLogLevel = DittoLogLevel.DEBUG

            val androidDependencies = DefaultAndroidDittoDependencies(context)

            val identity = DittoIdentity.OnlineWithAuthentication(
                dependencies = androidDependencies,
                appId = dittoAppId,
                customAuthUrl = dittoAuthUrl,
                enableDittoCloudSync = false,
                callback = AuthCallback()
            )

            ditto = Ditto(androidDependencies, identity)
            ditto?.smallPeerInfo?.isEnabled = true
            ditto?.disableSyncWithV3()
            ditto?.updateTransportConfig { config ->
                config.connect.websocketUrls.add(dittoWsUrl)
                config.enableAllPeerToPeer()
            }



            Log.d(TAG, "Ditto ONLINE WITH AUTHENTICATION initialization complete and sync started.")

        } catch (e: DittoError) {
            Log.e(TAG, "A DittoError occurred during initialization: ${e.message}")
            ditto = null
        } catch (e: Exception) {
            Log.e(TAG, "An unexpected error occurred during Ditto initialization: ${e.message}")
            ditto = null
        }
    }

    fun provideTokenToAuthenticator(token: String) {
        ditto?.let {
            try {
                ditto?.auth?.login(token, "auth-webhook") { _, err ->
                    if (err != null) {
                        Log.e(TAG, "Ditto login failed: ${err.message}")
                    } else {
                        Log.d(TAG, "Ditto login request completed successfully.")
                        _isAuthenticationRequired.value = false
                        ditto?.startSync()
                    }
                }
            } catch (e: DittoError) {
                Log.e(TAG, "Ditto login failed: ${e.message}", e)
            }
        } ?: Log.e(TAG, "Authenticator not available. ProvideToken called at the wrong time.")
    }
    fun requireDitto(): Ditto {
        return ditto ?: throw DittoNotCreatedException()
    }

    fun logout() {
        ditto?.auth?.logout()
        ditto?.stopSync()
        _isAuthenticationRequired.value = true;

    }
}

class DittoNotCreatedException : Throwable("Ditto object could not be created. Check logs for errors.")