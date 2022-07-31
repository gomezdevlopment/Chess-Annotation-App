package com.gomezdevlopment.chessnotationapp.view.home_screen

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gomezdevlopment.chessnotationapp.view.game_screen.board.GameScreen
import com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.puzzles_component.PuzzleScreen
import com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component.games.GameReview
import com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component.settings.BoardThemes
import com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component.settings.PieceThemes
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel
import com.gomezdevlopment.chessnotationapp.view_model.MatchmakingViewModel
import com.gomezdevlopment.chessnotationapp.view_model.PuzzleViewModel
import com.gomezdevlopment.chessnotationapp.view_model.UserViewModel

@Composable
fun Navigation(
    gameViewModel: GameViewModel,
    matchmakingViewModel: MatchmakingViewModel,
    puzzleViewModel: PuzzleViewModel,
    userViewModel: UserViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Home(
                navController,
                matchmakingViewModel,
                gameViewModel,
                puzzleViewModel,
                userViewModel
            )
        }
        composable("game") { GameScreen(gameViewModel, navController) }
        composable("gameReview") { GameReview(gameViewModel, navController) }
        composable("matchSearch") {
            MatchSearch(
                gameViewModel,
                matchmakingViewModel,
                navController
            )
        }
        composable("puzzles") { PuzzleScreen(puzzleViewModel, navController) }
        composable("boardThemes") { BoardThemes(navController, userViewModel) }
        composable("pieceThemes") { PieceThemes(navController, userViewModel) }
    }
}