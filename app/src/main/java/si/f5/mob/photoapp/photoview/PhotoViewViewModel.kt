package si.f5.mob.photoapp.photoview

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
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
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import si.f5.mob.mediastore.MediaStore
import si.f5.mob.photoapp.BaseViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PhotoViewViewModel @Inject constructor(
    private val mediaStore: MediaStore,
    @ApplicationContext private val context: Context,
    application: Application
) : BaseViewModel(application) {

    data class ImageFrame(
        val bitmap: Bitmap,
        val size: IntSize,
        val originPoint: Point,
        val diagonalPoint: Point = Point(originPoint.x + size.width, originPoint.y + size.height),
    )

    private val _imageBitmap1 = MutableLiveData<Bitmap?>()
    val imageBitmap1: LiveData<Bitmap?>
        get() = _imageBitmap1

    private val _imageBitmap2 = MutableLiveData<Bitmap?>()
    val imageBitmap2: LiveData<Bitmap?>
        get() = _imageBitmap2

    private val _rectList = MutableLiveData<List<Rect>?>(null)
    val rectList: LiveData<List<Rect>?>
        get() = _rectList

    private var _canvasSize: Size = Size(0F, 0F)

    private val _clickedImageId = MutableLiveData<Long?>()
    val clickedImageId: LiveData<Long?>
        get() = _clickedImageId

    fun getImageBitmap(imageId1: Long?, imageId2: Long?) = viewModelScope.launch {
        if (imageId1 == null || imageId2 == null) {
            setError(IllegalStateException("画像情報がない"))
        }

        val image1 = mediaStore.getImageUriById(imageId1!!)
        val image2 = mediaStore.getImageUriById(imageId2!!)
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

        getObjectDetection(image1.name, image1.uri)
        getObjectDetection(image2.name, image2.uri)
    }

    fun getObjectDetection(name: String, uri: Uri) {
        val options = ObjectDetectorOptions.Builder()
            // 単一画像解析モード（解析時間が長くなるが、正確性が向上）
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            // 複数オブジェクト検出（最大5つまで）
            .enableMultipleObjects()
            // オブジェクト分類
            .enableClassification()
            .build()
        val detector = ObjectDetection.getClient(options)
        val image = InputImage.fromFilePath(context, uri)

        detector.process(image)
            .addOnSuccessListener { list ->
                Timber.d("解析結果 - $name")
                list.forEachIndexed { index, detectedObject ->
                    Timber.d("  ${index + 1}個目")
                    val label = detectedObject.labels.find { it.index == index }
                    if (label != null) {
                        Timber.d("  ${label.text} - ${label.confidence * 100}%")
                    }
                    val rect = detectedObject.boundingBox
                    Timber.d("  左上(${rect.left}, ${rect.top}) 右下(${rect.right}, ${rect.bottom})")
                }
                val rectList =
                    if (_rectList.value != null) _rectList.value!!.toMutableList() else mutableListOf()
                if (list.isNotEmpty()) {
                    rectList.add(list[0].boundingBox)
                }
                _rectList.postValue(rectList)
            }
            .addOnFailureListener {
                Timber.e(it)
            }
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