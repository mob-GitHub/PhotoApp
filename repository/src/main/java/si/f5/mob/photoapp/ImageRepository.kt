package si.f5.mob.photoapp

import si.f5.mob.mediastore.entity.Image
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor() {
    private var _selectedImageList = mutableListOf<Image>()
    val selectedImageList: List<Image>
        get() = _selectedImageList

    fun setSelectedImageList(value: List<Image>) {
        _selectedImageList = value.toMutableList()
    }
}