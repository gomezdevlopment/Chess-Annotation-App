package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.puzzles_component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.GameEvent
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.view.cardWhite
import com.gomezdevlopment.chessnotationapp.view.game_screen.board.ChessBoard
import com.gomezdevlopment.chessnotationapp.view.game_screen.board.ChessUILogic
import com.gomezdevlopment.chessnotationapp.view.game_screen.board.Coordinates
import com.gomezdevlopment.chessnotationapp.view.game_screen.board.Pieces
import com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements.EndOfGameCard
import com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements.Promotion
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.Highlight
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.Outline
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.PossibleCapture
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.PossibleMove
import com.gomezdevlopment.chessnotationapp.view.teal
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel
import com.gomezdevlopment.chessnotationapp.view_model.PuzzleViewModel

@Composable
fun PuzzleScreen(viewModel: PuzzleViewModel) {
    val chessBoardVector: ImageVector =
        ImageVector.vectorResource(id = R.drawable.ic_chess_board_teal)
    Column(
        Modifier
            .fillMaxSize()
            .background(cardWhite),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
        ) {
            ChessBoard(chessBoardVector = chessBoardVector)
            Pieces(
                height = maxWidth / 8,
                pieces = viewModel.piecesOnBoard,
                playerTurn = viewModel.getPlayerTurn(),
                userColor = viewModel.userColor,
                selectedPiece = viewModel.selectedPiece,
                pieceClicked = viewModel.pieceClicked,
                currentSquare = viewModel.getCurrentSquare().value,
                previousSquare = viewModel.getPreviousSquare().value,
            )
            Coordinates(size = maxWidth / 8)
            PuzzleUILogic(height = maxWidth / 8, viewModel = viewModel)
            //ChessUILogic(height = maxWidth / 8, viewModel = viewModel, navController)
        }
    }
}

@Composable
private fun PuzzleUILogic(height: Dp, viewModel: PuzzleViewModel) {
    val hashMap = viewModel.occupiedSquares
    val showMoves by remember { viewModel.pieceClicked }
    val isMoveSelected = remember { mutableStateOf(false) }
    val promotionSelectionShowing = remember { mutableStateOf(false) }
    val clickedPiece = remember { viewModel.selectedPiece }
    val targetRank = remember { mutableStateOf(0) }
    val targetFile = remember { mutableStateOf(0) }

    if (showMoves) {
        println("show moves")
        val legalMoves: List<Square> = clickedPiece.value.legalMoves
        println(legalMoves)
        for (move in legalMoves) {
            if (hashMap.containsKey(move)) {
                PossibleCapture(height, move, targetRank, targetFile, isMoveSelected)
            } else {
                PossibleMove(height, move, targetRank, targetFile, isMoveSelected)
            }
        }
    }

    if (isMoveSelected.value) {
        if (clickedPiece.value.piece == "pawn" && (targetRank.value == 7 || targetRank.value == 0)) {
            promotionSelectionShowing.value = true
        } else {
            viewModel.changePiecePosition(
                Square(targetRank.value, targetFile.value),
                clickedPiece.value,
                null
            )
        }
        viewModel.pieceClicked.value = false
        isMoveSelected.value = false
    }

//    if (promotionSelectionShowing.value) {
//        Promotion(
//            height,
//            clickedPiece.value,
//            promotionSelectionShowing,
//            targetRank.value,
//            targetFile.value,
//            viewModel
//        )
//    }
}