package com.gomezdevlopment.chessnotationapp.view.home_screen

import android.app.Application
import android.content.Context
import android.widget.Space
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.tealDarker
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.textWhite
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.userColor
import com.gomezdevlopment.chessnotationapp.view.game_screen.board.GameScreen
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.cardWhite
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel
import com.gomezdevlopment.chessnotationapp.view_model.MatchmakingViewModel
import com.gomezdevlopment.chessnotationapp.view_model.SignOutViewModel


@Composable
fun Settings(signOutViewModel: SignOutViewModel, navController: NavController) {
    Column(verticalArrangement = Arrangement.Center) {
        Button(
            onClick = {
                signOutViewModel.signOut()
                navController.navigate(R.id.action_settingsFragment_to_signInFragment)
            }
        ) {
            Text("Sign Out")
        }
    }
}

@Composable
fun MatchSearch(
    gameViewModel: GameViewModel,
    matchmakingViewModel: MatchmakingViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Searching for Match...",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
                Spacer(modifier = Modifier.height(20.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(100.dp)
                        .aspectRatio(1f),
                    strokeWidth = 5.dp,
                    color = tealDarker
                )
            }

        }
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(0.dp, 20.dp)) {
            TextButton(onClick = { matchmakingViewModel.cancelSearch() }) {
                Text(
                    text = "Cancel",
                    fontWeight = FontWeight.ExtraBold,
                    color = tealDarker,
                    fontSize = 24.sp
                )
            }
        }
    }
    NavigationFromMatchSearch(
        gameViewModel = gameViewModel,
        matchmakingViewModel = matchmakingViewModel,
        navController = navController
    )
}

@Composable
fun NavigationFromMatchSearch(
    gameViewModel: GameViewModel,
    matchmakingViewModel: MatchmakingViewModel,
    navController: NavController
) {
    when (matchmakingViewModel.navDestination.value) {
        "game" -> LaunchedEffect(Unit) {
            navController.navigate("game")
            gameViewModel.createNewGame(matchmakingViewModel.time, gameViewModel.isOnline.value)
        }
        "home" -> LaunchedEffect(Unit) {
            navController.navigate("home")
            matchmakingViewModel.navDestination.value = ""
        }
    }
}

@Composable
fun Navigation(
    gameViewModel: GameViewModel,
    matchmakingViewModel: MatchmakingViewModel,
    signOutViewModel: SignOutViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { Home(navController, matchmakingViewModel, gameViewModel) }
        composable("game") { GameScreen(gameViewModel, navController) }
        composable("settings") { Settings(signOutViewModel, navController) }
        composable("matchSearch") {
            MatchSearch(
                gameViewModel,
                matchmakingViewModel,
                navController
            )
        }
    }
}

@Composable
fun Home(
    navController: NavController,
    matchmakingViewModel: MatchmakingViewModel,
    gameViewModel: GameViewModel
) {
    val gameModes = arrayListOf("Rapid", "Blitz", "Bullet")
    val timeControls = arrayListOf(600000L, 300000L, 60000L)
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
                    matchmakingViewModel.joinGame(timeControls[index])
                    navController.navigate("matchSearch")
                }
            }
        }
        VSPlayerCard(navController = navController, viewModel = gameViewModel)
//        Spacer(modifier = Modifier.height(100.dp))
//        Button(onClick = { navController.navigate("settings") }) {
//            Text("Settings")
//        }

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
                        userColor = "white"
                        viewModel.isOnline.value = false
                        navController.navigate("game")
                    },
                backgroundColor = tealDarker,
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
