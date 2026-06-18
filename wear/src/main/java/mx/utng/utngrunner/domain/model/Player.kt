package mx.utng.utngrunner.domain.model

data class Player(
    val x: Float          = 55f,
    val y: Float          = FLOOR_Y,   // posición Y actual
    val velocityY: Float  = 0f,        // velocidad vertical
    val isJumping: Boolean = false,
    val isSliding: Boolean = false,
    val slideFrames: Int   = 0,        // frames restantes de deslizamiento
    val isInvincible: Boolean = false,
    val invincibleFrames: Int = 0
) {
    companion object {
        const val FLOOR_Y = 160f
        const val JUMP_VELOCITY = -12f
        const val GRAVITY = 0.6f
        const val SLIDE_DURATION = 30
        const val INVINCIBLE_FRAMES = 80
    }
}
