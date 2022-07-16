package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component.games

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import com.gomezdevlopment.chessnotationapp.view.game_screen.board.*
import com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements.*
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun GameReview(viewModel: GameViewModel, navController: NavController){
    rememberSystemUiController().setStatusBarColor(MaterialTheme.colors.background)
    val chessBoardVector: ImageVector =
        ImageVector.vectorResource(id = viewModel.chessBoardTheme)

    Column(Modifier.fillMaxHeight()) {
        Row(verticalAlignment = Alignment.Top) {
            AnnotationBar(viewModel)
        }
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                when (MainActivity.userColor) {
                    "white" -> {
                        Column() {
                            BlackCaptures(viewModel)
                        }
                    }
                    "black" -> {
                        Column() {
                            WhiteCaptures(viewModel)
                        }
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                ) {
                    ChessBoard(chessBoardVector = chessBoardVector, modifier = Modifier.chessBoardFullScreen())
                    Pieces(
                        height = maxWidth / 8,
                        pieces = viewModel.piecesOnBoard,
                        playerTurn = viewModel.getPlayerTurn(),
                        userColor = MainActivity.userColor,
                        selectedPiece= viewModel.getSelectedPiece(),
                        pieceClicked = viewModel.isPieceClicked(),
                        kingInCheck = viewModel.kingInCheck(),
                        currentSquare = viewModel.getCurrentSquare().value,
                        previousSquare = viewModel.getPreviousSquare().value,
                        kingSquare = viewModel.kingSquare,
                        theme = viewModel.pieceTheme,
                        pieceAnimationSpeed = viewModel.pieceAnimationSpeed
                    )
                    Coordinates(size = maxWidth / 8)
                    //ChessUILogic(height = maxWidth / 8, viewModel = viewModel, navController)
                }
            }
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                when (MainActivity.userColor) {
                    "white" -> {
                        Column() {
                            WhiteCaptures(viewModel)
                        }
                    }
                    "black" -> {
                        Column() {
                            BlackCaptures(viewModel)
                        }
                    }
                }
            }
        }
        Row(verticalAlignment = Alignment.Bottom) {
            ReviewGameBar(viewModel = viewModel, navController)
        }
    }
}