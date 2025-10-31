package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme

@Composable
fun BottomBar(controller: NavHostController) {
    val navBackStackEntry by controller.currentBackStackEntryAsState()

    // id del usuario logeado, de momento es hardcodeado hasta que se pueda logear
    val idLogUser = "1"

    @Composable
    fun RowScope.navigationItem(
        navRoute: String,
        icon: ImageVector,
        label: String,
    ) {
        NavigationBarItem(
            selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == navRoute } == true,
            onClick = {
                controller.navigate(navRoute) {
                    popUpTo(controller.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors =
                NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    indicatorColor = MaterialTheme.colorScheme.tertiary,
                ),
            icon = {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                )
            },
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                )
            },
            alwaysShowLabel = false,
        )
    }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        navigationItem(
            navRoute = "home",
            icon = Icons.Default.LocationOn,
            label = "Buscar",
        )
        navigationItem(
            navRoute = "eventList",
            icon = Icons.Default.CalendarMonth,
            label = "Calendario",
        )
        // No cambie a este item por necesitar un id de usuario (unica diferencia),
        // dependiendo de si usuario necesitara dar su id para acceder a la pantalla
        // puede ser cambiado a simplemente un navigationItem()
        NavigationBarItem(
            selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == "user/{id}" } == true,
            onClick = {
                controller.navigate("user/$idLogUser") {
                    popUpTo(controller.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors =
                NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    indicatorColor = MaterialTheme.colorScheme.tertiary,
                ),
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "User",
                )
            },
            label = {
                Text(
                    text = "User",
                    style = MaterialTheme.typography.labelMedium,
                )
            },
            alwaysShowLabel = false,
        )
    }
}

@Preview
@Composable
fun BottomBarTest() {
    ScaffoldingV2Theme {
        BottomBar(NavHostController(LocalContext.current))
    }
}
