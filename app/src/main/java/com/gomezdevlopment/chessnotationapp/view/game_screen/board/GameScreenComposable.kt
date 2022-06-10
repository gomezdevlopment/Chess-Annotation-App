package com.gomezdevlopment.chessnotationapp.view.game_screen.board

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.GameEvent
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements.AnnotationBar
import com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements.EndOfGameCard
import com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements.GameBar
import com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements.Promotion
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.PossibleCapture
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.PossibleMove
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel

@Composable
fun ChessCanvas(width: Float, viewModel: GameViewModel) {
    val chessBoardVector: ImageVector =
        ImageVector.vectorResource(id = R.drawable.ic_chess_board_teal)
    val rowWidthAndHeight: Float = (width / 8f)

    Row(verticalAlignment = Alignment.Top) {
        AnnotationBar(viewModel)
    }
    
    Column(verticalArrangement = Arrangement.Center) {
        Row(verticalAlignment = CenterVertically) {
            Box(
                modifier = Modifier
                    .width(width.dp)
                    .aspectRatio(1f),
            ) {
                ChessBoard(chessBoardVector = chessBoardVector)
                Pieces(viewModel = viewModel, height = rowWidthAndHeight)
                Coordinates(size = rowWidthAndHeight)
                ChessUILogic(height = rowWidthAndHeight, viewModel = viewModel)
            }
        }
    }

    Row(verticalAlignment = Alignment.Bottom) {
        GameBar(viewModel = viewModel)
    }

}

@Composable
fun ChessBoard(chessBoardVector: ImageVector){
    Image(
        imageVector = chessBoardVector,
        contentDescription = "Chess Board",
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .zIndex(1f)
    )
}

@Composable
fun ChessUILogic(height: Float, viewModel: GameViewModel) {
    val cardVisible = remember { mutableStateOf(false) }
    val hashMap = viewModel.getHashMap()
    val showMoves by remember { viewModel.isPieceClicked() }
    val isMoveSelected = remember { mutableStateOf(false) }
    val promotionSelectionShowing = remember { mutableStateOf(false) }
    val clickedPiece = remember { viewModel.getSelectedPiece() }
    val targetRank = remember { mutableStateOf(0) }
    val targetFile = remember { mutableStateOf(0) }
    val checkmate = remember { viewModel.getCheckmate() }
    val stalemate = remember { viewModel.getStalemate() }
    val insufficientMaterial = remember { viewModel.getInsufficientMaterial() }
    val threeFoldRepetition = remember { viewModel.getThreeFoldRepetition() }
    val fiftyMoveRule = remember { viewModel.getFiftyMoveRule() }

//    val xRays = remember {
//        viewModel.xRays()
//    }
//
//    val attackedSquares = remember {
//        viewModel.getSquaresToBlock()
//    }
//
//    val attacks = remember {
//        viewModel.getAttacks()
//    }

//    for(attack in attacks){
//        Highlight(height = height, square = attack, color = Color.Red, .5f)
//    }
//
//    for(attack in xRays){
//        Outline(height = height, square = attack, color = Color.Blue)
//    }

    if (showMoves) {
        val legalMoves = viewModel.onEvent(GameEvent.OnPieceClicked, clickedPiece.value)
        if (legalMoves != null) {
            for (move in legalMoves) {
                if (hashMap.containsKey(move)) {
                    PossibleCapture(height, move, targetRank, targetFile, isMoveSelected)
                } else {
                    PossibleMove(height, move, targetRank, targetFile, isMoveSelected)
                }
            }
        }
    }

    if (isMoveSelected.value) {
        if (clickedPiece.value.piece == "pawn" && (targetRank.value == 7 || targetRank.value == 0)) {
            viewModel.movePiece(
                Square(targetRank.value, targetFile.value),
                clickedPiece.value
            )
            promotionSelectionShowing.value = true
        } else {
            viewModel.changePiecePosition(
                Square(targetRank.value, targetFile.value),
                clickedPiece.value
            )
        }
        viewModel.isPieceClicked().value = false
        isMoveSelected.value = false
    }

    if (promotionSelectionShowing.value) {
        Promotion(
            height,
            clickedPiece.value,
            promotionSelectionShowing,
            targetRank.value,
            targetFile.value,
            viewModel
        )
    }

    if (checkmate.value) {
        var winner = "White"
        if (viewModel.getPlayerTurn() == "white") {
            winner = "Black"
        }
        cardVisible.value = true
        EndOfGameCard("Checkmate", message = "$winner Wins!", cardVisible)
    }

    if (insufficientMaterial.value) {
        cardVisible.value = true
        EndOfGameCard("Draw", message = "by Insufficient Material", cardVisible)
    }

    if (stalemate.value) {
        cardVisible.value = true
        EndOfGameCard("Draw", message = "by Stalemate", cardVisible)
    }

    if (threeFoldRepetition.value) {
        cardVisible.value = true
        EndOfGameCard("Draw", message = "by Threefold Repetition", cardVisible)
    }

    if (fiftyMoveRule.value) {
        cardVisible.value = true
        EndOfGameCard("Draw", message = "by Fifty Move Rule", cardVisible)
    }
}
