package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.Manifest
import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.WrongLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.ui.components.EventCard
import ar.edu.unlam.mobile.scaffolding.ui.components.EventFilterButton
import ar.edu.unlam.mobile.scaffolding.ui.components.SystemBarStyle
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    userId: Long,
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = hiltViewModel(),
    navController: NavController,
) {
    val uiState by viewModel.userUiState.collectAsState()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    SystemBarStyle()

    var isDistanceFilter by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(key1 = userId) {
        viewModel.getUser(userId)
        viewModel.getEvents(userId)
    }

    if (permissionState.status.isGranted) {
        LaunchedEffect(Unit) {
            viewModel.getCurrentLocation(context = context)
        }
        Log.d("UserScreen", "Permiso de ubicación otorgado")
    }

    Box(
        modifier =
            modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize(),
    ) {
        when (uiState.userUiState) {
            MessageUIState.Loading -> {
                CircularProgressIndicator(
                    modifier = modifier.align(Alignment.Center),
                )
            }

            is MessageUIState.Success -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 4.dp)
                                        .clickable(
                                            onClick = { navController.navigate("userProfile/$userId") },
                                        ),
                            ) {
                                AsyncImage(
                                    model = uiState.avatar,
                                    contentDescription = "Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier =
                                        Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .border(
                                                BorderStroke(
                                                    2.dp,
                                                    MaterialTheme.colorScheme.secondary,
                                                ),
                                                shape = CircleShape,
                                            ),
                                )

                                Column(
                                    modifier =
                                        Modifier
                                            .padding(start = 5.dp)
                                            .weight(1f),
                                ) {
                                    Text(
                                        text = uiState.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                    )
                                    Text(
                                        text = uiState.description ?: "",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                                IconButton(
                                    onClick = { navController.navigate("userProfile/$userId") },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Más opciones",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                    )
                                }
                            }
                        },
                        colors =
                            TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                scrolledContainerColor = MaterialTheme.colorScheme.primary,
                            ),
                        modifier = Modifier.fillMaxWidth(),
                        scrollBehavior = scrollBehavior,
                    )

                    TopAppBar(
                        title = {
                            Button(
                                onClick = { viewModel.togglePastEvents() },
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary,
                                    ),
                                shape = MaterialTheme.shapes.medium,
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                                modifier =
                                    Modifier
                                        .padding(vertical = 6.dp)
                                        .height(48.dp),
                            ) {
                                Text(
                                    text =
                                        if (uiState.showPastEvents) {
                                            "Eventos que participé"
                                        } else {
                                            "Eventos participando"
                                        },
                                    style =
                                        MaterialTheme.typography.titleMedium.copy(
                                            color = MaterialTheme.colorScheme.onSecondary,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                        ),
                                )
                            }
                        },
                        actions = {

                            EventFilterButton(
                                onDateFilter = {
                                    viewModel.getEvents(userId, sortBy = "date", order = "asc")
                                    isDistanceFilter = false
                                },
                                onDistanceFilter = {
                                    if (permissionState.status.isGranted) {
                                        viewModel.getEvents(userId, sortBy = "distance", order = "asc")
                                        isDistanceFilter = true
                                    } else {
                                        permissionState.launchPermissionRequest()
                                    }
                                },
                                selectedOption = if (isDistanceFilter) 1 else 0,
                            )
                        },
                        modifier = Modifier.consumeWindowInsets(WindowInsets.systemBars),
                    )

                    val eventsToShow = if (uiState.showPastEvents) uiState.pastEvents else uiState.joinedEvents

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (eventsToShow.isNotEmpty()) {
                            items(items = eventsToShow, key = { it.id }) { event ->
                                EventCard(
                                    imageUrl = event.image,
                                    title = event.title,
                                    date = event.dateTime,
                                    coordinates = LatLng(event.lat, event.lng),
                                    myLocation = uiState.currentLocation,
                                    isDistanceFilter = isDistanceFilter,
                                    modifier =
                                        Modifier
                                            .padding(vertical = 4.dp)
                                            .animateItem(tween(durationMillis = 500))
                                            .clickable {
                                                val isPast =
                                                    uiState.showPastEvents // true si es evento pasado
                                                navController.navigate(
                                                    "eventDetails/${event.id}?enableReporting=$isPast&hideParticipateButton=true",
                                                )
                                            },
                                )
                            }
                        } else {
                            item {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Column(modifier = Modifier.align(Alignment.Center)) {
                                        Icon(
                                            imageVector = Icons.Default.WrongLocation,
                                            contentDescription = "No event found",
                                            modifier =
                                                Modifier
                                                    .size(100.dp)
                                                    .align(Alignment.CenterHorizontally),
                                        )
                                        Text(
                                            text =
                                                if (uiState.showPastEvents) {
                                                    "No participaste en eventos anteriores"
                                                } else {
                                                    "No te encuentras en ningun evento CleanUp"
                                                },
                                            color = Color.White,
                                            modifier = Modifier.padding(4.dp),
                                        )
                                        if (!uiState.showPastEvents) {
                                            Text(
                                                text = "Revisa eventos cercanos para unirte a la ayuda ;)",
                                                color = Color.White,
                                                modifier = Modifier.padding(4.dp),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            is MessageUIState.Error -> {
                Text(text = "Error: ${(uiState.userUiState as MessageUIState.Error).message}")
            }
        }
    }
}
