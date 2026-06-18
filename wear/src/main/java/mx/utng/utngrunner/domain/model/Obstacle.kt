package mx.utng.utngrunner.domain.model

data class Obstacle(
    val x: Float,
    val width: Int,
    val height: Int,
    val type: ObstacleType
)

enum class ObstacleType(val label: String, val w: Int, val h: Int) {
    TAREA("TAREA", 20, 35),
    EXAMEN("EXAMEN", 14, 50),
    BUG("BUG", 22, 22),
    REPO("REPO", 30, 18)
}
