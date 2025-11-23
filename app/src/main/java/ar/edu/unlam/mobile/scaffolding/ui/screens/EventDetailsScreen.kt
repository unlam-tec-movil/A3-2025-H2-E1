package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.unlam.mobile.scaffolding.domain.user.model.UserItem
import ar.edu.unlam.mobile.scaffolding.ui.components.ConfirmParticipationComponent
import ar.edu.unlam.mobile.scaffolding.ui.components.EventParticipant
import ar.edu.unlam.mobile.scaffolding.ui.components.EventPicturesCard
import ar.edu.unlam.mobile.scaffolding.ui.components.ParticipantInfoPopUp
import ar.edu.unlam.mobile.scaffolding.ui.components.PrimaryButton
import ar.edu.unlam.mobile.scaffolding.ui.components.SystemBarStyle
import ar.edu.unlam.mobile.scaffolding.ui.components.TimePlaceEventCard
import ar.edu.unlam.mobile.scaffolding.ui.components.TopBar
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EventDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: EventDetailsViewModel = hiltViewModel(),
    enableReporting: Boolean = false,
    hideParticipateButton: Boolean = false,
    navController: NavController? = null,
) {
    val state = viewModel.uiState.collectAsState()

    if (state.value.isLoading || state.value.event == null) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val event = state.value.event ?: return
    val scrollState = rememberScrollState()
    val showPopup = remember { mutableStateOf(false) }
    val selectedUser = remember { mutableStateOf<UserItem?>(null) }
    val showParticipationSheet = remember { mutableStateOf(false) }

    SystemBarStyle()

    // mostrar el componente Participation
    if (showParticipationSheet.value) {
        val sheetState =
            rememberModalBottomSheetState(
                skipPartiallyExpanded = true, // 👈 clave para que abra arriba del todo
            )

        ModalBottomSheet(
            onDismissRequest = { showParticipationSheet.value = false },
            sheetState = sheetState,
        ) {
            ConfirmParticipationComponent(
                eventName = event.title,
                eventDate =
                    java.text
                        .SimpleDateFormat(
                            "EEEE d 'de' MMMM, HH:mm 'hs'",
                            java.util.Locale("es", "AR"),
                        ).format(java.util.Date(event.dateTime)),
                eventPlace = "Ubicación: ${event.lat}, ${event.lng}",
                onBackClick = { showParticipationSheet.value = false },
                onAddToCalendarClick = { /* tu lógica */ },
                onParticipateClick = { /* tu lógica */ },
            )
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = event.title,
                onNavigateBack = { navController?.popBackStack() },
            )
        },
    ) { paddingValues ->

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier =
                Modifier
                    .padding(paddingValues)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    .verticalScroll(scrollState),
        ) {
            if (event.image != null) {
                AsyncImage(
                    model = event.image,
                    contentDescription = "Imagen del evento",
                    modifier =
                        modifier
                            .height(180.dp)
                            .fillMaxWidth(),
                    contentScale = ContentScale.Crop,
                )
            }

            if (showPopup.value && selectedUser.value != null) {
                ParticipantInfoPopUp(
                    user = selectedUser.value!!,
                    onDismiss = { showPopup.value = false },
                    onReportClick = { showPopup.value = false },
                    enableReporting = enableReporting,
                )
            }

            TimePlaceEventCard(
                event = event,
                onLocationClick = { lat, lng ->
                    navController?.navigate("$HOME_SCREEN_ROUTE/$lat/$lng")
                },
            )

            Text(text = event.description)

            EventParticipant(
                user = event.creator,
                members = event.members,
                onAvatarClick = { userClicked ->
                    selectedUser.value = userClicked
                    showPopup.value = true
                },
            )

            EventPicturesCard(
                title = "Estado del lugar",
                images = event.beforeImage,
            )

            Spacer(modifier = Modifier.height(66.dp))
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Spacer(modifier = Modifier.weight(1f))

            if (!hideParticipateButton && !enableReporting) {
                PrimaryButton(
                    "Participar",
                    width = 200.dp,
                    modifier =
                        Modifier
                            .navigationBarsPadding()
                            .padding(bottom = 16.dp),
                    onClick = {
                        //  mostramos ConfirmParticipationComponent
                        showParticipationSheet.value = true
                    },
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun EventDetailsScreenPreview() {
    EventDetailsScreen()
}
