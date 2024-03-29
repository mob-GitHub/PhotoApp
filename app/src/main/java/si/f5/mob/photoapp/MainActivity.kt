package si.f5.mob.photoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import si.f5.mob.photoapp.main.MainScreen
import si.f5.mob.photoapp.photoview.PhotoViewScreen

@AndroidEntryPoint
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Scaffold { paddingValues ->
                    val navController = rememberNavController()
                    NavHost(
                        modifier = Modifier.padding(paddingValues),
                        navController = navController,
                        startDestination = Screen.Main.route
                    ) {
                        composable(Screen.Main.route) {
                            MainScreen(navController = navController)
                        }
                        composable("${Screen.PhotoView.route}/{imageId1}/{imageId2}") {
                            val imageId1 = it.arguments?.getString("imageId1")?.toLong()
                            val imageId2 = it.arguments?.getString("imageId2")?.toLong()
                            PhotoViewScreen(
                                navController = navController,
                                imageId1 = imageId1,
                                imageId2 = imageId2
                            )
                        }
                    }
                }
            }
        }
    }
}