package mx.utng.utngrunner.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.utng.utngrunner.presentation.game.GameScreen
import mx.utng.utngrunner.presentation.game.GameViewModel
import mx.utng.utngrunner.presentation.game.GameViewModelFactory
import mx.utng.utngrunner.presentation.theme.UTNGRunnerTheme

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: GameViewModel = viewModel(
                factory = GameViewModelFactory(applicationContext)
            )
            UTNGRunnerTheme {
                GameScreen(viewModel = viewModel)
            }
        }
    }
}
