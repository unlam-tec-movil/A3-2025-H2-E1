package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ar.edu.unlam.mobile.scaffolding.ui.components.OnBoardItem
import ar.edu.unlam.mobile.scaffolding.ui.components.PrimaryButton
import ar.edu.unlam.mobile.scaffolding.ui.model.OnboardingModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
) {
    val pages =
        listOf(
            OnboardingModel(
                image = Icons.Default.CleaningServices,
                title = "Bienvenido",
                description =
                    "Ayuda a mantener tu entorno limpio. " +
                        "\nCada acción cuenta. " +
                        "\nÚnete a una comunidad comprometida con el cuidado ambiental.",
            ),
            OnboardingModel(
                image = Icons.Default.TravelExplore,
                title = "Encuentra eventos cerca de ti",
                description =
                    "Descubre y participa en limpiezas en tu ciudad. " +
                        "\nÚnete a eventos y conoce gente nueva. " +
                        "\nExplora eventos, regístrate y recibe recordatorios fácilmente.",
            ),
            OnboardingModel(
                image = Icons.Default.DateRange,
                title = "Organiza tu propia limpieza",
                description =
                    "Crea tus propios eventos y suma voluntarios. " +
                        "\nComparte ubicacion, horarios y coordina tu equipo desde la app. " +
                        "\nPequeñas acciones, grandes cambios.",
            ),
        )

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.size - 1

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (page == pages.size) {
                navController?.navigate("home")
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(bottom = 60.dp),
    ) {
        // 2. El Pager horizontal
        HorizontalPager(
            state = pagerState,
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
        ) { pageIndex ->

            OnBoardItem(
                page = pages[pageIndex],
            )
        }

        // 3. Indicadores (Los puntitos abajo)
        Row {
            repeat(pages.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier =
                        Modifier
                            .padding(4.dp)
                            .width(if (isSelected) 18.dp else 8.dp)
                            .height(8.dp)
                            .border(
                                width = 1.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(10.dp),
                            ).background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                shape = CircleShape,
                            ),
                )
            }
        }

        PrimaryButton(
            text = if (!isLastPage) "Siguiente" else "Terminar",
            onClick = {
                if (!isLastPage) {
                    val nextPage = pagerState.currentPage + 1
                    scope.launch { pagerState.animateScrollToPage(nextPage) }
                } else {
                    navController?.navigate("splash")
                }
            },
        )

        Text(
            text = "Omitir",
            style =
                TextStyle(
                    color = Color.Gray,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                ),
            modifier =
                Modifier.clickable {
                    navController?.navigate("splash")
                },
        )
    }
}

@Composable
@Preview(showBackground = true)
fun OnBoardingScreenPreview() {
    OnBoardingScreen()
}
