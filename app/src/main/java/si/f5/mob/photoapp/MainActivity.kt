package si.f5.mob.photoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import si.f5.mob.photoapp.main.MainScreen
import si.f5.mob.photoapp.main.MainViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Scaffold(topBar = { PhotoAppTopAppBar() }) {
                    NavHost(navController = rememberNavController(), startDestination = "main") {
                        composable("main") {
                            val viewModel: MainViewModel = hiltViewModel()
                            MainScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}