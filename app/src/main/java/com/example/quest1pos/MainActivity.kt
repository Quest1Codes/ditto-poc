package com.example.quest1pos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.quest1pos.ui.navigation.AppNavigation
import com.example.quest1pos.ui.theme.Quest1POSTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Quest1POSTheme {
                AppNavigation()
            }
        }
    }
}
