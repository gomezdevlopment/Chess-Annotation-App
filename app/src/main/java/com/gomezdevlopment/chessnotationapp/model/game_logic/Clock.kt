package com.gomezdevlopment.chessnotationapp.model.game_logic

import android.os.CountDownTimer
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import com.gomezdevlopment.chessnotationapp.model.repositories.GameRepository
import com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements.formatTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Clock {

//    private var countDownTimer: CountDownTimer? = null
//    var whiteTimer = gameRepository.whiteTimer
//    var blackTimer = gameRepository.blackTimer
//    private val initialTime by gameRepository.initialTime
//    //
//    private var _whiteTime = MutableStateFlow(formatTime(whiteTimer.value))
//    val whiteTime: StateFlow<String> = _whiteTime
//    val whiteProgress: StateFlow<Float> = gameRepository.whiteProgress
//    private val _whiteTimeIsPlaying = MutableStateFlow(false)
//    val whiteTimeIsPlaying: StateFlow<Boolean> = _whiteTimeIsPlaying
//    //
//    private val _blackTime = MutableStateFlow(formatTime(blackTimer.value))
//    val blackTime: StateFlow<String> = _blackTime
//    val blackProgress: StateFlow<Float> = gameRepository.blackProgress
//    private val _blackTimeIsPlaying = MutableStateFlow(false)
//    val blackTimeIsPlaying: StateFlow<Boolean> = _blackTimeIsPlaying

    fun handleCountDownTimer(
        whiteTimeIsPlaying: StateFlow<Boolean>,
        _whiteTimeIsPlaying: MutableStateFlow<Boolean>,
        _whiteTime: MutableStateFlow<String>,
        _whiteProgress: MutableStateFlow<Float>,
        whiteTimer: MutableState<Long>,
        blackTimeIsPlaying: StateFlow<Boolean>,
        _blackTimeIsPlaying: MutableStateFlow<Boolean>,
        _blackTime: MutableStateFlow<String>,
        _blackProgress: MutableStateFlow<Float>,
        blackTimer: MutableState<Long>,
        initialTime: Long,
        gameRepository: GameRepository
    ) {
        if (whiteTimeIsPlaying.value) {
            pauseTimer(_whiteTimeIsPlaying, _whiteTime, _whiteProgress, whiteTimer, gameRepository.countDownTimer.value)
            startTimer(
                _blackTimeIsPlaying,
                _blackTime,
                _blackProgress,
                blackTimer,
                initialTime,
                gameRepository,
                gameRepository.countDownTimer
            )
        } else if (blackTimeIsPlaying.value) {
            pauseTimer(_blackTimeIsPlaying, _blackTime, _blackProgress, blackTimer, gameRepository.countDownTimer.value)
            startTimer(
                _whiteTimeIsPlaying,
                _whiteTime,
                _whiteProgress,
                whiteTimer,
                initialTime,
                gameRepository,
                gameRepository.countDownTimer
            )
        }
    }

    fun pauseTimer(
        _isPlaying: MutableStateFlow<Boolean>,
        _time: MutableStateFlow<String>,
        _progress: MutableStateFlow<Float>,
        time: MutableState<Long>,
        countDownTimer: CountDownTimer?
    ) {
        //time.value-=elapsedTime
        countDownTimer?.cancel()
        handleTimerValues(
            false,
            formatTime(time.value),
            _progress.value,
            _isPlaying,
            _time,
            _progress
        )
    }

    fun startTimer(
        _isPlaying: MutableStateFlow<Boolean>,
        _time: MutableStateFlow<String>,
        _progress: MutableStateFlow<Float>,
        time: MutableState<Long>,
        initialTime: Long,
        gameRepository: GameRepository,
        countDownTimer: MutableState<CountDownTimer?>
    ) {
        _isPlaying.value = true
        countDownTimer.value = object : CountDownTimer(time.value, 1) {

            override fun onTick(millisRemaining: Long) {
                time.value = millisRemaining
                _progress.value = millisRemaining / initialTime.toFloat()
                handleTimerValues(
                    true,
                    formatTime(millisRemaining),
                    _progress.value,
                    _isPlaying,
                    _time,
                    _progress
                )
            }

            override fun onFinish() {
                pauseTimer(_isPlaying, _time, _progress, time, countDownTimer.value)
                gameRepository.timeout()
            }
        }.start()
    }

    fun handleTimerValues(
        isPlaying: Boolean,
        text: String,
        progress: Float,
        _isPlaying: MutableStateFlow<Boolean>,
        _time: MutableStateFlow<String>,
        _progress: MutableStateFlow<Float>
    ) {
        _isPlaying.value = isPlaying
        _time.value = text
        _progress.value = progress
    }
}