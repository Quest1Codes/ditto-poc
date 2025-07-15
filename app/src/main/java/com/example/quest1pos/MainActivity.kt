package com.example.quest1pos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import com.example.quest1pos.ui.theme.Quest1POSTheme

import com.example.quest1pos.ui.view.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Quest1POSTheme {
                MainScreen()
            }
        }
    }
}