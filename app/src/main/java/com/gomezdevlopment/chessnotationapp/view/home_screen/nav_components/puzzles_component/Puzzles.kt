package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.gomezdevlopment.chessnotationapp.view.teal

@Composable
fun PuzzleScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .background(teal),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Puzzles", fontSize = 30.sp)
    }
}