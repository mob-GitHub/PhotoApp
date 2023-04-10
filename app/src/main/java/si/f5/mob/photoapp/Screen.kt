package si.f5.mob.photoapp

import androidx.annotation.StringRes

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Main : Screen("main", R.string.screen_main)
    object PhotoView : Screen("photoview", R.string.screen_photo_view)
    object PhotoEdit : Screen("photoedit", R.string.screen_photo_edit)
}
