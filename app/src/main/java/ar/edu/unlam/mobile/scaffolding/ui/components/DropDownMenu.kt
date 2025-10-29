package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

@Composable
fun EventosMenu(
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var textWidth by remember { mutableStateOf(0f) }

    val opciones = listOf(
        "Eventos participando",
        "Eventos que participé",
        "Sesiones de limpieza"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween 
    ) {
        // Texto + subrayado dinámico a la izquierda
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Eventos que participe",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp //  agranda la letra
                ),
                onTextLayout = { textLayoutResult ->
                    textWidth = textLayoutResult.size.width.toFloat()
                }
            )
            Divider(
                color = Color(0xFF4CAF50),
                thickness = 2.dp,
                modifier = Modifier.width(with(LocalDensity.current) { textWidth.toDp() })
            )
        }

        // Botón circular 
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { expanded = true }) {
                ThreeLinesIcon(lineWidth = 20.dp, lineHeight = 2.dp, lineSpacing = 4.dp, color = Color.Black)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            expanded = false
                            onOptionSelected(opcion)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ThreeLinesIcon(lineWidth: Dp, lineHeight: Dp, lineSpacing: Dp, color: Color) {
    Column(
        verticalArrangement = Arrangement.spacedBy(lineSpacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(3) {
            Canvas(modifier = Modifier.size(width = lineWidth, height = lineHeight)) {
                drawLine(
                    color = color,
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = size.height,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventosMenuPreview() {
    MaterialTheme {
        EventosMenu { opcionSeleccionada ->
            println("Seleccionaste: $opcionSeleccionada")
        }
    }
}
