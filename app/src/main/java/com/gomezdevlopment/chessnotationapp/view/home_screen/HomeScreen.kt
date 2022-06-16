package com.gomezdevlopment.chessnotationapp.view.home_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.teal
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.tealDarker
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.textWhite
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view.game_screen.board.GameScreen
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.cardWhite
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel


@Composable
fun Navigation(viewModel: GameViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { Home(navController) }
        composable("game") { GameScreen(viewModel, navController) }
        /*...*/
    }
}

@Composable
fun Home(navController: NavController) {
    val gameModes = arrayListOf("Rapid", "Blitz", "Bullet")
    val gameModeImageIDs =
        arrayListOf(R.drawable.ic_rapid, R.drawable.ic_blitz, R.drawable.ic_bullet)
    Column() {
        Spacer(modifier = Modifier.height(100.dp))
        Text(
            text = "Select Game Mode",
            modifier = Modifier.padding(10.dp),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h5,
            color = tealDarker
        )

        LazyRow() {
            itemsIndexed(gameModes) { index, mode ->
                GameSelectionCard(drawableID = gameModeImageIDs[index], title = mode) {
                    navController.navigate("game")
                }
            }
        }
    }
//    Column(
//        Modifier
//            .fillMaxWidth()
//            .fillMaxHeight()
//    ) {
//        BoxWithConstraints(
//            Modifier
//                .fillMaxWidth()
//                .fillMaxHeight()
//                .padding(0.dp, 50.dp),
//            contentAlignment = Alignment.TopCenter
//        ) {
//            Card(
//                Modifier
//                    .width(maxWidth * 0.9f)
//                    .wrapContentHeight()
//                    .clickable {
//                        navController.navigate("game")
//                    },
//                backgroundColor = tealDarker,
//                shape = RoundedCornerShape(10.dp),
//                elevation = 5.dp,
//            )
//            {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    Row(
//                        horizontalArrangement = Arrangement.Center,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            "Play Now",
//                            fontWeight = FontWeight.Bold,
//                            style = MaterialTheme.typography.h4,
//                            color = textWhite,
//                            modifier = Modifier.padding(20.dp)
//                        )
//                    }
//                    Row(
//                        horizontalArrangement = Arrangement.Center,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        val userIcon: ImageVector =
//                            ImageVector.vectorResource(id = R.drawable.ic_user_icon)
//                        Icon(
//                            imageVector = userIcon, contentDescription = "Play Game",
//                            Modifier
//                                .size(50.dp)
//                                .padding(10.dp), tint = textWhite
//                        )
//                        Text(
//                            "vs",
//                            fontWeight = FontWeight.Bold,
//                            style = MaterialTheme.typography.h4,
//                            color = textWhite,
//                            modifier = Modifier.padding(20.dp)
//                        )
//                        Icon(
//                            imageVector = userIcon, contentDescription = "Play Game",
//                            Modifier
//                                .size(50.dp)
//                                .padding(10.dp), tint = textWhite
//                        )
//                    }
//                }
//
//            }
//        }
//    }
}

//@Preview
@Composable
fun GameSelectionCard(drawableID: Int, title: String, onClick: () -> Unit) {
    Card(
        Modifier
            .width(200.dp)
            .wrapContentHeight()
            .padding(10.dp)
            .clickable {
                //navController.navigate("game")
            },
        backgroundColor = cardWhite,
        shape = RoundedCornerShape(10.dp),
        elevation = 5.dp,
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
                    color = tealDarker,
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
