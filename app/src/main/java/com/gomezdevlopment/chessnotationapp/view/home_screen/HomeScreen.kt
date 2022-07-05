package com.gomezdevlopment.chessnotationapp.view.home_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gomezdevlopment.chessnotationapp.view.*
import com.gomezdevlopment.chessnotationapp.view.game_screen.board.GameScreen
import com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component.GameReview
import com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.play_component.PlayScreen
import com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.puzzles_component.PuzzleScreen
import com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component.UserScreen
import com.gomezdevlopment.chessnotationapp.view_model.*

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
            matchmakingViewModel.navDestination.value = ""
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
    signOutViewModel: SignOutViewModel,
    puzzleViewModel: PuzzleViewModel,
    userViewModel: UserViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { Home(navController, matchmakingViewModel, gameViewModel, puzzleViewModel, userViewModel) }
        composable("game") { GameScreen(gameViewModel, navController) }
        composable("gameReview") { GameReview(gameViewModel, navController) }
        //composable("settings") { Settings(signOutViewModel, navController) }
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
fun HomeBottomNavigation(
    navController: NavHostController,
    playNavController: NavController,
    matchmakingViewModel: MatchmakingViewModel,
    gameViewModel: GameViewModel,
    puzzleViewModel: PuzzleViewModel,
    userViewModel: UserViewModel
) {
    NavHost(navController = navController, startDestination = "play") {
        composable("play") { PlayScreen(playNavController, matchmakingViewModel, gameViewModel) }
        composable("puzzles") {
            LaunchedEffect(key1 = true){
                puzzleViewModel.initializePuzzles()
            }
            PuzzleScreen(puzzleViewModel)
        }
        composable("user") { UserScreen(userViewModel, playNavController, gameViewModel) }
    }
}

@Composable
fun Home(
    navController: NavController,
    matchmakingViewModel: MatchmakingViewModel,
    gameViewModel: GameViewModel,
    puzzleViewModel: PuzzleViewModel,
    userViewModel: UserViewModel
) {
    val bottomNavBarController = rememberNavController()

    Scaffold(bottomBar = { BottomNavBar(navController = bottomNavBarController) }) {
        HomeBottomNavigation(
            navController = bottomNavBarController,
            playNavController = navController,
            matchmakingViewModel = matchmakingViewModel,
            gameViewModel = gameViewModel,
            puzzleViewModel = puzzleViewModel,
            userViewModel = userViewModel
        )
    }

}


