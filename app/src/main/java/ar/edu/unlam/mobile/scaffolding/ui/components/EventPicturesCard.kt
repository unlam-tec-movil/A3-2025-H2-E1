package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun EventPicturesCard(
    modifier: Modifier = Modifier,
    title: String?,
    images: List<String>,
) {
    Column(modifier = modifier) {
        if (title != null) {
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = LocalWindowInfo.current.containerSize.height.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(images) { item ->
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                    elevation = CardDefaults.cardElevation(4.dp),
                ) {
                    AsyncImage(
                        model = item,
                        contentDescription = "Imagen del evento",
                        modifier =
                            modifier
                                .height(180.dp),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun EventPicturesCardPreview() {
    fun imageExamples(): List<String> =
        listOf(
            "https://cdn.pixabay.com/photo/2014/07/09/12/17/live-concert-388160_1280.jpg",
            "https://shorturl.at/QUHmG",
            "https://shorturl.at/ZehlK",
        )

    EventPicturesCard(title = "Imagenes del antes", images = imageExamples())
}
