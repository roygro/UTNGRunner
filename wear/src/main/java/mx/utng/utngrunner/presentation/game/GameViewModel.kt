package mx.utng.utngrunner.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.utng.utngrunner.data.health.HeartRateDataSource
import mx.utng.utngrunner.domain.model.GamePhase
import mx.utng.utngrunner.domain.model.GameState
import mx.utng.utngrunner.domain.model.Player
import mx.utng.utngrunner.domain.usecase.GetHighScoreUseCase
import mx.utng.utngrunner.domain.usecase.SaveHighScoreUseCase

enum class HapticType { JUMP, HIT }

class GameViewModel(
    private val getHighScore: GetHighScoreUseCase,
    private val saveHighScore: SaveHighScoreUseCase,
    private val heartRateSource: HeartRateDataSource
) : ViewModel() {
 
    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()
 
    private var gameFrame = 0L
    private var gameJob: Job? = null

    private val _hapticChannel = Channel<HapticType>(Channel.BUFFERED)
    val hapticEvents = _hapticChannel.receiveAsFlow()
 
    init {
        loadHighScore()
        observeHeartRate()
        viewModelScope.launch {
            heartRateSource.startMonitoring()
        }
    }
 
    fun startGame() {
        gameJob?.cancel()
        _state.value = GameState(
            phase = GamePhase.PLAYING,
            highScore = _state.value.highScore,
            heartRate = _state.value.heartRate
        )
        gameFrame = 0L
        gameJob = viewModelScope.launch {
            // 60 fps → ~16ms por frame
            while (_state.value.phase == GamePhase.PLAYING) {
                delay(16L)
                val previousLives = _state.value.lives
                _state.update { GameEngine.update(it, gameFrame++) }
                
                // Emitir feedback háptico si bajó la vida
                if (_state.value.lives < previousLives) {
                    _hapticChannel.trySend(HapticType.HIT)
                }
            }
            if (_state.value.phase == GamePhase.DEAD) {
                saveHighScore(_state.value.score)
            }
        }
    }
 
    fun onJump() {
        val s = _state.value
        when (s.phase) {
            GamePhase.IDLE, GamePhase.DEAD -> startGame()
            GamePhase.PLAYING -> {
                if (!s.player.isJumping && s.player.y >= Player.FLOOR_Y - 5f) {
                    _state.update { it.copy(player = it.player.copy(
                        velocityY = Player.JUMP_VELOCITY, isJumping = true
                    ))}
                    _hapticChannel.trySend(HapticType.JUMP)
                }
            }
            else -> {}
        }
    }
 
    fun onSlide() {
        if (_state.value.phase != GamePhase.PLAYING || _state.value.player.isJumping) return
        _state.update { it.copy(player = it.player.copy(
            slideFrames = Player.SLIDE_DURATION
        ))}
    }
 
    private fun loadHighScore() {
        viewModelScope.launch {
            val hs = getHighScore()
            _state.update { it.copy(highScore = hs) }
        }
    }
 
    private fun observeHeartRate() {
        viewModelScope.launch {
            heartRateSource.heartRate.collect { bpm ->
                _state.update { it.copy(heartRate = bpm) }
            }
        }
    }
 
    override fun onCleared() { 
        gameJob?.cancel() 
    }
}
