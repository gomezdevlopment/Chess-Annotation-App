package com.gomezdevlopment.chessnotationapp.view.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gomezdevlopment.chessnotationapp.view.theming.tealDarker

@Composable
fun Loading() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .width(100.dp)
                .aspectRatio(1f),
            strokeWidth = 5.dp,
            color = tealDarker
        )
    }

}
