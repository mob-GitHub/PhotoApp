package si.f5.mob.photoapp

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable

@Composable
fun PhotoAppTopAppBar() {
    TopAppBar(
        title = { Text(text = "PhotoApp") }
    )
}