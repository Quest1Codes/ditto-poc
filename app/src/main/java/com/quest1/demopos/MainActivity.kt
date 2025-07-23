// File: app/src/main/java/com/quest1/demopos/MainActivity.kt

package com.quest1.demopos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.quest1.demopos.ui.navigation.AppNavigation
import com.quest1.demopos.ui.theme.Quest1POSTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import live.ditto.ditto_wrapper.DittoManager
import live.ditto.transports.DittoSyncPermissions

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dittoManager: DittoManager

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            dittoManager.requireDitto().refreshPermissions()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestDittoPermissions()

        setContent {
            Quest1POSTheme {
                AppNavigation()
            }
        }
    }

    private fun requestDittoPermissions() {
        val missing = DittoSyncPermissions(this).missingPermissions()
        if (missing.isNotEmpty()) {
            requestPermissionLauncher.launch(missing)
        }
    }
}