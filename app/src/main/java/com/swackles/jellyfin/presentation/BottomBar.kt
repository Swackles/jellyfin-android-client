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
import com.ramcosta.composedestinations.generated.destinations.DashboardScreenDestination
import com.ramcosta.composedestinations.generated.destinations.DetailScreenDestination
import com.ramcosta.composedestinations.generated.destinations.LoginScreenDestination
import com.ramcosta.composedestinations.generated.destinations.PlayerScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SearchScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsHomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.UserSelectScreenDestination
import com.ramcosta.composedestinations.spec.TypedDestinationSpec
import com.ramcosta.composedestinations.utils.currentDestinationAsState

private enum class BottomBarDestinations(
    val direction: TypedDestinationSpec<*>,
    val icon: ImageVector,
    val label: String
) {
    Greeting(DashboardScreenDestination, Icons.Default.Home, "Home"),
    Search(SearchScreenDestination, Icons.Default.Search, "Search"),
    Settings(SettingsHomeScreenDestination, Icons.Default.Settings, "Settings")
}


@Composable
fun BottomBar(navController: NavController) {
    val currentDestination = navController.currentDestinationAsState().value

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
    val IGNORED_PATHS = listOf<TypedDestinationSpec<*>>(
        LoginScreenDestination,
        UserSelectScreenDestination,
        DetailScreenDestination,
        PlayerScreenDestination
    )
}