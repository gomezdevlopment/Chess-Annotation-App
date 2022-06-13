package com.gomezdevlopment.chessnotationapp.view.game_screen.board

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.GameEvent
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements.*
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.PossibleCapture
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.PossibleMove
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel

@Composable
fun GameScreen(viewModel: GameViewModel, navController: NavController) {
    val chessBoardVector: ImageVector =
        ImageVector.vectorResource(id = R.drawable.ic_chess_board_teal)

    println("Recomposing")
    Column(Modifier.fillMaxHeight()) {
        Row(verticalAlignment = Alignment.Top) {
            AnnotationBar(viewModel)
        }
        Column(verticalArrangement = Arrangement.Center) {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                BlackClock(viewModel = viewModel, size = maxWidth/6)
            }

            BlackCaptures(viewModel)
            Row(verticalAlignment = CenterVertically) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                ) {
                    ChessBoard(chessBoardVector = chessBoardVector)
                    Pieces(viewModel = viewModel, height = maxWidth/8)
                    Coordinates(size = maxWidth/8)
                    ChessUILogic(height = maxWidth/8, viewModel = viewModel)
                }
            }
            WhiteCaptures(viewModel)
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                WhiteClock(viewModel = viewModel, size = maxWidth/6)
            }
        }
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.weight(1f)) {
            GameBar(viewModel = viewModel, navController = navController)
        }
    }
}

@Composable
fun BlackClock(viewModel: GameViewModel, size: Dp) {
    Row(modifier = Modifier.padding(10.dp)) {
        CountDownView(
            viewModel,
            size,
            viewModel.blackTimer.value,
            viewModel.blackTime,
            viewModel.blackProgress,
            viewModel.blackTimeIsPlaying
        )
    }
}

@Composable
fun WhiteClock(viewModel: GameViewModel, size: Dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), horizontalArrangement = Arrangement.End
    ) {
        CountDownView(
            viewModel,
            size,
            viewModel.whiteTimer.value,
            viewModel.whiteTime,
            viewModel.whiteProgress,
            viewModel.whiteTimeIsPlaying
        )
    }
}


@Composable
fun ChessBoard(chessBoardVector: ImageVector) {
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
fun ChessUILogic(height: Dp, viewModel: GameViewModel) {
    val cardVisible = remember { viewModel.cardVisible }
    val hashMap = viewModel.getHashMap()
    val showMoves by remember { viewModel.isPieceClicked() }
    val isMoveSelected = remember { mutableStateOf(false) }
    val promotionSelectionShowing = remember { mutableStateOf(false) }
    val clickedPiece = remember { viewModel.getSelectedPiece() }
    val targetRank = remember { mutableStateOf(0) }
    val targetFile = remember { mutableStateOf(0) }
    val endOfGame by remember { viewModel.endOfGame }

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

    if (endOfGame) {
        if (cardVisible.value) {
            val header by viewModel.endOfGameResult
            val message by viewModel.endOfGameMessage
            EndOfGameCard(header, message = message, cardVisible, viewModel)
        }
    }
}
