package com.gomezdevlopment.chessnotationapp.model.game_logic

import android.os.CountDownTimer
import androidx.compose.runtime.MutableState
import com.gomezdevlopment.chessnotationapp.model.repositories.GameRepository
import com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements.formatTime
import kotlinx.coroutines.flow.MutableStateFlow

class Clock {

    fun pauseTimer(
        _time: MutableStateFlow<String>,
        _progress: MutableStateFlow<Float>,
        time: MutableState<Long>,
        countDownTimer: CountDownTimer?
    ) {
        countDownTimer?.cancel()
        handleTimerValues(
            formatTime(time.value),
            _progress.value,
            _time,
            _progress
        )
    }

    fun startTimer(
        _time: MutableStateFlow<String>,
        _progress: MutableStateFlow<Float>,
        time: MutableState<Long>,
        initialTime: Long,
        gameRepository: GameRepository,
        countDownTimer: MutableState<CountDownTimer?>
    ) {
        countDownTimer.value = object : CountDownTimer(time.value, 1) {

            override fun onTick(millisRemaining: Long) {
                time.value = millisRemaining
                _progress.value = millisRemaining / initialTime.toFloat()
                handleTimerValues(
                    formatTime(millisRemaining),
                    _progress.value,
                    _time,
                    _progress
                )
            }

            override fun onFinish() {
                pauseTimer(_time, _progress, time, countDownTimer.value)
                gameRepository.timeout()
            }
        }.start()
    }

    fun handleTimerValues(
        time: String,
        progress: Float,
        _time: MutableStateFlow<String>,
        _progress: MutableStateFlow<Float>
    ) {
        _time.value = time
        _progress.value = progress
    }
}