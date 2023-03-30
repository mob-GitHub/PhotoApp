package si.f5.mob.photoapp.main

import android.Manifest
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import si.f5.mob.common.Config
import si.f5.mob.mediastore.entity.Image
import si.f5.mob.photoapp.BuildConfig
import si.f5.mob.photoapp.PhotoAppTopAppBar
import si.f5.mob.photoapp.Screen

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(mainViewModel: MainViewModel = hiltViewModel(), navController: NavController) {
    val imageList: List<GridItemData> by mainViewModel.imageList.observeAsState(listOf())
    val selectedImageCount: Int by mainViewModel.selectedImageCount.observeAsState(0)

    // 権限チェック
    val permissionState =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    when (permissionState.status) {
        PermissionStatus.Granted -> {
            mainViewModel.initialize()
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
        Box {
            LazyVerticalGrid(
                columns = GridCells.Fixed(Config.PHOTO_GRID_SPAN_COUNT),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(items = imageList) { image ->
                    GridItem(
                        image = image.image,
                        selectedCountState = mainViewModel.selectedImageCount.observeAsState(0),
                        onClick = { id, isSelected ->
                            mainViewModel.setImageIsSelected(id, isSelected)
                        }
                    )
                }
            }
            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color.White),
                onClick = {
                    val imageId = imageList.filter { it.isSelected }
                    navController.navigate("photoview/${imageId[0].image.id}/${imageId[1].image.id}")
                },
                enabled = selectedImageCount == Config.PHOTO_SELECT_COUNT_MAX
            ) {
                Column {
                    Row {
                        Text(
                            text = "プレビュー画面へ",
                            fontSize = TextUnit(22F, TextUnitType.Sp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text(
                            text = "$selectedImageCount/2枚選択済",
                            fontSize = TextUnit(16F, TextUnitType.Sp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GridItem(
    modifier: Modifier = Modifier,
    image: Image,
    onClick: (id: Long, isSelected: Boolean) -> Unit,
    selectedCountState: State<Int>,
) {
    val isSelected = remember { mutableStateOf(false) }
    val selectedCount by selectedCountState

    Box(modifier = modifier
        .aspectRatio(1f)
        .clickable {
            if (selectedCount < Config.PHOTO_SELECT_COUNT_MAX || isSelected.value) {
                isSelected.value = !isSelected.value
                onClick(image.id, isSelected.value)
            }
        }
    ) {
        AsyncImage(
            model = image.uri,
            contentDescription = image.name,
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.None,
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
        ) {
            if (isSelected.value) {
                SelectIcon(imageVector = Icons.Default.CheckBox, tint = Color.Red)
            } else {
                SelectIcon(imageVector = Icons.Default.CheckBoxOutlineBlank)
            }
        }
    }
}

@Composable
private fun SelectIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    tint: Color = LocalContentColor.current
) {
    Icon(
        imageVector = imageVector,
        contentDescription = imageVector.name,
        modifier = modifier
            .background(Color.White),
        tint = tint
    )
}

@Preview
@Composable
fun GridItemPreview() {
    GridItem(
        image = Image(
            1L,
            Uri.parse("android.resource://${BuildConfig.APPLICATION_ID}/drawable/turtlerock"),
            "",
            400,
            400
        ),
        selectedCountState = remember { mutableStateOf(0) },
        onClick = { _, _ ->
        }
    )
}