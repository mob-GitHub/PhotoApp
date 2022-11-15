package si.f5.mob.mediastore.entity

import android.net.Uri

/**
 * MediaStoreから取得する画像のエンティティクラス
 */
data class Image(
    val id: Long,
    val uri: Uri,
    val name: String,
    val width: Int,
    val height: Int,
)
