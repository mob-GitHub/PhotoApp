package si.f5.mob.photoapp.photoedit

import android.app.Application
import android.graphics.Rect
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import si.f5.mob.mediastore.entity.Image
import si.f5.mob.photoapp.BaseViewModel
import si.f5.mob.photoapp.ImageRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PhotoEditViewModel @Inject constructor(
    private val imageRepository: ImageRepository,
    application: Application,
) : BaseViewModel(application) {
    private val _rectList = MutableLiveData<List<Rect>?>(null)
    val rectList: LiveData<List<Rect>?>
        get() = _rectList

    fun getImage(imageId: Long): Image {
        return imageRepository.selectedImageList.find { it.id == imageId }
            ?: throw IllegalStateException("画像情報が無い")
    }

    fun getObjectDetection(name: String, uri: Uri) {
        val context = getApplication<Application>().applicationContext
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
}