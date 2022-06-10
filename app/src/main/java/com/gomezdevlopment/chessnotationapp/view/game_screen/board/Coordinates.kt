package com.gomezdevlopment.chessnotationapp.view.game_screen.board

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.tealDarker

@Composable()
fun Coordinates(size: Float) {
    val ranks = listOf("1", "2", "3", "4", "5", "6", "7", "8")
    val files = listOf("a", "b", "c", "d", "e", "f", "g", "h")

    ranks.forEachIndexed() { index, rank ->
        var color = Color.White
        if(index%2 == 0){
            color = tealDarker
        }
        val offsetY = (7 - index).toFloat() * size
        val offsetX = ((size * 8) - 8)
        Text(
            text = rank,
            Modifier
                .zIndex(3f)
                .absoluteOffset(offsetX.dp, offsetY.dp),
            color = color,
            fontSize = 9.sp
        )
    }

    files.forEachIndexed() { index, file ->
        var color = tealDarker
        if(index%2 == 0){
            color = Color.White
        }
        val offsetX = size * (index).toFloat() + 2f
        val offsetY = (size * 8f) - 13f
        Text(
            text = file,
            Modifier
                .zIndex(3f)
                .offset(offsetX.dp, offsetY.dp),
            color = color,
            fontSize = 9.sp
        )
    }

}