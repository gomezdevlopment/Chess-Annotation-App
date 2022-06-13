package com.gomezdevlopment.chessnotationapp.view.home_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel


@Composable
fun Navigation(viewModel: GameViewModel){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { Home(navController) }
        composable("game") { GameScreen(viewModel, navController) }
        /*...*/
    }
}

@Composable
fun Home(navController: NavController) {
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
                        navController.navigate("game")
                    }
                ,
                backgroundColor = tealDarker,
                shape = RoundedCornerShape(10.dp),
                elevation = 5.dp,
            )
            {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Play Now",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.h4,
                            color = textWhite,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        val userIcon: ImageVector = ImageVector.vectorResource(id = R.drawable.ic_user_icon)
                        Icon(imageVector = userIcon, contentDescription = "Play Game",
                            Modifier
                                .size(50.dp)
                                .padding(10.dp), tint = textWhite)
                        Text(
                            "vs",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.h4,
                            color = textWhite,
                            modifier = Modifier.padding(20.dp)
                        )
                        Icon(imageVector = userIcon, contentDescription = "Play Game",
                            Modifier
                                .size(50.dp)
                                .padding(10.dp), tint = textWhite)
                    }
                }

            }
        }
    }
}
