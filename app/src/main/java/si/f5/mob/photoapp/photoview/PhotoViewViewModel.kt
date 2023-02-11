package si.f5.mob.photoapp.photoview

import android.app.Application
import android.graphics.Bitmap
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
    private val _imageBitmap = MutableLiveData<Bitmap?>()
    val imageBitmap: LiveData<Bitmap?>
        get() = _imageBitmap

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
}