package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.ui.components.TopBar
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userId: Long,
    viewModel: UserProfileViewModel = hiltViewModel(),
    navController: NavController,
    modifier: Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                title = "Perfil",
                actions = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Cerrar",
                        )
                    }
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier,
    ) { paddingValues ->
        Box(
            modifier =
                modifier
                    .padding(paddingValues),
        ) {
            when (uiState.profileUiState) {
                MessageUIState.Loading -> {
                    Box(modifier = modifier, contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is MessageUIState.Error -> {
                    Box(modifier = modifier, contentAlignment = Alignment.Center) {
                        Text(text = "Error: ${(uiState.profileUiState as MessageUIState.Error).message}")
                    }
                }

                is MessageUIState.Success -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(12.dp),
                    ) {
                        AsyncImage(
                            model = uiState.avatar,
                            contentDescription = "Avatar de ${uiState.name}",
                            modifier =
                                Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(
                                        BorderStroke(
                                            2.dp,
                                            MaterialTheme.colorScheme.secondary,
                                        ),
                                        shape = CircleShape,
                                    ),
                            contentScale = ContentScale.Crop,
                        )

                        Text(
                            text = uiState.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                        )

                        HorizontalDivider()
                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "Editar Perfil",
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Editar Perfil")
                        }

                        Button(
                            onClick = {
                                viewModel.logout()

                                navController.navigate("login") {
                                    popUpTo(0)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Cerrar sesión",
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cerrar Sesión")
                        }
                    }
                }
            }
        }
    }
}
