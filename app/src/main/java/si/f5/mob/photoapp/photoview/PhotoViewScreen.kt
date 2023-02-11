package si.f5.mob.photoapp.photoview

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoViewScreen(
    photoViewViewModel: PhotoViewViewModel = hiltViewModel(), imageId: Long?
) {
    val imageBitmap: Bitmap? by photoViewViewModel.imageBitmap.observeAsState()

    photoViewViewModel.getImageBitmap(imageId = imageId)

    Scaffold { paddingValues ->
        BoxWithConstraints(modifier = Modifier.padding(paddingValues)) {
            when (imageBitmap) {
                null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            modifier = Modifier.align(Alignment.Center), text = "画像が取得できませんでした"
                        )
                    }
                }
                else -> {
                    ImageView(bitmap = imageBitmap!!)
                }
            }
        }
    }
}

@Composable
private fun ImageView(bitmap: Bitmap) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawImage(bitmap.asImageBitmap())
    }
}