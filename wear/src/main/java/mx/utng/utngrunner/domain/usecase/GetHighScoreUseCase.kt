package mx.utng.utngrunner.domain.usecase

import mx.utng.utngrunner.domain.repository.ScoreRepository

/** Caso de uso: encapsula UNA operación de negocio */
class GetHighScoreUseCase(private val repository: ScoreRepository) {
    suspend operator fun invoke(): Int = repository.getHighScore()
}
