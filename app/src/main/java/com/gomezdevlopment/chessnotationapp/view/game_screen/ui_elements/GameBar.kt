package com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements

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
import androidx.navigation.NavController
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.tealDarker
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel

@Composable
fun GameBar(viewModel: GameViewModel){
        Row(Modifier.wrapContentHeight(), verticalAlignment = Alignment.Bottom) {
//            Column(
//                Modifier
//                    .weight(1f)
//                    .clickable {
//                        navController.navigate("home")
//                        viewModel.isOnline.value = true
//                        //viewModel.resetGame()
//                    }) {
//
//                val home: ImageVector =
//                    ImageVector.vectorResource(id = R.drawable.ic_home)
//                Icon(
//                    imageVector = home,
//                    contentDescription = "Go to Home Screen",
//                    tint = tealDarker,
//                    modifier = Modifier
//                        .height(50.dp)
//                        .padding(10.dp)
//                        .aspectRatio(1f)
//                        .align(Alignment.CenterHorizontally)
//                )
//            }

            Column(
                Modifier
                    .weight(1f)
                    .clickable {
                        viewModel.openResignDialog.value = true
                    }) {

                val resign: ImageVector =
                    ImageVector.vectorResource(id = R.drawable.ic_resign)
                Icon(
                    imageVector = resign,
                    contentDescription = "Resign",
                    tint = tealDarker,
                    modifier = Modifier
                        .height(50.dp)
                        .padding(15.dp)
                        .aspectRatio(1f)
                        .align(Alignment.CenterHorizontally)
                )
            }

            Column(
                Modifier
                    .weight(1f)
                    .clickable {
                        viewModel.openDrawOfferDialog.value = true
                    }) {

                val home: ImageVector =
                    ImageVector.vectorResource(id = R.drawable.ic_draw_offer)
                Icon(
                    imageVector = home,
                    contentDescription = "Offer Draw",
                    tint = tealDarker,
                    modifier = Modifier
                        .height(50.dp)
                        .padding(5.dp)
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