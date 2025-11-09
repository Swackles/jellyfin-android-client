package com.swackles.jellyfin.presentation

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.AuthScreenDestination
import com.ramcosta.composedestinations.generated.destinations.DashboardScreenDestination
import com.swackles.jellyfin.session.AuthState
import com.swackles.jellyfin.session.SessionEvent
import com.swackles.jellyfin.session.SessionManager

@Composable
fun JellyfinApp(
    navController: NavHostController = rememberNavController(),
    sessionManager: SessionManager,
) {
    val authState by sessionManager.authState.collectAsState()

    if (authState is AuthState.Loading) return

    LaunchedEffect(Unit) {
        sessionManager.events.collect { event ->
            Log.d("JellyfinApp", "caught session event $event")

            when(event) {
                is SessionEvent.Authenticated ->
                    navController.navigate(DashboardScreenDestination.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                is SessionEvent.LoggedOut ->
                    navController.navigate(AuthScreenDestination.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
            }
        }
    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController) }
    ) {
        DestinationsNavHost(
            navController = navController,
            navGraph = NavGraphs.root,
            modifier = Modifier.padding(it).fillMaxSize(),
            start =  when(authState) {
                is AuthState.Authenticated -> DashboardScreenDestination
                else -> AuthScreenDestination()
            }
        )
    }
}