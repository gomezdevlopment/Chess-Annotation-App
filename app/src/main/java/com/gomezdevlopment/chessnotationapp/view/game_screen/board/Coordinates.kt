package com.gomezdevlopment.chessnotationapp.view.game_screen.board

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.gomezdevlopment.chessnotationapp.view.tealDarker

@Composable()
fun Coordinates(size: Dp) {
    Ranks(size = size, darkColor = tealDarker, lightColor = Color.White)
    Files(size = size, darkColor = tealDarker, lightColor = Color.White)
}

@Composable
fun Ranks(
    size: Dp, darkColor: Color, lightColor: Color
) {
    val ranks = listOf("1", "2", "3", "4", "5", "6", "7", "8")
    ranks.asReversed().forEachIndexed() { index, rank ->
        var color = lightColor
        if (index % 2 == 0) {
            color = darkColor
        }

        val offsetX = (size * 7f)
        val offsetY = size * (7 - index).toFloat()

        Column(
            modifier = Modifier
                .height(size)
                .aspectRatio(1f)
                .absoluteOffset(offsetX, offsetY)
                .zIndex(3f),
            verticalArrangement = Arrangement.Top
        ) {
            Text(text = rank, color = color, fontSize = 9.sp, modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth(), textAlign = TextAlign.End)
        }
    }
}

@Composable
fun Files(
    size: Dp, darkColor: Color, lightColor: Color
) {
    val files = listOf("a", "b", "c", "d", "e", "f", "g", "h")
    files.asReversed().forEachIndexed { index, file ->
        var color = darkColor
        if (index % 2 == 0) {
            color = lightColor
        }
        val offsetX = size * (index).toFloat()
        val offsetY = (size * 7f)

        Column(
            modifier = Modifier
                .height(size)
                .aspectRatio(1f)
                .absoluteOffset(offsetX, offsetY)
                .zIndex(3f),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(text = file, color = color, fontSize = 9.sp, modifier = Modifier.padding(2.dp))
        }
    }
}