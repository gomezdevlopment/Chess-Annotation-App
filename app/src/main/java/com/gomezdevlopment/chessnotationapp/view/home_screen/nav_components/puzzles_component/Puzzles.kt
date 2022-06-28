package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.puzzles_component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.GameEvent
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.utils.Utils
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
import com.gomezdevlopment.chessnotationapp.view.tealDarker
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
        val ratingString = "Your Rating: ${viewModel.playerRating.value}"
        Text(
            ratingString,
            fontSize = 14.sp,
            modifier = Modifier.padding(20.dp),
            color = Color.Black
        )
        Text(
            viewModel.puzzleRating.value,
            fontSize = 20.sp,
            modifier = Modifier.padding(20.dp),
            color = tealDarker
        )
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
                userColor = viewModel.userColor.value,
                selectedPiece = viewModel.selectedPiece,
                pieceClicked = viewModel.pieceClicked,
                currentSquare = viewModel.getCurrentSquare().value,
                previousSquare = viewModel.getPreviousSquare().value,
            )
            Coordinates(size = maxWidth / 8)
            PuzzleUILogic(height = maxWidth / 8, viewModel = viewModel)

            if(viewModel.endOfPuzzle.value){
                if(viewModel.correct.value){
                    OutlineBoard(height = maxWidth, color = Color.Green)
                }else{
                    OutlineBoard(height = maxWidth, color = Color.Red)
                }

            }
        }
        TextButton(
            onClick = {
                viewModel.puzzleIndex += 1
                viewModel.setPuzzle()
            },
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Next Puzzle",
                fontSize = 20.sp,
                color = tealDarker,
                modifier = Modifier.padding(20.dp)
            )
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
        val legalMoves: List<Square> = clickedPiece.value.legalMoves
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

    SoundFX(viewModel = viewModel)
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

@Composable
fun OutlineBoard(
    height: Dp,
    color: Color
) {
    Canvas(
        modifier = Modifier
            .height(height)
            .aspectRatio(1f)
            .absoluteOffset(0.dp, 0.dp)
            .padding(1.dp)
            .zIndex(2f)

    ) {
        drawRect(
            color = color,
            size = size,
            alpha = 1f,
            style = Stroke(size.width * .01f)
        )
    }
}

@Composable
private fun SoundFX(viewModel: PuzzleViewModel) {
    val pieceSound = remember { viewModel.getPieceSound() }
    val checkSound = remember { viewModel.getCheckSound() }
    val captureSound = remember { viewModel.getCaptureSound() }
    val castlingSound = remember { viewModel.getCastlingSound() }

    if (pieceSound.value) {
        LaunchedEffect(0) {
            pieceSound.value = false
            viewModel.playSoundPool("piece")
        }
    }
    if (checkSound.value) {
        LaunchedEffect(0) {
            checkSound.value = false
            viewModel.playSoundPool("check")
        }
    }
    if (captureSound.value) {
        LaunchedEffect(0) {
            captureSound.value = false
            viewModel.playSoundPool("capture")
        }
    }
    if (castlingSound.value) {
        LaunchedEffect(0) {
            castlingSound.value = false
            viewModel.playSoundPool("castle")
        }
    }
}