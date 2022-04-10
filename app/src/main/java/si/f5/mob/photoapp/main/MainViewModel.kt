package si.f5.mob.photoapp.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import si.f5.mob.mediastore.MediaStore
import si.f5.mob.mediastore.entity.Image
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mediaStore: MediaStore,
) : ViewModel() {
    private val _imageList = MutableLiveData<List<Image>>(listOf())
    val imageList: LiveData<List<Image>> = _imageList

    fun getImageList() {
        val imageList = mediaStore.getImages()
        _imageList.postValue(imageList)
    }
}