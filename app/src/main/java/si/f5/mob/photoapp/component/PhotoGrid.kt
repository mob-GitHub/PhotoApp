package si.f5.mob.photoapp.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import si.f5.mob.mediastore.entity.Image
import timber.log.Timber

@Composable
@ExperimentalFoundationApi
fun PhotoGrid(imageList: List<Image>, navController: NavController) {
    // TODO:Configクラスに移動する
    val spanCount = 3
    val cells = GridCells.Fixed(spanCount)
    val widthPixels = LocalContext.current.resources.displayMetrics.widthPixels.dp
    Timber.d("widthPixels = $widthPixels")
    val spanWidth = (widthPixels / spanCount)
    Timber.d("spanWidth = $spanWidth")
    LazyVerticalGrid(cells = cells) {
        items(imageList) { image ->
            AsyncImage(
                model = image.uri,
                contentDescription = image.name,
                contentScale = ContentScale.None,
                // TODO:調整する
                modifier = Modifier
                    .height(120.dp)
                    .padding(4.dp)
                    .clickable {
                        navController.navigate("imageview/${image.id}")
                    }
            )
        }
    }
}