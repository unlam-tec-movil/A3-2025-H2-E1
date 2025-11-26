package ar.edu.unlam.mobile.scaffolding

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import ar.edu.unlam.mobile.scaffolding.ui.screens.EventDetailsScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.EventListScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.OnBoardingScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.SplashScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.home.HOME_SCREEN_ROUTE
import ar.edu.unlam.mobile.scaffolding.ui.screens.home.HomeScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.home.HomeViewModel
import ar.edu.unlam.mobile.scaffolding.ui.screens.login.LoginScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.register.RegisterScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.user.UserProfileScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.user.UserScreen
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import org.osmdroid.util.GeoPoint

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
                    MainScreen(sessionManager)
                }
            }
        }
    }
}

@Composable
fun MainScreen(sessionManager: SessionManager) {
    // Controller es el elemento que nos permite navegar entre pantallas. Tiene las acciones
    // para navegar como naviegate y también la información de en dónde se "encuentra" el usuario
    // a través del back stack
    val controller = rememberNavController()
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // id del usuario logeado, de momento es hardcodeado hasta que se pueda logear
    val loggedUserId = sessionManager.getLoggedUserId()

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
                                navRouteWithArgs = "user/$loggedUserId",
                                icon = Icons.Default.AccountCircle,
                                label = "User",
                            ),
                        ),
                )
            }
        },
    ) { paddingValue ->
        // NavHost es el componente que funciona como contenedor de los otros componentes que
        // podrán ser destinos de navegación.
        NavHost(navController = controller, startDestination = "onboarding") {
            // composable es el componente que se usa para definir un destino de navegación.
            // Por parámetro recibe la ruta que se utilizará para navegar a dicho destino.

            // Home es el componente en sí que es el destino de navegación.
            composable("splash") {
                SplashScreen(navController = controller)
            }

            // LOGIN
            composable("login") {
                LoginScreen(navController = controller)
            }

            // REGISTER
            composable("register") {
                RegisterScreen(navController = controller)
            }

            // Pantalla principal
            composable(HOME_SCREEN_ROUTE) {
                HomeScreen(
                    modifier = Modifier.padding(paddingValue),
                    navController = controller,
                )
            }

            composable(
                route = "$HOME_SCREEN_ROUTE/{lat}/{lng}",
                arguments =
                    listOf(
                        navArgument("lat") { type = NavType.StringType },
                        navArgument("lng") { type = NavType.StringType },
                    ),
            ) { backStackEntry ->
                val homeViewModel: HomeViewModel = hiltViewModel()
                val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
                val lng = backStackEntry.arguments?.getString("lng")?.toDoubleOrNull()

                LaunchedEffect(lat, lng) {
                    homeViewModel.setTargetLocation(GeoPoint(lat ?: 0.0, lng ?: 0.0))
                }

                HomeScreen(
                    viewModel = homeViewModel,
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

            // Lista de eventos del usuario
            composable(
                route = "user/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType }),
            ) { navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getLong("id") ?: 1
                UserScreen(
                    userId = id,
                    modifier = Modifier.padding(paddingValue),
                    navController = controller,
                )
            }

            // USER PROFILE
            composable(
                route = "userProfile/{id}",
                arguments =
                    listOf(
                        navArgument("id") { type = NavType.LongType },
                    ),
            ) { navBackStackEntry ->

                val id = navBackStackEntry.arguments?.getLong("id") ?: 1L

                UserProfileScreen(
                    userId = id,
                    modifier = Modifier.fillMaxSize(),
                    navController = controller,
                )
            }

            // forma de llamar a eventDetails habilitando reporting:
            // controller.navigate("eventDetails/${event.id}?enableReport=true")
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
            ) { navBackStackEntry ->

                val id = navBackStackEntry.arguments?.getString("id") ?: ""
                val enableReporting = navBackStackEntry.arguments?.getBoolean("enableReporting") ?: false
                val hideParticipate = navBackStackEntry.arguments?.getBoolean("hideParticipateButton") ?: false

                EventDetailsScreen(
                    modifier = Modifier.padding(paddingValue),
                    viewModel = hiltViewModel(),
                    navController = controller,
                    enableReporting = enableReporting,
                    hideParticipateButton = hideParticipate,
                )
            }

            composable("onboarding") {
                OnBoardingScreen(
                    navController = controller,
                    modifier = Modifier.padding(paddingValue),
                )
            }
        }
    }
}
