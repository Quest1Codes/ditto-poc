package com.quest1.demopos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.quest1.demopos.ui.navigation.AppNavigation
import com.quest1.demopos.ui.theme.Quest1POSTheme
import dagger.hilt.android.AndroidEntryPoint
import live.ditto.transports.DittoSyncPermissions

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Quest1POSTheme {
                AppNavigation()
            }
        }

        requestPermissions();
    }

    fun requestPermissions() {
        val missing = DittoSyncPermissions(this).missingPermissions()
        if (missing.isNotEmpty()) {
            this.requestPermissions(missing, 0)
        }
    }
}
