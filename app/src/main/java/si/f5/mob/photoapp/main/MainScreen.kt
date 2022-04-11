package si.f5.mob.photoapp.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import si.f5.mob.photoapp.component.PhotoGrid

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel, navController: NavController) {
    val imageList = viewModel.imageList.observeAsState()

    viewModel.getImageList()

    Scaffold {
        imageList.value?.let {
            PhotoGrid(imageList = it, navController = navController)
        }
    }
}