package live.ditto.ditto_wrapper

import android.content.Context
import android.util.Log
import live.ditto.Ditto
import live.ditto.DittoAuthenticator
import live.ditto.DittoError
import live.ditto.DittoIdentity
import live.ditto.DittoLogLevel
import live.ditto.DittoLogger
import live.ditto.android.DefaultAndroidDittoDependencies
import live.ditto.DittoAuthenticationCallback
class DittoManager(
    val context: Context,
    // We are going back to the simple online playground setup
    private val dittoAppId: String,
    private val dittoAuthUrl: String,
    private val dittoWsUrl: String
) {
    private var ditto: Ditto? = null
    private val TAG = "DittoManager"
    private var authenticator: DittoAuthenticator? = null

    // Callback handler for Ditto authentication events
    inner class AuthCallback : DittoAuthenticationCallback {
        override fun authenticationRequired(authenticator: DittoAuthenticator) {
            Log.d(TAG, "Ditto authentication required.")
            this@DittoManager.authenticator = authenticator
        }

        override fun authenticationExpiringSoon(
            authenticator: DittoAuthenticator,
            secondsRemaining: Long
        ) {
            Log.d(TAG, "Ditto token expiring in $secondsRemaining seconds. A new login will be required.")
            this@DittoManager.authenticator = authenticator
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
                enableDittoCloudSync = true,
                callback = AuthCallback()
            )

            ditto = Ditto(androidDependencies, identity)
            ditto?.smallPeerInfo?.isEnabled = true
            ditto?.transportConfig?.connect?.websocketUrls?.add(dittoWsUrl)

            ditto?.startSync()

            Log.d(TAG, "Ditto ONLINE WITH AUTHENTICATION initialization complete and sync started.")

        } catch (e: DittoError) {
            Log.e(TAG, "A DittoError occurred during initialization: ${e.message}")
            ditto = null
        } catch (e: Exception) {
            Log.e(TAG, "An unexpected error occurred during Ditto initialization: ${e.message}")
            ditto = null
        }
    }

    suspend fun loginWithToken(token: String) {
        authenticator?.let {
            try {
                // Use the authenticator provided by the callback to log in
                it.login(token, "auth-webhook") { _, err ->
                    if (err != null) {
                        Log.e(TAG, "Ditto login failed: ${err.message}")
                    } else {
                        Log.d(TAG, "Ditto login request completed successfully.")
                    }
                }
            } catch (e: DittoError) {
                Log.e(TAG, "Ditto login failed: ${e.message}", e)
            }
        } ?: Log.e(TAG, "Authenticator not available. Cannot log in to Ditto.")
    }
    fun requireDitto(): Ditto {
        return ditto ?: throw DittoNotCreatedException()
    }
}

class DittoNotCreatedException : Throwable("Ditto object could not be created. Check logs for errors.")