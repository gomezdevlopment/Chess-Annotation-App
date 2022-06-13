package com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.tealDarker
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel
import java.util.concurrent.TimeUnit
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.StateFlow

@Composable
fun Clock(viewModel: GameViewModel) {

}

fun formatTime(time: Long): String {
    val timeFormat = "%02d:%02d"
    return String.format(
        timeFormat,
        TimeUnit.MILLISECONDS.toMinutes(time),
        TimeUnit.MILLISECONDS.toSeconds(time) % 60
    )
}

@Composable
fun CountDownIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    time: String,
    size: Dp,
    stroke: Int
) {

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )

    Column(modifier = modifier) {
        Box {

            CircularProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .height(size)
                    .width(size),
                color = tealDarker,
                strokeWidth = stroke.dp,
            )

            Column(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    text = time,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun CountDownView(
    viewModel: GameViewModel,
    size: Dp,
    playerStartingTime: Long,
    playerTime: StateFlow<String>,
    playerProgress: StateFlow<Float>,
    playerTimeIsPlaying: StateFlow<Boolean>
) {

    val time by playerTime.collectAsState(formatTime(playerStartingTime))
    val progress by playerProgress.collectAsState(1.00F)
    val isPlaying by playerTimeIsPlaying.collectAsState(false)
    
    CountDownViewWithData(
        size = size,
        time = time,
        progress = progress,
        isPlaying = isPlaying
    ) {
        viewModel.handleCountDownTimer()
    }

}

@Composable
fun CountDownViewWithData(
    size: Dp,
    time: String,
    progress: Float,
    isPlaying: Boolean,
    optionSelected: () -> Unit,
) {
    CountDownIndicator(
        Modifier.padding(top = 5.dp),
        progress = progress,
        time = time,
        size = size,
        stroke = 5
    )
}