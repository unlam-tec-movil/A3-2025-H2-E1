package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.unlam.mobile.scaffolding.data.model.OnboardingModel

@Composable
fun OnBoardItem(page: OnboardingModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = page.image,
            contentDescription = null,
            modifier =
                Modifier
                    .height(350.dp)
                    .width(350.dp)
                    .padding(bottom = 20.dp),
        )
        Text(
            text = page.title,
            style =
                TextStyle(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
        )
        Text(
            text = page.description,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style =
                TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    textAlign = TextAlign.Center,
                ),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun OnBoardItemPreview() {
    val samplePage =
        OnboardingModel(
            image = Icons.Default.Warning,
            title = "Welcome to the App",
            description = "This is a sample description for the onboarding screen.",
        )
    OnBoardItem(page = samplePage)
}
