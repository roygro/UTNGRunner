package mx.utng.utngrunner.presentation.game

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.health.services.client.HealthServices
import mx.utng.utngrunner.data.datasource.PreferencesDataSource
import mx.utng.utngrunner.data.health.HeartRateDataSource
import mx.utng.utngrunner.data.repository.ScoreRepositoryImpl
import mx.utng.utngrunner.domain.usecase.GetHighScoreUseCase
import mx.utng.utngrunner.domain.usecase.SaveHighScoreUseCase

class GameViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
 
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Construir el grafo de dependencias de forma explícita
        val healthClient = HealthServices.getClient(context)
        val heartRateDs  = HeartRateDataSource(healthClient)
        val prefsDs      = PreferencesDataSource(context)
        val repository   = ScoreRepositoryImpl(prefsDs)
        return GameViewModel(
            getHighScore  = GetHighScoreUseCase(repository),
            saveHighScore = SaveHighScoreUseCase(repository),
            heartRateSource = heartRateDs
        ) as T
    }
}
