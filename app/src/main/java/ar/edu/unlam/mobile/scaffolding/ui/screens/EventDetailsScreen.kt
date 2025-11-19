package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.unlam.mobile.scaffolding.domain.event.model.Event
import ar.edu.unlam.mobile.scaffolding.domain.user.model.User
import ar.edu.unlam.mobile.scaffolding.ui.components.EventParticipant
import ar.edu.unlam.mobile.scaffolding.ui.components.EventPicturesCard
import ar.edu.unlam.mobile.scaffolding.ui.components.ParticipantInfoPopUp
import ar.edu.unlam.mobile.scaffolding.ui.components.PrimaryButton
import ar.edu.unlam.mobile.scaffolding.ui.components.SystemBarStyle
import ar.edu.unlam.mobile.scaffolding.ui.components.TimePlaceEventCard
import ar.edu.unlam.mobile.scaffolding.ui.components.TopBar
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import coil.compose.AsyncImage

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EventDetailsScreen(
    modifier: Modifier = Modifier,
    eventId: Int,
    enableReporting: Boolean = false,
    navController: NavController? = null,
    viewModel: EventDetailsViewModel = hiltViewModel(),
) {
    val eventState by viewModel.eventState.collectAsState()

    // Cargar evento cuando la pantalla se muestra
    LaunchedEffect(eventId) {
        viewModel.getEventById(eventId, 1L)
    }

    when (eventState) {
        is Resource.Success -> {
            val event = (eventState as Resource.Success).data
            EventDetailsContent(event, enableReporting, navController)
        }
        is Resource.Error -> {
            Text("Error: ${(eventState as Resource.Error).message}")
        }
    }
}

@Composable
fun EventDetailsContent(
    event: Event,
    enableReporting: Boolean,
    navController: NavController? = null,
) {
    val showPopup = remember { mutableStateOf(false) }
    val selectedUser = remember { mutableStateOf<User?>(null) }
    SystemBarStyle()

    Scaffold(
        topBar = {
            TopBar(
                title = event.title,
                onNavigateBack = { navController?.popBackStack() },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(paddingValues),
        ) {
            if (event.image != null) {
                item {
                    AsyncImage(
                        model = event.image,
                        contentDescription = "Imagen del evento",
                        modifier =
                            Modifier
                                .height(180.dp)
                                .fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }

            // Popup
            item {
                if (showPopup.value && selectedUser.value != null) {
                    ParticipantInfoPopUp(
                        user = selectedUser.value!!,
                        onDismiss = { showPopup.value = false },
                        onReportClick = { showPopup.value = false },
                        enableReporting = enableReporting,
                    )
                }
            }

            item { TimePlaceEventCard(event = event) }
            item { Text(text = event.description) }

            item {
                EventParticipant(
                    user = event.creator,
                    members = event.members,
                    onAvatarClick = { userClicked ->
                        selectedUser.value = userClicked
                        showPopup.value = true
                    },
                )
            }

            item { EventPicturesCard(title = "Estado del lugar", images = event.beforeImage) }
            item { Spacer(modifier = Modifier.height(66.dp)) }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Spacer(modifier = Modifier.weight(1f))
            if (!enableReporting) {
                PrimaryButton(
                    "Participar",
                    width = 200.dp,
                    onClick = {
                        val eventDateFormatted =
                            java.text
                                .SimpleDateFormat(
                                    "EEEE d 'de' MMMM, HH:mm 'hs'",
                                    java.util.Locale("es", "AR"),
                                ).format(java.util.Date(event.dateTime))

                        val eventPlace = "Ubicación: ${event.lat}, ${event.lng}"

                        val encodedName = java.net.URLEncoder.encode(event.title, "UTF-8")
                        val encodedDate = java.net.URLEncoder.encode(eventDateFormatted, "UTF-8")
                        val encodedPlace = java.net.URLEncoder.encode(eventPlace, "UTF-8")

                        navController?.navigate(
                            "confirmParticipation/${event.id}/$encodedName/$encodedDate/$encodedPlace",
                        )
                    },
                )
            }
        }
    }
}
