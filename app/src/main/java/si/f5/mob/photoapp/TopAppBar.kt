package si.f5.mob.photoapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoAppTopAppBar(
    title: String,
    navButtonVisible: Boolean,
    navButtonOnClick: () -> Unit = {},
) {
    val navigationIcon: @Composable () -> Unit = if (navButtonVisible) {
        {
            IconButton(onClick = navButtonOnClick) {
                Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "Back")
            }
        }
    } else {
        {}
    }
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = navigationIcon
    )
}