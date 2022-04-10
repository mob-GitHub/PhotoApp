package si.f5.mob.photoapp.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import coil.compose.AsyncImage
import si.f5.mob.mediastore.entity.Image

@Composable
@ExperimentalFoundationApi
fun PhotoGrid(imageList: List<Image>) {
    // TODO:Configクラスに移動する
    val spanCount = 3
    val cells = GridCells.Fixed(spanCount)
    LazyVerticalGrid(cells = cells) {
        items(imageList) { image ->
            AsyncImage(model = image.uri, contentDescription = image.name)
        }
    }
}