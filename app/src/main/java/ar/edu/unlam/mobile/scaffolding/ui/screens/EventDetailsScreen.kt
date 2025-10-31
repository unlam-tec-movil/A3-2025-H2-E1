package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ar.edu.unlam.mobile.scaffolding.domain.event.model.Event
import ar.edu.unlam.mobile.scaffolding.domain.user.model.User
import ar.edu.unlam.mobile.scaffolding.ui.components.EventParticipant
import ar.edu.unlam.mobile.scaffolding.ui.components.EventPicturesCard
import ar.edu.unlam.mobile.scaffolding.ui.components.PrimaryButton
import ar.edu.unlam.mobile.scaffolding.ui.components.TimePlaceEventCard
import ar.edu.unlam.mobile.scaffolding.ui.components.TopBar
import coil.compose.AsyncImage

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EventDetailsScreen(
    modifier: Modifier = Modifier,
    eventId: Int,
    navController: NavController? = null,
) {
    Scaffold(
        modifier =
            modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp),
        topBar = {
            TopBar(
                title = event.title,
                onNavigateBack = { navController?.popBackStack() },
            )
        },
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (event.image != null) {
                item {
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
            }

            item {
                TimePlaceEventCard(
                    event = event,
                )
            }

            item {
                Text(
                    text = event.description,
                    fontSize = TextUnit.Unspecified,
                )
            }

            item {
                EventParticipant(
                    user = event.creator,
                    members = event.members,
                )
            }

            item {
                EventPicturesCard(
                    title = "Estado del lugar",
                    images = event.beforeImage,
                )
            }

            item {
                Spacer(modifier = Modifier.height(66.dp))
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                "Participar",
                width = 200.dp,
                onClick = { /*TODO*/ },
            )
        }
    }
}

private val imageExamples =
    listOf(
        "https://cdn.pixabay.com/photo/2014/07/09/12/17/live-concert-388160_1280.jpg",
        "https://shorturl.at/QUHmG",
        "https://shorturl.at/ZehlK",
        "https://www.bigfootdigital.co.uk/wp-content/uploads/2020/07/image-optimisation-scaled.jpg",
        "https://i0.wp.com/picjumbo.com/wp-content/uploads/calming-nature-wallpaper-free-image.jpeg?w=600&quality=80",
    )

private val members =
    (1..20)
        .map {
            User(
                id = it.toLong(),
                name = "Usuario $it",
                avatarUrl = "https://media.vanityfair.com/photos/597f75b706f77f18ffaad3bc/master/w_1440,h_960,c_limit/avatar-2.jpg",
                description = "",
            )
        }

private val event =
    Event(
        id = "1",
        title = "Concierto de Rock",
        description = "Limpieza post concierto de rock con bandas locales e internacionales.",
        dateTime = System.currentTimeMillis(),
        image = "https://cdn.pixabay.com/photo/2014/07/09/12/17/live-concert-388160_1280.jpg",
        lat = -34.5508002,
        lng = -58.4548101,
        beforeImage = imageExamples,
        afterImage = null,
        members = members,
        creator =
            User(
                id = 1,
                name = "Pepe Papa",
                avatarUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBwr_zZjgvmu4BccwDNIHic8K5dyehw7cSYA&s",
                description = null,
            ),
        saved = false,
        participating = false,
    )

@Composable
@Preview(showBackground = true)
fun EventDetailsScreenPreview() {
    EventDetailsScreen(eventId = 1)
}
