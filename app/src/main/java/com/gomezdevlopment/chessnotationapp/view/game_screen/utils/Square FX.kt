package com.gomezdevlopment.chessnotationapp.view.game_screen.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square

@Composable
fun Highlight(
    height: Dp, square: Square, color: Color, transparency: Float
) {
    val offsetX = height * square.file
    val offsetY = height * (7 - square.rank)
    Canvas(
        modifier = Modifier
            .height(height)
            .aspectRatio(1f)
            .absoluteOffset(offsetX, offsetY)
            .zIndex(2f)
    ) {
        drawRect(
            color = color,
            size = size,
            alpha = transparency
        )
    }
}

@Composable
fun PossibleMove(
    height: Dp,
    square: Square,
    targetRank: MutableState<Int>,
    targetFile: MutableState<Int>,
    squareClicked: MutableState<Boolean>
) {
    val offsetX = height * square.file
    val offsetY = height * (7 - square.rank)
    Canvas(
        modifier = Modifier
            .height(height)
            .aspectRatio(1f)
            .absoluteOffset(offsetX, offsetY)
            .zIndex(2f)
            .clickable {
                targetRank.value = square.rank
                targetFile.value = square.file
                squareClicked.value = true
            }
    ) {
        drawCircle(
            color = blue,
            radius = size.width / 8,
            alpha = 1f
        )
    }
}

@Composable
fun PossibleCapture(
    height: Dp,
    square: Square,
    targetRank: MutableState<Int>,
    targetFile: MutableState<Int>,
    squareClicked: MutableState<Boolean>
) {
    val offsetX = height * square.file
    val offsetY = height * (7 - square.rank)
    Canvas(
        modifier = Modifier
            .height(height)
            .aspectRatio(1f)
            .absoluteOffset(offsetX, offsetY)
            .zIndex(2f)
            .clickable {
                targetRank.value = square.rank
                targetFile.value = square.file
                squareClicked.value = true
            }
    ) {
        drawCircle(
            color = Color.Red,
            radius = size.width * .4f,
            alpha = 1f,
            style = Stroke(size.width * .08f)
        )
    }
}

@Composable
fun Outline(
    height: Dp,
    square: Square,
    color: Color
) {
    val offsetX = height * square.file
    val offsetY = height * (7 - square.rank)
    Canvas(
        modifier = Modifier
            .height(height)
            .aspectRatio(1f)
            .absoluteOffset(offsetX, offsetY)
            .padding(1.dp)
            .zIndex(2f)

    ) {
        drawRect(
            color = color,
            size = size,
            alpha = 1f,
            style = Stroke(size.width * .05f)
        )
    }
}