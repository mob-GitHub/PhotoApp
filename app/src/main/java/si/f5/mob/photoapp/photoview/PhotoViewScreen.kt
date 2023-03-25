package si.f5.mob.photoapp.photoview

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import si.f5.mob.photoapp.PhotoAppTopAppBar
import si.f5.mob.photoapp.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoViewScreen(
    photoViewViewModel: PhotoViewViewModel = hiltViewModel(),
    navController: NavController,
    imageId: Long?
) {
    val error by photoViewViewModel.error.observeAsState()
    val imageBitmap: Bitmap? by photoViewViewModel.imageBitmap.observeAsState()

    photoViewViewModel.getImageBitmap(imageId = imageId)

    Scaffold(topBar = {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val navButtonVisible = navBackStackEntry?.destination?.route != Screen.PhotoView.route
        PhotoAppTopAppBar(
            title = stringResource(Screen.PhotoView.resourceId),
            navButtonVisible = navButtonVisible,
            navButtonOnClick = {
                navController.popBackStack()
            },
        )
    }) { paddingValues ->
        BoxWithConstraints(modifier = Modifier.padding(paddingValues)) {
            if (error == null) {
                when (imageBitmap) {
                    null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center), text = "読み込み中..."
                            )
                        }
                    }
                    else -> {
                        ImageView(bitmap = imageBitmap!!)
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center), text = "画像の読み込みに失敗しました。"
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageView(
    bitmap: Bitmap,
    viewModel: PhotoViewViewModel = hiltViewModel(),
) {
    Canvas(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(all = 10.dp)
            .border(width = 1.dp, color = Color.Gray)
            .clickable(role = Role.Image, onClick = {})
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    viewModel.onClickedCanvas(offset)
                }
            }
    ) {
        // ViewModelにCanvasサイズを通知
        viewModel.setCanvasSize(size)

        // 背景
        drawRect(color = Color.White, size = size)

        viewModel.getImageFrames().forEach { frame ->
            drawImage(
                image = bitmap.asImageBitmap(),
                srcSize = frame.size,
                dstOffset = IntOffset(frame.originPoint.x, frame.originPoint.y)
            )
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