package mx.utng.utngrunner.data.repository

import mx.utng.utngrunner.data.datasource.PreferencesDataSource
import mx.utng.utngrunner.domain.repository.ScoreRepository

/** Implementación concreta — la capa de datos implementa la interfaz del dominio */
class ScoreRepositoryImpl(
    private val dataSource: PreferencesDataSource
) : ScoreRepository {
 
    override suspend fun getHighScore(): Int =
        dataSource.getHighScore()
 
    override suspend fun saveHighScore(score: Int) =
        dataSource.saveHighScore(score)
}
