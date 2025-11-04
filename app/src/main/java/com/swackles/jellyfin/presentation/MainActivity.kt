package com.swackles.jellyfin.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.swackles.jellyfin.presentation.common.theme.JellyfinTheme
import com.swackles.jellyfin.session.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            sessionManager.initialize()
        }

        setContent {
            JellyfinTheme {
                JellyfinApp(sessionManager = sessionManager)
            }
        }
    }
}
