package mx.utng.utngrunner.domain.model

data class Coin(
    val x: Float,
    val y: Float,
    val phase: Float = 0f,
    val collected: Boolean = false
)
