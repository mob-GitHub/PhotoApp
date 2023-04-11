package si.f5.mob.photoapp.photoview

import android.app.Application
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
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
import si.f5.mob.mediastore.entity.Image
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
        val image: Image,
        val bitmap: Bitmap,
    )

    sealed interface Event {
        object None : Event
        class NavigateEditView(val imageId: Long) : Event
    }

    private val _clickedImageId = MutableLiveData<Long?>()
    val clickedImageId: LiveData<Long?>
        get() = _clickedImageId

    private val _event = MutableStateFlow<Event>(Event.None)
    val event: StateFlow<Event>
        get() = _event

    private val _imageList = MutableStateFlow<List<ImageFrame>>(listOf())
    val imageList: StateFlow<List<ImageFrame>>
        get() = _imageList

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
                result.drawable.toBitmap()
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
                result.drawable.toBitmap()
            }
            is ErrorResult -> {
                setError(IllegalStateException("画像取得に失敗"))
                null
            }
        }

        _imageList.update {
            listOf(
                ImageFrame(
                    image1,
                    result1!!
                ),
                ImageFrame(
                    image2,
                    result2!!
                )
            )
        }
    }

    fun clearEvent() {
        _event.update { Event.None }
    }

    fun onClickedCanvas(imageId: Long) {
        _event.update { Event.NavigateEditView(imageId) }
    }
}