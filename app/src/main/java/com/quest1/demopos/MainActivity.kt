package com.quest1.demopos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels // <-- Import this
import com.quest1.demopos.ui.navigation.AppNavigation
import com.quest1.demopos.ui.theme.Quest1POSTheme
import com.quest1.demopos.ui.view.SampleViewModel // <-- Import your ViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * This line is the key fix.
     * By getting an instance of the ViewModel here, you trigger Hilt to
     * create the entire chain of dependencies (`ViewModel` -> `UseCase` -> `Repository` -> `Manager`).
     * This will cause the `init` block in `DittoManager` and the log messages
     * in your other classes to execute.
     */
    private val viewModel: SampleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Quest1POSTheme {
                // In a real app, you would pass the ViewModel down to your
                // navigation graph or instantiate it in the specific screen
                // that needs it. For now, this is enough to get it running.
                AppNavigation()
            }
        }
    }
}