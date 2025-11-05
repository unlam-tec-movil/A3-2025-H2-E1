package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch

@Composable
fun AnimatedEventCard(
    eventCard: Event,
    onClose: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp
    val density = LocalDensity.current
    val screenHeightPx = with(density) { screenHeightDp.toPx() }

    //  sube hasta la mitad de la pantalla
    val finalPositionPx = screenHeightPx / 2

    val offsetY = remember { Animatable(screenHeightPx) }

    LaunchedEffect(eventCard) {
        offsetY.animateTo(
            targetValue = finalPositionPx,
            animationSpec = tween(durationMillis = 1000),
        )
    }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .offset { IntOffset(x = 0, y = offsetY.value.toInt()) }
                .zIndex(10f)
                .padding(horizontal = 16.dp),
    ) {
        EventHomeCard(
            event = eventCard,
            distance = "350 mts",
            onViewEventClick = {
                scope.launch {
                    offsetY.animateTo(
                        targetValue = screenHeightPx,
                        animationSpec = tween(durationMillis = 500),
                    )
                    onClose()
                }
            },
        )
    }
}
