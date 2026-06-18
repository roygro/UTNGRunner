package mx.utng.utngrunner.presentation.game

import android.graphics.Color as AndroidColor
import android.graphics.Paint as AndroidPaint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import mx.utng.utngrunner.domain.model.Coin
import mx.utng.utngrunner.domain.model.GameState
import mx.utng.utngrunner.domain.model.Obstacle
import mx.utng.utngrunner.domain.model.Player
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.sin

/** GameRenderer: SOLO dibuja. No toca la lógica de juego. */
object GameRenderer {
    private val COLORS = GameColors()

    fun DrawScope.draw(state: GameState, frame: Long) {
        drawBackground()
        drawGround()
        drawCoins(state.coins, frame)
        drawObstacles(state.obstacles)
        drawPlayer(state.player, frame)
        drawHUD(state)
    }

    private fun DrawScope.drawBackground() {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(COLORS.backgroundStart, COLORS.backgroundEnd)
            ),
            topLeft = Offset.Zero,
            size = size
        )
    }

    private fun DrawScope.drawGround() {
        val groundY = Player.FLOOR_Y + 20f
        // Draw the dirt ground
        drawRect(
            color = COLORS.ground,
            topLeft = Offset(0f, groundY),
            size = Size(size.width, size.height - groundY)
        )
        // Draw the grass/top ground line
        drawRect(
            color = COLORS.groundTop,
            topLeft = Offset(0f, groundY),
            size = Size(size.width, 4f)
        )
    }

    private fun DrawScope.drawPlayer(player: Player, frame: Long) {
        val alpha = if (player.isInvincible && (frame / 4) % 2 == 0L) 0.3f else 1f
        // Si se está deslizando, baja la posición de dibujado
        val yOffset = if (player.isSliding) 10f else 0f
        val yPos = player.y + yOffset

        // Altura y ancho del cuerpo se reducen al deslizarse
        val bodyHeight = if (player.isSliding) 14f else 26f
        val bodyWidth = if (player.isSliding) 24f else 20f
        val bodyLeft = if (player.isSliding) player.x - 10f else player.x - 8f
        val bodyTop = if (player.isSliding) yPos - 6f else yPos - 12f

        // Cuerpo (engineer overall/clothes)
        drawRect(
            color = COLORS.player.copy(alpha = alpha),
            topLeft = Offset(bodyLeft, bodyTop),
            size = Size(bodyWidth, bodyHeight)
        )

        // Casco de seguridad UTNG (blue helmet - solo si no se está deslizando)
        if (!player.isSliding) {
            drawRect(
                color = COLORS.helmet.copy(alpha = alpha),
                topLeft = Offset(player.x - 6f, yPos - 22f),
                size = Size(16f, 10f)
            )
            // Visor o cara del personaje
            drawRect(
                color = Color.White.copy(alpha = alpha),
                topLeft = Offset(player.x + 2f, yPos - 18f),
                size = Size(6f, 5f)
            )
        }

        // Piernas corriendo (animación según frame)
        if (!player.isJumping) {
            val legSwing = sin(frame * 0.3f) * 6f
            // Pierna 1
            drawRect(
                color = COLORS.helmet.copy(alpha = alpha),
                topLeft = Offset(player.x - 4f, yPos + bodyHeight / 2f),
                size = Size(4f, 6f + if (legSwing > 0) legSwing else 0f)
            )
            // Pierna 2
            drawRect(
                color = COLORS.helmet.copy(alpha = alpha),
                topLeft = Offset(player.x + 6f, yPos + bodyHeight / 2f),
                size = Size(4f, 6f + if (legSwing < 0) -legSwing else 0f)
            )
        } else {
            // Dibujar piernas encogidas al saltar
            drawRect(
                color = COLORS.helmet.copy(alpha = alpha),
                topLeft = Offset(player.x - 4f, yPos + bodyHeight / 2f),
                size = Size(4f, 4f)
            )
            drawRect(
                color = COLORS.helmet.copy(alpha = alpha),
                topLeft = Offset(player.x + 4f, yPos + bodyHeight / 2f),
                size = Size(4f, 4f)
            )
        }
    }

    private fun DrawScope.drawObstacles(obstacles: List<Obstacle>) {
        val groundY = Player.FLOOR_Y + 20f
        obstacles.forEach { obstacle ->
            // Evitar dibujar si colisionó y fue movido fuera de pantalla
            if (obstacle.x <= -50f) return@forEach

            val paint = AndroidPaint().apply {
                color = AndroidColor.WHITE
                textSize = 14f
                isFakeBoldText = true
            }

            // Dibujar rectángulo del obstáculo
            drawRect(
                color = Color(0xFFC62828), // Rojo oscuro para bugs/exámenes
                topLeft = Offset(obstacle.x, groundY - obstacle.height),
                size = Size(obstacle.width.toFloat(), obstacle.height.toFloat())
            )

            // Dibujar etiqueta de texto (ej. "TAREA") sobre el obstáculo
            drawContext.canvas.nativeCanvas.drawText(
                obstacle.type.label,
                obstacle.x,
                groundY - obstacle.height - 4f,
                paint
            )
        }
    }

    private fun DrawScope.drawCoins(coins: List<Coin>, frame: Long) {
        coins.forEach { coin ->
            if (coin.collected) return@forEach
            
            // Animación de rotación usando un pulso sinusoidal
            val scaleX = sin(frame * 0.2f + coin.phase)
            val width = 12f * Math.abs(scaleX)
            
            // Moneda dorada (círculo ovalado)
            drawOval(
                color = COLORS.coin,
                topLeft = Offset(coin.x - width / 2f, coin.y - 6f),
                size = Size(width, 12f)
            )
        }
    }

    private fun DrawScope.drawHUD(state: GameState) {
        val cx = size.width / 2f
        val nativeCanvas = drawContext.canvas.nativeCanvas

        // Hora del sistema
        val timePaint = AndroidPaint().apply {
            color = AndroidColor.WHITE
            textSize = 14f
            textAlign = AndroidPaint.Align.CENTER
        }
        nativeCanvas.drawText(getSystemTime(), cx, 24f, timePaint)

        // Puntuación
        val scorePaint = AndroidPaint().apply {
            color = AndroidColor.YELLOW
            textSize = 16f
            textAlign = AndroidPaint.Align.CENTER
            isFakeBoldText = true
        }
        nativeCanvas.drawText("${state.score} pts", cx, size.height - 18f, scorePaint)

        // Ritmo Cardiaco (Health integration)
        val hrPaint = AndroidPaint().apply {
            color = AndroidColor.rgb(255, 128, 128)
            textSize = 12f
            textAlign = AndroidPaint.Align.RIGHT
        }
        nativeCanvas.drawText("❤️ ${state.heartRate}", size.width - 16f, 40f, hrPaint)

        // Vidas
        repeat(state.lives) { i ->
            drawHeart(16f + i * 16f, 36f)
        }
    }

    private fun DrawScope.drawHeart(x: Float, y: Float) {
        drawCircle(
            color = Color.Red,
            radius = 3.5f,
            center = Offset(x - 3f, y)
        )
        drawCircle(
            color = Color.Red,
            radius = 3.5f,
            center = Offset(x + 3f, y)
        )
        drawCircle(
            color = Color.Red,
            radius = 4.5f,
            center = Offset(x, y + 3f)
        )
    }

    private fun getSystemTime(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }
}
