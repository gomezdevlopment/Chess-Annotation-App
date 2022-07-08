package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.play_component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import com.gomezdevlopment.chessnotationapp.view.theming.tealDarker
import com.gomezdevlopment.chessnotationapp.view.theming.textWhite
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel
import com.gomezdevlopment.chessnotationapp.view_model.MatchmakingViewModel


@Composable
fun PlayScreen(
    navController: NavController,
    matchmakingViewModel: MatchmakingViewModel,
    gameViewModel: GameViewModel
) {
    val gameModes = arrayListOf("Rapid", "Blitz", "Bullet")
    val timeControls = arrayListOf(600000L, 300000L, 60000L)
    val gameModeImageIDs =
        arrayListOf(R.drawable.ic_rapid, R.drawable.ic_blitz, R.drawable.ic_bullet)
    Column() {
        Column(Modifier.weight(1f)) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Select Game Mode",
                modifier = Modifier.padding(20.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h5,
            )

            LazyRow(Modifier.padding(10.dp, 10.dp)) {
                itemsIndexed(gameModes) { index, mode ->
                    GameSelectionCard(drawableID = gameModeImageIDs[index], title = mode) {
                        matchmakingViewModel.joinGame(timeControls[index])
                        navController.navigate("matchSearch")
                    }
                }
            }
            VSPlayerCard(navController = navController, viewModel = gameViewModel)
        }
    }
}

@Composable
fun VSPlayerCard(navController: NavController, viewModel: GameViewModel) {
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        BoxWithConstraints(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(0.dp, 50.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                Modifier
                    .width(maxWidth * 0.9f)
                    .wrapContentHeight()
                    .clickable {
                        MainActivity.userColor = "white"
                        viewModel.isOnline.value = false
                        navController.navigate("game")
                    },
                backgroundColor = tealDarker,
                shape = RoundedCornerShape(3.dp),
                elevation = 3.dp,
            )
            {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Play Now",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.h4,
                            color = textWhite,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val userIcon: ImageVector =
                            ImageVector.vectorResource(id = R.drawable.ic_user_icon)
                        Icon(
                            imageVector = userIcon, contentDescription = "Play Game",
                            Modifier
                                .size(50.dp)
                                .padding(10.dp), tint = textWhite
                        )
                        Text(
                            "vs",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.h4,
                            color = textWhite,
                            modifier = Modifier.padding(20.dp)
                        )
                        Icon(
                            imageVector = userIcon, contentDescription = "Play Game",
                            Modifier
                                .size(50.dp)
                                .padding(10.dp), tint = textWhite
                        )
                    }
                }

            }
        }
    }
}

//@Preview
@Composable
fun GameSelectionCard(drawableID: Int, title: String, onClick: () -> Unit) {
    Card(
        Modifier
            .width(200.dp)
            .wrapContentHeight()
            .padding(10.dp),
        backgroundColor = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(3.dp),
        elevation = 3.dp,
    )
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(20.dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val userIcon: ImageVector = ImageVector.vectorResource(id = drawableID)
                Icon(
                    imageVector = userIcon, contentDescription = "Play Game",
                    Modifier
                        .size(50.dp)
                        .padding(1.dp), tint = tealDarker
                )
            }
            Spacer(modifier = Modifier.height(50.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = tealDarker),
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text("New Game", color = textWhite)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

    }
}