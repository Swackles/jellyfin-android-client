package com.swackles.jellyfin.presentation

import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.swackles.jellyfin.presentation.styles.JellyfinTheme
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

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectNetwork()
                .penaltyLog()
                .build()
        )

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
