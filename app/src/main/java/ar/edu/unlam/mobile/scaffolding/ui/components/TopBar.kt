package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String?,
    onNavigateBack: (() -> Unit)? = null,
    colors: TopAppBarColors =
        TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground,
        ),
    actions: (@Composable () -> Unit)? = null,
) {
    CenterAlignedTopAppBar(
        title = {
            if (title != null) {
                Text(text = title)
            }
        },
        navigationIcon = {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            if (actions != null) actions()
        },
        colors = colors,
    )
}

@Preview
@Composable
fun TopBarPreview() {
    ScaffoldingV2Theme {
        TopBar(
            title = "Title",
            onNavigateBack = { /*TODO*/ },
            actions = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                }
            },
        )
    }
}

@Preview
@Composable
fun TopBarPreview2() {
    ScaffoldingV2Theme {
        TopBar(
            title = "Title",
        ) {
            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "testIcon")
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "testButton")
            }
        }
    }
}
