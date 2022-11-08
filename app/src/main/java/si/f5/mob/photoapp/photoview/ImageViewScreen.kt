package si.f5.mob.photoapp.photoview

import android.net.Uri
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavController
import coil.compose.AsyncImage
import timber.log.Timber
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun ImageViewScreen(navController: NavController, imageUri: Uri?) {
    Scaffold { paddingValues ->
        BoxWithConstraints(modifier = Modifier.padding(paddingValues)) {
            if (imageUri == null) {
                EmptyUriErrorAlert(navController = navController)
            } else {
                ImageView(imageUri = imageUri)
            }
        }
    }
}

@Composable
private fun EmptyUriErrorAlert(navController: NavController) {
    AlertDialog(onDismissRequest = {},
        title = { Text(text = "エラー") },
        text = { Text(text = "URLが空のため、表示できませんでした。") },
        confirmButton = {
            TextButton(onClick = {
                navController.popBackStack()
            }) {
                Text(text = "OK")
            }
        })
}

@Composable
private fun ImageView(imageUri: Uri?) {
    var offsetX by remember {
        mutableStateOf(0f)
    }
    var offsetY by remember {
        mutableStateOf(0f)
    }
    var scale by remember {
        mutableStateOf(1f)
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTransformGestures { _, _, zoom, _ ->
                scale = maxOf(1f, scale * zoom)
                if (scale == 1f) {
                    // 初期位置へ戻す
                    offsetX = 0f
                    offsetY = 0f
                }
            }
        }) {
        AsyncImage(modifier = Modifier
            .align(Alignment.Center)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    val maxOffsetX = size.width / 2 / scale
                    val maxOffsetY = size.height / 2 / scale
                    if (scale > 1f) {
                        change.consume()
                        if ((offsetX + dragAmount.x).absoluteValue < maxOffsetX) {
                            offsetX += dragAmount.x
                        }
                        if ((offsetY + dragAmount.y).absoluteValue < maxOffsetY) {
                            offsetY += dragAmount.y
                        }
                        Timber.d("maxOffsetX = $maxOffsetX maxOffsetY = $maxOffsetY")
                        Timber.d("x = $offsetX  y = $offsetY width = ${size.width} height = ${size.height} scale = $scale")
                    }
                }
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) },
            model = imageUri,
            contentDescription = ""
        )
    }
}