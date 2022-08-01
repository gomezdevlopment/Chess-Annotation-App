package com.gomezdevlopment.chessnotationapp.view.home_screen

import androidx.compose.foundation.background
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
import com.gomezdevlopment.chessnotationapp.view.BackPressHandler
import com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.play_component.PlayScreen
import com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.puzzles_component.PuzzleDifficultySelectionScreen
import com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component.UserScreen
import com.gomezdevlopment.chessnotationapp.view_model.*

@Composable
fun MatchSearch(
    gameViewModel: GameViewModel,
    matchmakingViewModel: MatchmakingViewModel,
    navController: NavController
) {
    BackPressHandler() {
        matchmakingViewModel.cancelSearch()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background),
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
                    color = MaterialTheme.colors.onBackground
                )
                Spacer(modifier = Modifier.height(20.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(100.dp)
                        .aspectRatio(1f),
                    strokeWidth = 5.dp,
                    color = MaterialTheme.colors.primary
                )
            }

        }
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(0.dp, 20.dp)) {
            TextButton(onClick = { matchmakingViewModel.cancelSearch() }) {
                Text(
                    text = "Cancel",
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colors.primary,
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
fun HomeBottomNavigation(
    bottomNavBarController: NavHostController,
    navController: NavController,
    matchmakingViewModel: MatchmakingViewModel,
    gameViewModel: GameViewModel,
    puzzleViewModel: PuzzleViewModel,
    userViewModel: UserViewModel
) {
    NavHost(navController = bottomNavBarController, startDestination = "play") {
        composable("play") {
            PlayScreen(navController, matchmakingViewModel, gameViewModel)
        }
        composable("puzzleDifficultySelection") {
            PuzzleDifficultySelectionScreen(puzzleViewModel, navController)
        }
        composable("user") {
            UserScreen(userViewModel, navController, gameViewModel)
        }
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
            bottomNavBarController = bottomNavBarController,
            navController = navController,
            matchmakingViewModel = matchmakingViewModel,
            gameViewModel = gameViewModel,
            puzzleViewModel = puzzleViewModel,
            userViewModel = userViewModel
        )
    }

}


