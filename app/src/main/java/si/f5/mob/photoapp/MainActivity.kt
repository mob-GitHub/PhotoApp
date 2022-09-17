package si.f5.mob.photoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import si.f5.mob.photoapp.main.MainScreen
import si.f5.mob.photoapp.main.MainViewModel
import si.f5.mob.photoapp.photoview.ImageViewScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Scaffold(topBar = { PhotoAppTopAppBar() }) { paddingValues ->
                    val navController = rememberNavController()
                    val mainViewModel: MainViewModel by viewModels()
                    NavHost(
                        modifier = Modifier.padding(paddingValues),
                        navController = navController,
                        startDestination = "main"
                    ) {
                        composable("main") {
                            MainScreen(mainViewModel, navController)
                        }
                        composable("imageview/{imageId}") { navBackStackEntry ->
                            val imageId = navBackStackEntry.arguments?.getString("imageId")
                            val imageUri =
                                mainViewModel.imageList.value?.find { it.id == imageId?.toLong() }?.uri
                            ImageViewScreen(navController = navController, imageUri = imageUri)
                        }
                    }
                }
            }
        }
    }
}