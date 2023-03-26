package si.f5.mob.photoapp.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import si.f5.mob.mediastore.MediaStore
import si.f5.mob.mediastore.entity.Image
import si.f5.mob.photoapp.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mediaStore: MediaStore,
    application: Application
) : BaseViewModel(application) {
    private val _imageList = MutableLiveData<List<Image>>(listOf())
    val imageList: LiveData<List<Image>>
        get() = _imageList

    fun getImageList() {
        val imageList = mediaStore.getImages()
        _imageList.postValue(imageList)
    }
}