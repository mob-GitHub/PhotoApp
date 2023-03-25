package si.f5.mob.photoapp.photoview

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Point
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import si.f5.mob.mediastore.MediaStore
import si.f5.mob.photoapp.BaseViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PhotoViewViewModel @Inject constructor(
    private val mediaStore: MediaStore,
    application: Application
) : BaseViewModel(application) {

    data class ImageFrame(
        val size: IntSize,
        val originPoint: Point,
        val diagonalPoint: Point = Point(originPoint.x + size.width, originPoint.y + size.height),
    )

    private val _imageBitmap = MutableLiveData<Bitmap?>()
    val imageBitmap: LiveData<Bitmap?>
        get() = _imageBitmap

    private var _canvasSize: Size = Size(0F, 0F)

    private val _clickedImageId = MutableLiveData<Long?>()
    val clickedImageId: LiveData<Long?>
        get() = _clickedImageId

    fun getImageBitmap(imageId: Long?) = viewModelScope.launch {
        if (imageId == null) {
            _imageBitmap.postValue(null)
        }

        val image = mediaStore.getImageUriById(imageId!!)
        if (image == null) {
            _imageBitmap.postValue(null)
        }

        Timber.d("Image(id = ${image!!.id}, uri = ${image.uri}, name = ${image.name}, width = ${image.width}, height = ${image.height})")

        val request = ImageRequest.Builder(getApplication())
            .data(image.uri)
            .build()
        val result = when (val result = ImageLoader(getApplication()).execute(request)) {
            is SuccessResult -> {
                result.drawable
            }
            is ErrorResult -> {
                setError(IllegalStateException("画像取得に失敗"))
                null
            }
        }

        _imageBitmap.postValue(result?.toBitmapOrNull())
    }

    fun onClickedCanvas(offset: Offset) {
        getImageFrames().forEachIndexed { index, imageFrame ->
            if (offset.x >= imageFrame.originPoint.x && offset.y >= imageFrame.originPoint.y
                && offset.x <= imageFrame.diagonalPoint.x && offset.y <= imageFrame.diagonalPoint.y
            ) {
                Timber.d("clickedIndex = $index")
            }
        }
    }

    fun setCanvasSize(canvasSize: Size) {
        _canvasSize = canvasSize
    }

    fun getImageFrames() = listOf(
        ImageFrame(getImageSize(_canvasSize), Point(50, 50)),
        ImageFrame(
            getImageSize(_canvasSize),
            Point(getImageSize(_canvasSize).width + 50, getImageSize(_canvasSize).height + 50)
        )
    )

    private fun getImageSize(canvasSize: Size) =
        IntSize(canvasSize.width.toInt() / 2 - 50, canvasSize.height.toInt() / 2 - 50)
}