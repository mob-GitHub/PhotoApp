package si.f5.mob.photoapp.photoview

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun ImageViewScreen(navController: NavController, imageUri: Uri?) {
    Scaffold { paddingValues ->
        if (imageUri == null) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text(text = "エラー") },
                text = { Text(text = "URLが空のため、表示できませんでした。") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            navController.popBackStack()
                        }) {
                        Text(text = "OK")
                    }
                })
        } else {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues.calculateBottomPadding()),
                model = imageUri,
                contentDescription = ""
            )
        }
    }
}