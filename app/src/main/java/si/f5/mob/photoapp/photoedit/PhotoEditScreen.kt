package si.f5.mob.photoapp.photoedit

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

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