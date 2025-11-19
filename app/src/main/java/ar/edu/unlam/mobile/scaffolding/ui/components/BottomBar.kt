package ar.edu.unlam.mobile.scaffolding.ui.components

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
fun BottomBar(
    navItems: List<NavigationItem>,
    controller: NavHostController,
) {
    val navBackStackEntry by controller.currentBackStackEntryAsState()

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        navItems.forEach { item ->
            val controlNav = item.navRouteWithArgs ?: item.navRoute
            NavigationBarItem(
                selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == item.navRoute } == true,
                onClick = {
                    controller.navigate(controlNav) {
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
                        imageVector = item.icon,
                        contentDescription = item.label,
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelMedium,
                    )
                },
                alwaysShowLabel = false,
            )
        }
    }
}

data class NavigationItem(
    val navRoute: String,
    val navRouteWithArgs: String? = null,
    val icon: ImageVector,
    val label: String,
)

@Preview
@Composable
fun BottomBarTest() {
    ScaffoldingV2Theme {
        BottomBar(
            listOf(
                NavigationItem(
                    navRoute = "home",
                    icon = Icons.Default.LocationOn,
                    label = "Buscar",
                ),
                NavigationItem(
                    navRoute = "eventList",
                    icon = Icons.Default.CalendarMonth,
                    label = "Eventos",
                ),
                NavigationItem(
                    navRoute = "user/{id}",
                    navRouteWithArgs = "user/1",
                    icon = Icons.Default.AccountCircle,
                    label = "Perfil",
                ),
            ),
            NavHostController(LocalContext.current),
        )
    }
}
