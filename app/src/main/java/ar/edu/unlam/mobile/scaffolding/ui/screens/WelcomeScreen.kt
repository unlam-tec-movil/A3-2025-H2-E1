package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ar.edu.unlam.mobile.scaffolding.R
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(navController: NavController) {
    val scale = remember { Animatable(0.8f) }

    // Animación del icono (rebote)
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.15f,
            animationSpec = tween(500, easing = LinearOutSlowInEasing),
        )
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(300, easing = FastOutLinearInEasing),
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // --- Ícono ---
            Image(
                painter = painterResource(id = R.drawable.icono_app),
                contentDescription = "Icono de la app",
                modifier =
                    Modifier
                        .size(160.dp) // un poquito más grande
                        .graphicsLayer(
                            scaleX = scale.value,
                            scaleY = scale.value,
                        ),
            )

            Spacer(modifier = Modifier.height(30.dp))

            // --- Texto ---
            Text(
                text = "Bienvenido a la App",
                style = MaterialTheme.typography.headlineLarge,
            )

            Spacer(modifier = Modifier.height(50.dp))

            // --- Carrusel ---
            EventCarousel(
                images =
                    listOf(
                        R.drawable.event1,
                        R.drawable.event2,
                        R.drawable.event3,
                        R.drawable.event4,
                        R.drawable.event5,
                    ),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp)),
            )

            Spacer(modifier = Modifier.height(60.dp))

            // --- BOTÓN INICIO ---
            Button(
                onClick = {
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = "INICIAR",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
fun EventCarousel(
    images: List<Int>,
    modifier: Modifier = Modifier,
    intervalMillis: Long = 2500,
) {
    var index by remember { mutableStateOf(0) }
    var previousIndex by remember { mutableStateOf(0) }

    // Cambio automático
    LaunchedEffect(Unit) {
        while (true) {
            delay(intervalMillis)
            previousIndex = index
            index = (index + 1) % images.size
        }
    }

    // Carrusel con efecto slide
    AnimatedContent(
        targetState = index,
        transitionSpec = {
            slideInHorizontally { fullWidth -> fullWidth } + fadeIn() togetherWith
                slideOutHorizontally { fullWidth -> -fullWidth } + fadeOut()
        },
        label = "carouselSlide",
    ) { targetIndex ->
        Image(
            painter = painterResource(id = images[targetIndex]),
            contentDescription = "Imagen carousel",
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )
    }
}
