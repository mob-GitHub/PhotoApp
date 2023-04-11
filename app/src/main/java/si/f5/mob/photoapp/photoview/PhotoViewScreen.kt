package si.f5.mob.photoapp.photoview

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Point
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoViewScreen(
    photoViewViewModel: PhotoViewViewModel = hiltViewModel(),
    navController: NavController,
) {
    var isGetImage by remember { mutableStateOf(false) }
    val error by photoViewViewModel.error.observeAsState()
    val event by photoViewViewModel.event.collectAsState()
    val imageList by photoViewViewModel.imageList.collectAsState()

    if (!isGetImage) {
        photoViewViewModel.getImageBitmap()
        isGetImage = true
    }

    when (event) {
        is PhotoViewViewModel.Event.None -> Unit
        is PhotoViewViewModel.Event.NavigateEditView -> {
            val imageId: Long = (event as PhotoViewViewModel.Event.NavigateEditView).imageId
            navController.navigate("${Screen.PhotoEdit.route}/$imageId")
            photoViewViewModel.clearEvent()
        }
    }

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
        Box(modifier = Modifier.padding(paddingValues)) {
            if (error == null) {
                if (imageList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center), text = "読み込み中..."
                        )
                    }
                } else {
                    ImageView()
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center), text = "画像の読み込みに失敗しました。"
                    )
                }
            }
        }
    }
}

/**
 * 閲覧用ImageView
 * 画像配置情報はPhotoViewModel
 */
@Composable
private fun ImageView(
    viewModel: PhotoViewViewModel = hiltViewModel(),
) {
    var imageSize by remember { mutableStateOf(IntSize.Zero) }
    var originPoints by remember { mutableStateOf(listOf<IntOffset>()) }

    Canvas(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(all = 10.dp)
            .border(width = 1.dp, color = Color.Gray)
            .clickable(role = Role.Image, onClick = {})
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    viewModel.imageList.value.forEachIndexed { index, imageFrame ->
                        val diagonalPoint = Point(
                            originPoints[index].x + size.width,
                            originPoints[index].y + size.height
                        )
                        if (offset.x >= originPoints[index].x && offset.y >= originPoints[index].y
                            && offset.x <= diagonalPoint.x && offset.y <= diagonalPoint.y
                        ) {
                            Timber.d("clickedIndex = $index")
                            viewModel.onClickedCanvas(imageFrame.image.id)
                        }
                    }
                }
            }
    ) {
        // 背景
        drawRect(color = Color.White, size = size)

        imageSize = IntSize(size.width.toInt() / 2 - 50, size.height.toInt() / 2 - 50)
        originPoints = listOf(
            IntOffset(50, 50),
            IntOffset(imageSize.width + 50, imageSize.height + 50)
        )

        viewModel.imageList.value.forEachIndexed { index, frame ->
            val matrix = Matrix()
            val scaleX = imageSize.width / frame.image.width
            val scaleY = imageSize.height / frame.image.height
            matrix.setScale(scaleX.toFloat(), scaleY.toFloat())
            val imageBitmap =
                Bitmap.createScaledBitmap(frame.bitmap, imageSize.width, imageSize.height, true)

            drawImage(
                image = imageBitmap.asImageBitmap(),
                srcSize = imageSize,
                dstOffset = originPoints[index]
            )
        }
    }
}