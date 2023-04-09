package si.f5.mob.photoapp.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import si.f5.mob.mediastore.MediaStore
import si.f5.mob.photoapp.BaseViewModel
import si.f5.mob.photoapp.ImageRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mediaStore: MediaStore,
    private val imageRepository: ImageRepository,
    application: Application
) : BaseViewModel(application) {
    private val _imageList = MutableLiveData<List<GridItemData>>(listOf())
    val imageList: LiveData<List<GridItemData>>
        get() = _imageList

    private val _selectedImageCount = MutableLiveData(0)
    val selectedImageCount: LiveData<Int>
        get() = _selectedImageCount


    fun initialize() {
        _selectedImageCount.postValue(0)
    }

    fun getImageList() {
        val imageList = mediaStore.getImages().map { GridItemData(it) }
        _imageList.postValue(imageList)
    }

    fun setImageIsSelected(imageId: Long, value: Boolean) {
        val selectedImage = _imageList.value?.find { it.image.id == imageId }
            ?: throw IllegalStateException("選択した画像が存在しない")
        selectedImage.isSelected = value
        _imageList.postValue(_imageList.value)
        _selectedImageCount.postValue(_imageList.value?.filter { it.isSelected }?.size ?: 0)
        imageRepository.setSelectedImageList(_imageList.value!!.filter { it.isSelected }
            .map { it.image })
    }
}