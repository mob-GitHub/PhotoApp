package si.f5.mob.data.mediastore

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ActivityContext
import si.f5.mob.data.mediastore.entity.Image
import timber.log.Timber
import javax.inject.Inject

class MediaStore @Inject constructor(
    @ActivityContext private val context: Context,
) {
    companion object {
        /**
         * MediaStoreから1回あたりに取得する最大取得数
         */
        const val GET_IMAGE_MAX_ITEMS = 20
    }

    private val contentResolver: ContentResolver = context.contentResolver

    /**
     * MediaStoreから画像を取得
     * 追加日時降順
     * https://developer.android.com/training/data-storage/shared/media
     */
    fun getImages(
        maxItems: Int = GET_IMAGE_MAX_ITEMS,
    ): List<Image> {
        val imageList: MutableList<Image> = mutableListOf()

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val query = contentResolver.query(
            collection, projection, null, null, sortOrder
        )

        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                )

                val image = Image(uri, name)
                imageList.add(image)
                Timber.d(image.toString())
            }
        }

        return imageList
    }
}