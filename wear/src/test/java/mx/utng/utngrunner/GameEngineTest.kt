package mx.utng.utngrunner

import mx.utng.utngrunner.domain.model.GamePhase
import mx.utng.utngrunner.domain.model.GameState
import mx.utng.utngrunner.domain.model.Obstacle
import mx.utng.utngrunner.domain.model.ObstacleType
import mx.utng.utngrunner.domain.model.Player
import mx.utng.utngrunner.presentation.game.GameEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {
 
    @Test
    fun `player falls due to gravity`() {
        val state = GameState(
            phase = GamePhase.PLAYING,
            player = Player(y = 100f, velocityY = 0f)
        )
        val next = GameEngine.update(state, frame = 0)
        assertTrue(next.player.y > 100f)  // cayó
    }
 
    @Test
    fun `score increases every frame`() {
        val state = GameState(phase = GamePhase.PLAYING, score = 0)
        val next = GameEngine.update(state, frame = 0)
        assertEquals(1, next.score)
    }
 
    @Test
    fun `level increases at score 300`() {
        val state = GameState(phase = GamePhase.PLAYING, score = 299)
        val next = GameEngine.update(state, frame = 0)
        assertEquals(2, next.level)
    }
 
    @Test
    fun `lives decrease on obstacle collision`() {
        val player = Player(y = Player.FLOOR_Y, isInvincible = false)
        val obstacle = Obstacle(
            x = player.x,
            width = ObstacleType.TAREA.w,
            height = ObstacleType.TAREA.h,
            type = ObstacleType.TAREA
        )
        val state = GameState(
            phase = GamePhase.PLAYING,
            player = player,
            obstacles = listOf(obstacle),
            lives = 3
        )
        val next = GameEngine.update(state, frame = 0)
        assertTrue(next.lives < 3)
    }
 
    @Test
    fun `game over when lives reach zero`() {
        val player = Player(y = Player.FLOOR_Y, isInvincible = false)
        val obstacle = Obstacle(
            x = player.x,
            width = ObstacleType.TAREA.w,
            height = ObstacleType.TAREA.h,
            type = ObstacleType.TAREA
        )
        val state = GameState(
            phase = GamePhase.PLAYING,
            player = player,
            obstacles = listOf(obstacle),
            lives = 1
        )
        val next = GameEngine.update(state, frame = 0)
        assertEquals(0, next.lives)
        assertEquals(GamePhase.DEAD, next.phase)
    }
}
