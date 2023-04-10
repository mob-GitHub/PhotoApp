package si.f5.mob.photoapp.photoedit

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.SubcomposeAsyncImage
import si.f5.mob.common.Config
import si.f5.mob.photoapp.PhotoAppTopAppBar
import si.f5.mob.photoapp.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoEditScreen(
    navController: NavController,
    imageId: Long,
    photoEditViewModel: PhotoEditViewModel = hiltViewModel(),
) {
    val image = photoEditViewModel.getImage(imageId)
    var scale by remember { mutableStateOf(1F) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        if ((scale * zoomChange) >= 1F && (scale * zoomChange) <= 3F) {
            scale *= zoomChange
        }
        offset += panChange
    }

    Scaffold(topBar = {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val navButtonVisible = navBackStackEntry?.destination?.route != Screen.PhotoEdit.route
        PhotoAppTopAppBar(
            title = stringResource(id = Screen.PhotoEdit.resourceId),
            navButtonVisible = navButtonVisible,
            navButtonOnClick = {
                navController.popBackStack()
            }
        )
    }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    }
                    .transformable(transformableState)
                    .fillMaxSize(),
                model = image.uri,
                contentDescription = null,
                loading = {
                    CircularProgressIndicator()
                }
            )
            TrimmingGrid(modifier = Modifier.align(Alignment.Center))
        }
    }
}

/**
 * 編集用ImageView
 * 自由に動かすことができる
 */
@Composable
private fun EditImageView(bitmap: Bitmap) {
    // TODO:後で実際にトリミング用の設定にする
    val imageSize = IntSize(500, 500)
    var offset by remember { mutableStateOf(IntOffset(50, 50)) }

    Canvas(modifier = Modifier
        .aspectRatio(1f)
        .padding(all = 10.dp)
        .border(width = 1.dp, color = Color.Gray)
        .pointerInput(Unit) {
            detectDragGestures { _, dragAmount ->
                val nextOffset = offset + IntOffset(dragAmount.x.toInt(), dragAmount.y.toInt())
                // 画像右端座標
                val imageRightX = nextOffset.x + imageSize.width
                // 画像下端座標
                val imageBottomY = nextOffset.y + imageSize.height
                if (nextOffset.x >= 0 && nextOffset.y >= 0 && imageRightX <= size.width && imageBottomY <= size.height) {
                    offset = nextOffset
                }
            }
        }) {
        // 背景
        drawRect(color = Color.White, size = size)
        drawImage(
            image = bitmap.asImageBitmap(),
            srcSize = imageSize,
            dstOffset = offset
        )
    }
}

@Composable
private fun TrimmingGrid(modifier: Modifier = Modifier) {
    val framePoint = Config.FRAME_SIZE.toFloat()
    val outLineStroke = 5F

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .focusable(enabled = false)
    ) {
        val left = (size.width - framePoint) / 2
        val top = (size.height - framePoint) / 2
        val originPoint = Offset(left, top)
        val gridOffsetList = listOf(
            Pair(
                Offset(left + framePoint / 3, top),
                Offset(left + framePoint / 3, top + framePoint)
            ),
            Pair(
                Offset(left + framePoint / 3 * 2, top),
                Offset(left + framePoint / 3 * 2, top + framePoint)
            ),
            Pair(
                Offset(left, top + framePoint / 3),
                Offset(left + framePoint, top + framePoint / 3)
            ),
            Pair(
                Offset(left, top + framePoint / 3 * 2),
                Offset(left + framePoint, top + framePoint / 3 * 2)
            )
        )

        // 外枠
        drawLine(
            color = Color.Red,
            start = originPoint,
            end = Offset(left, top + framePoint),
            strokeWidth = outLineStroke
        )
        drawLine(
            color = Color.Red,
            start = Offset(left, top + framePoint),
            end = Offset(size.width - left, top + framePoint),
            strokeWidth = outLineStroke
        )
        drawLine(
            color = Color.Red,
            start = Offset(size.width - left, top + framePoint),
            end = Offset(size.width - left, top),
            strokeWidth = outLineStroke
        )
        drawLine(
            color = Color.Red,
            start = originPoint,
            end = Offset(size.width - left, top),
            strokeWidth = outLineStroke
        )

        // グリッド
        gridOffsetList.forEach {
            drawLine(
                color = Color.Gray,
                start = it.first,
                end = it.second,
                pathEffect = PathEffect.dashPathEffect(intervals = floatArrayOf(10F, 10F))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTrimmingGrid() {
    TrimmingGrid()
}