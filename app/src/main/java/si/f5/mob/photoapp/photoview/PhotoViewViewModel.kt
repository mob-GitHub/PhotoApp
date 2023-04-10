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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import si.f5.mob.mediastore.MediaStore
import si.f5.mob.photoapp.BaseViewModel
import si.f5.mob.photoapp.ImageRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PhotoViewViewModel @Inject constructor(
    private val mediaStore: MediaStore,
    private val imageRepository: ImageRepository,
    application: Application
) : BaseViewModel(application) {

    data class ImageFrame(
        val bitmap: Bitmap,
        val size: IntSize,
        val originPoint: Point,
        val diagonalPoint: Point = Point(originPoint.x + size.width, originPoint.y + size.height),
    )

    sealed interface Event {
        object None : Event
        class NavigateEditView(val imageId: Long) : Event
    }

    private val _imageBitmap1 = MutableLiveData<Bitmap?>()
    val imageBitmap1: LiveData<Bitmap?>
        get() = _imageBitmap1

    private val _imageBitmap2 = MutableLiveData<Bitmap?>()
    val imageBitmap2: LiveData<Bitmap?>
        get() = _imageBitmap2

    private var _canvasSize: Size = Size(0F, 0F)

    private val _clickedImageId = MutableLiveData<Long?>()
    val clickedImageId: LiveData<Long?>
        get() = _clickedImageId

    private val _event = MutableStateFlow<Event>(Event.None)
    val event: StateFlow<Event>
        get() = _event

    fun getImageBitmap() = viewModelScope.launch {
        val imageList = imageRepository.selectedImageList

        val image1 = mediaStore.getImageUriById(imageList[0].id)
        val image2 = mediaStore.getImageUriById(imageList[1].id)
        if (image1 == null || image2 == null) {
            setError(IllegalStateException("画像情報がない"))
        }

        Timber.d("Image1(id = ${image1!!.id}, uri = ${image1.uri}, name = ${image1.name}, width = ${image1.width}, height = ${image1.height})")
        Timber.d("Image2(id = ${image2!!.id}, uri = ${image2.uri}, name = ${image2.name}, width = ${image2.width}, height = ${image2.height})")

        val request1 = ImageRequest.Builder(getApplication())
            .data(image1.uri)
            .build()
        val result1 = when (val result = ImageLoader(getApplication()).execute(request1)) {
            is SuccessResult -> {
                result.drawable
            }
            is ErrorResult -> {
                setError(IllegalStateException("画像取得に失敗"))
                null
            }
        }
        val request2 = ImageRequest.Builder(getApplication())
            .data(image2.uri)
            .build()
        val result2 = when (val result = ImageLoader(getApplication()).execute(request2)) {
            is SuccessResult -> {
                result.drawable
            }
            is ErrorResult -> {
                setError(IllegalStateException("画像取得に失敗"))
                null
            }
        }

        _imageBitmap1.postValue(result1?.toBitmapOrNull())
        _imageBitmap2.postValue(result2?.toBitmapOrNull())
    }

    fun clearEvent() {
        _event.update { Event.None }
    }

    fun onClickedCanvas(offset: Offset) {
        getImageFrames().forEachIndexed { index, imageFrame ->
            if (offset.x >= imageFrame.originPoint.x && offset.y >= imageFrame.originPoint.y
                && offset.x <= imageFrame.diagonalPoint.x && offset.y <= imageFrame.diagonalPoint.y
            ) {
                Timber.d("clickedIndex = $index")
                val imageId = imageRepository.selectedImageList[index].id
                _event.update { Event.NavigateEditView(imageId) }
            }
        }
    }

    fun setCanvasSize(canvasSize: Size) {
        _canvasSize = canvasSize
    }

    fun getImageFrames() = listOf(
        ImageFrame(imageBitmap1.value!!, getImageSize(_canvasSize), Point(50, 50)),
        ImageFrame(
            imageBitmap2.value!!,
            getImageSize(_canvasSize),
            Point(getImageSize(_canvasSize).width + 50, getImageSize(_canvasSize).height + 50)
        )
    )

    private fun getImageSize(canvasSize: Size) =
        IntSize(canvasSize.width.toInt() / 2 - 50, canvasSize.height.toInt() / 2 - 50)
}