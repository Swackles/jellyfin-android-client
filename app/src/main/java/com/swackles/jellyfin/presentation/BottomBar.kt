package com.swackles.jellyfin.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import com.swackles.jellyfin.presentation.destinations.DashboardScreenDestination
import com.swackles.jellyfin.presentation.destinations.Destination
import com.swackles.jellyfin.presentation.destinations.PlayerScreenDestination
import com.swackles.jellyfin.presentation.destinations.SearchScreenDestination
import com.swackles.jellyfin.presentation.destinations.SettingsScreenDestination

private enum class BottomBarDestinations(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    val label: String
) {
    Greeting(DashboardScreenDestination, Icons.Default.Home, "Home"),
    Search(SearchScreenDestination, Icons.Default.Search, "Search"),
    Settings(SettingsScreenDestination, Icons.Default.Settings, "Settings")
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