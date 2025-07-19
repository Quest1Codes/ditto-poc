package com.quest1.demopos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.quest1.demopos.ui.navigation.AppNavigation
import com.quest1.demopos.ui.theme.Quest1POSTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Quest1POSTheme {
                // This is the only line that needs to be changed.
                // It should call AppNavigation() to start your main app flow.
                AppNavigation()
            }
        }
    }
}
