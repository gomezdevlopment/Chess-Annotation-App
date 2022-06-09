package com.gomezdevlopment.chessnotationapp.view.game_screen

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel

@Composable
fun GameBar(viewModel: GameViewModel){
    Row(Modifier.wrapContentHeight()) {
        val onBackPressedDispatcher =
            LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
        Column(
            Modifier
                .weight(1f)
                .clickable {
                    onBackPressedDispatcher?.onBackPressed()
                    viewModel.resetGame()
                }) {

            val home: ImageVector =
                ImageVector.vectorResource(id = R.drawable.ic_home)
            Icon(
                imageVector = home,
                contentDescription = "Go to Home Screen",
                tint = tealDarker,
                modifier = Modifier
                    .height(50.dp)
                    .padding(10.dp)
                    .aspectRatio(1f)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Column(
            Modifier
                .weight(1f)
                .clickable {
                    viewModel.previousNotation()
                    //viewModel.undoMove()
                }
        ) {
            val leftArrow: ImageVector =
                ImageVector.vectorResource(id = R.drawable.ic_round_arrow_left)
            Icon(
                imageVector = leftArrow,
                contentDescription = "Previous Move",
                tint = tealDarker,
                modifier = Modifier
                    .height(50.dp)
                    .padding(10.dp)
                    .aspectRatio(1f)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Column(
            Modifier
                .weight(1f)
                .clickable {
                    viewModel.nextNotation()
                }) {
            val rightArrow: ImageVector =
                ImageVector.vectorResource(id = R.drawable.ic_round_arrow_right)
            Icon(
                imageVector = rightArrow,
                contentDescription = "Next Move",
                tint = tealDarker,
                modifier = Modifier
                    .height(50.dp)
                    .padding(10.dp)
                    .aspectRatio(1f)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}