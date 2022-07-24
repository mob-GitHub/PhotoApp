package si.f5.mob.photoapp.main

import android.Manifest
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import si.f5.mob.photoapp.component.PhotoGrid

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun MainScreen(viewModel: MainViewModel, navController: NavController) {
    val imageList = viewModel.imageList.observeAsState()

    // 権限チェック
    val permissionState =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    when (permissionState.status) {
        PermissionStatus.Granted -> {
            viewModel.getImageList()
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

    Scaffold { paddingValues ->
        imageList.value?.let {
            PhotoGrid(
                modifier = Modifier.padding(paddingValues.calculateBottomPadding()),
                imageList = it,
                navController = navController
            )
        }
    }
}