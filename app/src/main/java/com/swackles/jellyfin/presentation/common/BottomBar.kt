package com.swackles.jellyfin.presentation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import com.swackles.jellyfin.presentation.NavGraphs
import com.swackles.jellyfin.presentation.appCurrentDestinationAsState
import com.swackles.jellyfin.presentation.destinations.DashboardScreenDestination
import com.swackles.jellyfin.presentation.destinations.Destination
import com.swackles.jellyfin.presentation.destinations.PlayerScreenDestination
import com.swackles.jellyfin.presentation.startAppDestination

private enum class BottomBarDestinations(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    val label: String
) {
    Greeting(DashboardScreenDestination, Icons.Default.Home, "Home")
}


@Composable
fun BottomBar(navController: NavController) {
    val currentDestination: Destination = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startAppDestination

    if (BottomBarConstants.ignoredPaths.contains(currentDestination)) return

    NavigationBar {
        BottomBarDestinations.values().forEach { destination ->
            NavigationBarItem(
                selected = currentDestination == destination.direction,
                onClick = { navController.navigate(destination.direction) { launchSingleTop = true } },
                icon = { Icon(destination.icon, contentDescription = destination.label)},
                label = { Text(destination.label) },
            )
        }
    }
}

private object BottomBarConstants {
    val ignoredPaths = listOf(PlayerScreenDestination)
}