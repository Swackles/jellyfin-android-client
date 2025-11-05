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
import com.swackles.jellyfin.presentation.screens.appCurrentDestinationAsState
import com.swackles.jellyfin.presentation.screens.destinations.AuthScreenDestination
import com.swackles.jellyfin.presentation.screens.destinations.DashboardScreenDestination
import com.swackles.jellyfin.presentation.screens.destinations.SearchScreenDestination
import com.swackles.jellyfin.presentation.screens.destinations.SettingsHomeScreenDestination
import com.swackles.jellyfin.presentation.screens.destinations.TypedDestination

private enum class BottomBarDestinations(
    val direction: TypedDestination<*>,
    val icon: ImageVector,
    val label: String
) {
    Greeting(DashboardScreenDestination, Icons.Default.Home, "Home"),
    Search(SearchScreenDestination, Icons.Default.Search, "Search"),
    Settings(SettingsHomeScreenDestination, Icons.Default.Settings, "Settings")
}


@Composable
fun BottomBar(navController: NavController) {
    val currentDestination = navController.appCurrentDestinationAsState().value

    if (BottomBarConstants.IGNORED_PATHS.contains(currentDestination)) return

    NavigationBar {
        BottomBarDestinations.entries.forEach { destination ->
            NavigationBarItem(
                selected = currentDestination == destination.direction,
                onClick = { navController.navigate(destination.direction.route) { launchSingleTop = true } },
                icon = { Icon(destination.icon, contentDescription = destination.label)},
                label = { Text(destination.label) },
            )
        }
    }
}

private object BottomBarConstants {
    val IGNORED_PATHS = listOf<TypedDestination<*>>(
        AuthScreenDestination
    )
}