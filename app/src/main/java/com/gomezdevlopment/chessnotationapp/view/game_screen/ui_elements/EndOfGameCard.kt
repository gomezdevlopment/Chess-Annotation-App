package com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel

@Composable
fun EndOfGameCard(
    header: String,
    message: String,
    cardVisible: MutableState<Boolean>,
    viewModel: GameViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(id = R.color.transparentBlack))
            .zIndex(4f)
    )
    {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(50.dp),
            elevation = 10.dp,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(50.dp))
                    Text(
                        text = header,
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        modifier = Modifier.absolutePadding(10.dp)
                    )
                    Text(text = message, textAlign = TextAlign.Center, fontSize = 14.sp)
                }
                Column(modifier = Modifier.fillMaxWidth()) {
                    RoundDialogButton("Close")
                    {
                        cardVisible.value = false
                    }
                    RoundDialogButton("Rematch")
                    {
                        cardVisible.value = false
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun RoundDialogButton(
    buttonText: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,

        shape = CircleShape,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 5.dp)
            .wrapContentHeight(),
        border = BorderStroke(2.dp, colorResource(id = R.color.teal)),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.White,
            contentColor = colorResource(id = R.color.teal)
        )
    )
    {
        Text(buttonText)
    }
}