package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.unlam.mobile.scaffolding.domain.user.model.User
import coil.compose.AsyncImage

@Composable
fun EventParticipant(
    user: User,
    members: List<User>?,
    onAvatarClick: (User) -> Unit,
) {
    val isExpanded = remember { mutableStateOf(false) }
    val itemSpacing = 8.dp
    val itemSize = 44.dp

    Column(verticalArrangement = Arrangement.spacedBy(itemSpacing)) {
        // --- ORGANIZADOR ---
        Text(
            text = "Organizador",
            style = MaterialTheme.typography.titleMedium,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
            modifier = Modifier.clickable { onAvatarClick(user) },
        ) {
            AsyncImage(
                contentScale = ContentScale.Crop,
                placeholder = rememberVectorPainter(Icons.Default.Person),
                modifier =
                    Modifier
                        .clip(CircleShape)
                        .size(itemSize)
                        .background(Color.Gray),
                model = user.avatarUrl,
                contentDescription = "Avatar Organizador",
            )
            Text(
                text = user.name,
                style = MaterialTheme.typography.labelLarge,
            )
        }

        // --- PARTICIPANTES ---
        Text(
            text = "Participantes",
            style = MaterialTheme.typography.titleMedium,
        )

        if (members != null && members.isNotEmpty()) {
            LazyVerticalGrid(
                verticalArrangement = Arrangement.spacedBy(itemSpacing),
                columns = GridCells.FixedSize(itemSize + (itemSpacing * 2)),
                modifier = Modifier.heightIn(max = LocalWindowInfo.current.containerSize.height.dp),
            ) {
                val maxToExpand = 4
                val minMembers = minOf(members.size, 5)
                val displayCount = if (isExpanded.value) members.size else minMembers

                items(displayCount) { index ->

                    // Botón "+X"
                    if (index == maxToExpand && !isExpanded.value) {
                        Box(
                            modifier =
                                Modifier
                                    .padding(horizontal = itemSpacing)
                                    .clip(CircleShape)
                                    .size(itemSize)
                                    .background(Color.LightGray)
                                    .clickable { isExpanded.value = true },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "+${members.size - maxToExpand - 1}",
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    } else if (index == members.size - 1 && isExpanded.value) {
                        Text(
                            text = "Ver menos",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            modifier =
                                Modifier
                                    .padding(horizontal = itemSpacing)
                                    .clickable { isExpanded.value = false },
                        )
                    } else {
                        AsyncImage(
                            contentScale = ContentScale.Crop,
                            placeholder = rememberVectorPainter(Icons.Default.Person),
                            modifier =
                                Modifier
                                    .padding(horizontal = itemSpacing)
                                    .clip(CircleShape)
                                    .size(itemSize)
                                    .background(Color.Gray)
                                    .clickable {
                                        onAvatarClick(members[index])
                                    },
                            model = members[index].avatarUrl,
                            contentDescription = "Avatar Participante",
                        )
                    }
                }
            }
        } else {
            Text(
                text = "No hay participantes aún.",
                color = Color.Gray,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun EventParticipantPreviewSimplified() {
    val sampleUser =
        User(
            id = 1,
            name = "Juan Perez",
            avatarUrl = "",
            description = "Organizador del evento",
        )

    val sampleMembers =
        listOf(
            User(id = 2, name = "Kiara Mochica", avatarUrl = "", description = ""),
            User(id = 3, name = "Tatiana Sánchez", avatarUrl = "", description = ""),
            User(id = 4, name = "Maria Lopez", avatarUrl = "", description = ""),
            User(id = 5, name = "Ana Garcia", avatarUrl = "", description = ""),
            User(id = 6, name = "Nico Ñoñez", avatarUrl = "", description = ""),
        )

    EventParticipant(
        user = sampleUser,
        members = sampleMembers,
        onAvatarClick = { /* Nada en preview */ },
    )
}
