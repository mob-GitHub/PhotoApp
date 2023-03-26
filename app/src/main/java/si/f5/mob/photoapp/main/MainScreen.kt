package si.f5.mob.photoapp.main

import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import si.f5.mob.common.Config
import si.f5.mob.photoapp.PhotoAppTopAppBar
import si.f5.mob.photoapp.Screen

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(mainViewModel: MainViewModel = hiltViewModel(), navController: NavController) {
    val imageList = mainViewModel.imageList.observeAsState()

    // 権限チェック
    val permissionState =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    when (permissionState.status) {
        PermissionStatus.Granted -> {
            mainViewModel.getImageList()
        }
        is PermissionStatus.Denied -> {
            AlertDialog(
                onDismissRequest = {},
                title = { Text(text = "写真へのアクセス許可のお願い") },
                text = { Text(text = "写真へのアクセスを許可してください") },
                confirmButton = {
                    TextButton(onClick = {
                        permissionState.launchPermissionRequest()
                    }) {
                        Text(text = "OK")
                    }
                }
            )
        }
    }

    Scaffold(topBar = {
        PhotoAppTopAppBar(
            title = stringResource(Screen.Main.resourceId),
            navButtonVisible = false
        )
    }) { paddingValues ->
        BoxWithConstraints {
            imageList.value?.let {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(Config.PHOTO_GRID_SPAN_COUNT),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(items = it) { image ->
                        AsyncImage(
                            model = image.uri,
                            contentDescription = image.name,
                            contentScale = ContentScale.Crop,
                            filterQuality = FilterQuality.None,
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable {
                                    navController.navigate("photoview/${image.id}")
                                }
                        )
                    }
                }
            }
        }
    }
}