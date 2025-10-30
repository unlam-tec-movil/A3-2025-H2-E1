package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun UserScreen(
    userId: String,
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = hiltViewModel(),
) {
    val userUiState by viewModel.userUiState.collectAsState()

    LaunchedEffect(key1 = userId) {
        val id = userId.toLongOrNull()
        if (id != null) {
            viewModel.getUser(id)
        } else {
            viewModel.getUser(2)
        }
        // Esto no funciona correctamente, despues sera arreglado
        // Pero mientras no sea necesaria la pantalla de User nos sirve
    }

    Scaffold { padding ->
        Column(modifier = modifier.padding(padding)) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .padding(start = 5.dp),
                ) {
                    AsyncImage(
                        model = userUiState.avatar,
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
                                .align(Alignment.CenterVertically),
                    ) {
                        Text(
                            text = userUiState.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        Text(
                            text = userUiState.description,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }
        }
    }
}
