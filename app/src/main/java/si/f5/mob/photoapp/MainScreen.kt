package si.f5.mob.photoapp

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MainScreen() {
    Scaffold(
        topBar = { PhotoAppTopAppBar() }
    ) {
        Box {
            Text(text = "Hello Jetpack Compose!!")
        }
    }
}

@Preview
@Composable
private fun PreviewMainScreen() {
    MainScreen()
}