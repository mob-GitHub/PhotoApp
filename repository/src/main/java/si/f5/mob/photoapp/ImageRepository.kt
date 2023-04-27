package si.f5.mob.photoapp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import si.f5.mob.common.di.IoDispatcher
import si.f5.mob.mediastore.MediaStore
import si.f5.mob.mediastore.entity.Image
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    private val mediaStore: MediaStore,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) {
    private val _imageList = MutableSharedFlow<List<Image>>()
    val imageList: Flow<List<Image>>
        get() = _imageList.distinctUntilChanged()

    private var _selectedImageList = mutableListOf<Image>()
    val selectedImageList: List<Image>
        get() = _selectedImageList

    suspend fun getImageList() = withContext(dispatcher) {
        _imageList.emit(mediaStore.getImages())
    }

    fun setSelectedImageList(value: List<Image>) {
        _selectedImageList = value.toMutableList()
    }

    suspend fun getImageUriById(id: Long): Image? = withContext(dispatcher) {
        return@withContext mediaStore.getImageUriById(id)
    }
}