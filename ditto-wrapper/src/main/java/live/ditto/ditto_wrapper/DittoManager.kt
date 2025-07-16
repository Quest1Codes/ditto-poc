package live.ditto.ditto_wrapper

import android.content.Context
import android.util.Log
import live.ditto.Ditto
import live.ditto.DittoError
import live.ditto.DittoIdentity
import live.ditto.DittoLogLevel
import live.ditto.DittoLogger
import live.ditto.android.DefaultAndroidDittoDependencies

class DittoManager(
    val context: Context,
    // We are going back to the simple online playground setup
    private val dittoAppId: String,
    private val dittoToken: String,
    private val dittoAuthUrl: String
) {
    private var ditto: Ditto? = null
    private val TAG = "DittoManager"

    init {
        try {
            Log.d(TAG, "Attempting to initialize Ditto in ONLINE mode.")
            Log.d(TAG, "Using App ID: $dittoAppId")
            DittoLogger.minimumLogLevel = DittoLogLevel.DEBUG

            val androidDependencies = DefaultAndroidDittoDependencies(context)

            // Using the standard OnlinePlayground identity. This is the correct
            // method for the credentials provided by the Ditto Portal.
            val identity = DittoIdentity.OnlinePlayground(
                dependencies = androidDependencies,
                appId = dittoAppId,
                token = dittoToken,
                customAuthUrl = dittoAuthUrl,
                enableDittoCloudSync = true
            )

            ditto = Ditto(androidDependencies, identity)
            ditto?.smallPeerInfo?.isEnabled = true


            ditto?.startSync()

            Log.d(TAG, "Ditto ONLINE initialization complete and sync started.")

        } catch (e: DittoError) {
            Log.e(TAG, "A DittoError occurred during initialization: ${e.message}")
            ditto = null
        } catch (e: Exception) {
            Log.e(TAG, "An unexpected error occurred during Ditto initialization: ${e.message}")
            ditto = null
        }
    }

    fun requireDitto(): Ditto {
        return ditto ?: throw DittoNotCreatedException()
    }
}

class DittoNotCreatedException : Throwable("Ditto object could not be created. Check logs for errors.")
