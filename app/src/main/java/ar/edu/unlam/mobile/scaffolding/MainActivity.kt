package ar.edu.unlam.mobile.scaffolding

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.runtime.LaunchedEffect
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
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.SessionManager
import ar.edu.unlam.mobile.scaffolding.ui.components.BottomBar
import ar.edu.unlam.mobile.scaffolding.ui.components.NavigationItem
import ar.edu.unlam.mobile.scaffolding.ui.components.SnackbarVisualsWithError
import ar.edu.unlam.mobile.scaffolding.ui.components.Welcome
import ar.edu.unlam.mobile.scaffolding.ui.screens.ConfirmParticipationScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.EventDetailsScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.EventListScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.FormScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.HOME_SCREEN_ROUTE
import ar.edu.unlam.mobile.scaffolding.ui.screens.HomeScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.HomeViewModel
import ar.edu.unlam.mobile.scaffolding.ui.screens.SplashScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.UserProfileScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.UserScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.login.LoginScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.register.RegisterScreen
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle =
                SystemBarStyle.light(
                    Color.TRANSPARENT,
                    Color.TRANSPARENT,
                ),
        )
        setContent {
            ScaffoldingV2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    if (BuildConfig.AUTO_LOGIN) {
                        sessionManager.saveToken(BuildConfig.DEV_TOKEN)
                    }
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val controller = rememberNavController()
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val idLogUser = 1L
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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

        NavHost(
            navController = controller,
            startDestination = "splash",
        ) {
            // SPLASH
            composable("splash") {
                SplashScreen(navController = controller)
            }

            // WELCOME
            composable("welcome") {
                Welcome(
                    onStartClick = {
                        controller.navigate("home") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    },
                )
            }

            // HOME (sin params)
            composable(HOME_SCREEN_ROUTE) {
                HomeScreen(
                    modifier = Modifier.padding(paddingValue),
                    navController = controller,
                )
            }

            // HOME (con lat/lng)
            composable(
                route = "$HOME_SCREEN_ROUTE/{lat}/{lng}",
                arguments =
                    listOf(
                        navArgument("lat") { type = NavType.StringType },
                        navArgument("lng") { type = NavType.StringType },
                    ),
            ) { backStackEntry ->

                val vm: HomeViewModel = hiltViewModel()
                val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
                val lng = backStackEntry.arguments?.getString("lng")?.toDoubleOrNull()

                LaunchedEffect(lat, lng) {
                    vm.setTargetLocation(lat, lng)
                }

                HomeScreen(
                    viewModel = vm,
                    modifier = Modifier.padding(paddingValue),
                    navController = controller,
                )
            }

            // LISTA DE EVENTOS
            composable("eventList") {
                EventListScreen(
                    modifier = Modifier.padding(paddingValue),
                    navController = controller,
                )
            }

            // PERFIL USUARIO
            composable(
                route = "user/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType }),
            ) { entry ->
                val id = entry.arguments?.getLong("id") ?: 1
                UserScreen(
                    userId = id,
                    modifier = Modifier.padding(paddingValue),
                    navController = controller,
                )
            }

            // USER PROFILE
            composable(
                route = "userProfile/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType }),
            ) {
                val id = navBackStackEntry?.arguments?.getLong("id") ?: 1L
                UserProfileScreen(
                    userId = id,
                    modifier = Modifier.fillMaxSize(),
                    navController = controller,
                )
            }

            // FORM
            composable("form") {
                FormScreen(
                    modifier = Modifier.padding(paddingValue),
                    snackbarHostState = snackBarHostState,
                )
            }

            // EVENT DETAILS
            composable(
                route =
                    "eventDetails/{id}" +
                        "?enableReporting={enableReporting}" +
                        "&hideParticipateButton={hideParticipateButton}",
                arguments =
                    listOf(
                        navArgument("id") { type = NavType.StringType },
                        navArgument("enableReporting") {
                            type = NavType.BoolType
                            defaultValue = false
                        },
                        navArgument("hideParticipateButton") {
                            type = NavType.BoolType
                            defaultValue = false
                        },
                    ),
            ) { entry ->

                val id = entry.arguments?.getInt("id") ?: 1
                val enableReporting = entry.arguments?.getBoolean("enableReporting") ?: false
                val hideParticipate = entry.arguments?.getBoolean("hideParticipateButton") ?: false

                EventDetailsScreen(
                    modifier = Modifier.padding(paddingValue),
                    viewModel = hiltViewModel(),
                    navController = controller,
                    enableReporting = enableReporting,
                    hideParticipateButton = hideParticipate,
                )
            }

            // LOGIN
            composable("login") {
                LoginScreen(navController = controller)
            }

            // REGISTER
            composable("register") {
                RegisterScreen(navController = controller)
            }

            // CONFIRM PARTICIPATION
            composable(
                route = "confirmParticipation/{eventId}/{eventName}/{eventDate}/{eventPlace}",
                arguments =
                    listOf(
                        navArgument("eventId") { type = NavType.StringType },
                        navArgument("eventName") { type = NavType.StringType },
                        navArgument("eventDate") { type = NavType.StringType },
                        navArgument("eventPlace") { type = NavType.StringType },
                    ),
            ) { entry ->

                val eventName = entry.arguments?.getString("eventName") ?: "Evento"
                val eventDate = entry.arguments?.getString("eventDate") ?: "Sin fecha"
                val eventPlace = entry.arguments?.getString("eventPlace") ?: "Sin lugar"

                ConfirmParticipationScreen(
                    eventName = eventName,
                    eventDate = eventDate,
                    eventPlace = eventPlace,
                    onBackClick = { controller.popBackStack() },
                    onAddToCalendarClick = { },
                    onParticipateClick = { },
                )
            }
        }
    }
}
