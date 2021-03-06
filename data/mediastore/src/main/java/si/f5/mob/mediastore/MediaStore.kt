package si.f5.mob.mediastore

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ActivityContext
import si.f5.mob.mediastore.entity.Image
import timber.log.Timber
import javax.inject.Inject

class MediaStore @Inject constructor(
    @ActivityContext private val context: Context,
) {
    private val contentResolver: ContentResolver = context.contentResolver

    /**
     * MediaStoreから画像を取得
     * 追加日時降順
     * https://developer.android.com/training/data-storage/shared/media
     */
    fun getImages(): List<Image> {
        val imageList: MutableList<Image> = mutableListOf()

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val query = contentResolver.query(
            collection, projection, null, null, sortOrder
        )

        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val width = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val height = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                )
                val isVertical = width < height

                val image = Image(id, uri, name, width, height, isVertical)
                imageList.add(image)
                Timber.d(image.toString())
            }
        }

        return imageList
    }
}