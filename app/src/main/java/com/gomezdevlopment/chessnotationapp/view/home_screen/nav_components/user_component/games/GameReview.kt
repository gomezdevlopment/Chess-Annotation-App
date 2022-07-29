package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component.games

import androidx.compose.foundation.background
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
//    rememberSystemUiController().setStatusBarColor(MaterialTheme.colors.background)

    Column(Modifier.fillMaxHeight().background(MaterialTheme.colors.background),) {
        Row(verticalAlignment = Alignment.Top) {
            AnnotationBar(viewModel)
        }
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                when (MainActivity.userColor) {
                    "white" -> {
                        Column() {
                            BlackCaptures(viewModel, Arrangement.End)
                        }
                    }
                    "black" -> {
                        Column() {
                            WhiteCaptures(viewModel, Arrangement.End)
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
                    ChessBoard(chessBoard= viewModel.chessBoardTheme, modifier = Modifier.chessBoardFullScreen())
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
                        pieceAnimationSpeed = viewModel.pieceAnimationSpeed,
                        highlightStyle = viewModel.highlightStyle,
                        hint = false,
                        correctPiece = null
                    )
                    Coordinates(size = maxWidth / 8)
                }
            }
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                when (MainActivity.userColor) {
                    "white" -> {
                        Column() {
                            WhiteCaptures(viewModel, Arrangement.Start)
                        }
                    }
                    "black" -> {
                        Column() {
                            BlackCaptures(viewModel, Arrangement.Start)
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