package ar.edu.unlam.mobile.scaffolding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ar.edu.unlam.mobile.scaffolding.ui.components.BottomBar
import ar.edu.unlam.mobile.scaffolding.ui.components.NavigationItem
import ar.edu.unlam.mobile.scaffolding.ui.components.SnackbarVisualsWithError
import ar.edu.unlam.mobile.scaffolding.ui.screens.ConfirmParticipationScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.EventDetailsScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.EventListScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.FormScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.HOME_SCREEN_ROUTE
import ar.edu.unlam.mobile.scaffolding.ui.screens.HomeScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.UserScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.login.LoginScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.register.RegisterScreen
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScaffoldingV2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    // Controller es el elemento que nos permite navegar entre pantallas. Tiene las acciones
    // para navegar como naviegate y también la información de en dónde se "encuentra" el usuario
    // a través del back stack
    val controller = rememberNavController()
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // id del usuario logeado, de momento es hardcodeado hasta que se pueda logear
    val idLogUser = "1"

    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        bottomBar = {
            if (currentRoute == "home" || currentRoute == "eventList" || currentRoute == "user/{id}") {
                BottomBar(
                    controller = controller,
                    navItems =
                        listOf(
                            NavigationItem(
                                navRoute = "home",
                                icon = Icons.Default.LocationOn,
                                label = "Buscar",
                            ),
                            NavigationItem(
                                navRoute = "eventList",
                                icon = Icons.Default.CalendarMonth,
                                label = "Calendario",
                            ),
                            NavigationItem(
                                navRoute = "user/{id}",
                                navRouteWithArgs = "user/$idLogUser",
                                icon = Icons.Default.AccountCircle,
                                label = "User",
                            ),
                        ),
                )
            }
        },
        snackbarHost = {
            SnackbarHost(snackBarHostState) { data ->
                // custom snackbar with the custom action button color and border
                val isError = (data.visuals as? SnackbarVisualsWithError)?.isError ?: false
                val buttonColor =
                    if (isError) {
                        ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error,
                        )
                    } else {
                        ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.inversePrimary,
                        )
                    }

                Snackbar(
                    modifier =
                        Modifier
                            .border(2.dp, MaterialTheme.colorScheme.secondary)
                            .padding(12.dp),
                    action = {
                        TextButton(
                            onClick = { if (isError) data.dismiss() else data.performAction() },
                            colors = buttonColor,
                        ) {
                            Text(data.visuals.actionLabel ?: "")
                        }
                    },
                ) {
                    Text(data.visuals.message)
                }
            }
        },
    ) { paddingValue ->
        // NavHost es el componente que funciona como contenedor de los otros componentes que
        // podrán ser destinos de navegación.
        NavHost(navController = controller, startDestination = HOME_SCREEN_ROUTE) {
            // composable es el componente que se usa para definir un destino de navegación.
            // Por parámetro recibe la ruta que se utilizará para navegar a dicho destino.
            composable(HOME_SCREEN_ROUTE) {
                // Home es el componente en sí que es el destino de navegación.
                HomeScreen(modifier = Modifier.padding(paddingValue))
            }

            composable("eventList") {
                EventListScreen(
                    modifier = Modifier.padding(paddingValue),
                    navController = controller,
                )
            }

            composable(
                route = "user/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getString("id") ?: "1"
                UserScreen(
                    userId = id,
                    modifier = Modifier.padding(paddingValue),
                )
            }

            composable("form") {
                FormScreen(
                    modifier = Modifier.padding(paddingValue),
                    snackbarHostState = snackBarHostState,
                )
            }

            composable(
                route = "eventDetails/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getInt("id") ?: 1
                EventDetailsScreen(
                    modifier = Modifier.padding(paddingValue),
                    eventId = id,
                    navController = controller,
                )
            }

            composable("login") {
                LoginScreen(
                    viewModel = hiltViewModel(),
                    navController = controller,
                )
            }

            composable("register") {
                RegisterScreen(
                    viewModel = hiltViewModel(),
                    navController = controller,
                )
            }
            composable(
                route = "confirmParticipation/{eventId}/{eventName}/{eventDate}/{eventPlace}",
                arguments =
                    listOf(
                        navArgument("eventId") { type = NavType.StringType },
                        navArgument("eventName") { type = NavType.StringType },
                        navArgument("eventDate") { type = NavType.StringType },
                        navArgument("eventPlace") { type = NavType.StringType },
                    ),
            ) { navBackStackEntry ->
                val eventName = navBackStackEntry.arguments?.getString("eventName") ?: "Evento"
                val eventDate = navBackStackEntry.arguments?.getString("eventDate") ?: "Sin fecha"
                val eventPlace = navBackStackEntry.arguments?.getString("eventPlace") ?: "Sin lugar"

                ConfirmParticipationScreen(
                    eventName = eventName,
                    eventDate = eventDate,
                    eventPlace = eventPlace,
                    onBackClick = { controller.popBackStack() },
                    onAddToCalendarClick = { /* TODO */ },
                    onParticipateClick = { /*todo*/ },
                )
            }
        }
    }
}
