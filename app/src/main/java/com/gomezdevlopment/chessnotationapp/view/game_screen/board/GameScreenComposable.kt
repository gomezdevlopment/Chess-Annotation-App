package com.gomezdevlopment.chessnotationapp.view.game_screen.board

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.GameEvent
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.view.*
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.userColor
import com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements.*
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.*
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel

@Composable
fun GameScreen(viewModel: GameViewModel, navController: NavController) {
    val chessBoardVector: ImageVector =
        ImageVector.vectorResource(id = R.drawable.ic_chess_board_teal)

    ResignAlertDialog(viewModel = viewModel)
    DrawOfferAlertDialog(viewModel = viewModel)
    DrawOfferedAlertDialog(viewModel = viewModel)
    Column(Modifier.fillMaxHeight()) {
        Row(verticalAlignment = Alignment.Top) {
            AnnotationBar(viewModel)
        }
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val size = maxWidth / 7
                when (userColor) {
                    "white" -> {
                        Column() {
                            BlackClock(viewModel = viewModel, size = size, Arrangement.Start)
                        }
                        Column() {
                            BlackCaptures(viewModel)
                        }
                    }
                    "black" -> {
                        Column() {
                            WhiteClock(viewModel = viewModel, size = size, Arrangement.Start)
                        }
                        Column() {
                            WhiteCaptures(viewModel)
                        }
                    }
                }

            }
            Row(verticalAlignment = CenterVertically) {
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
                        userColor = userColor,
                        selectedPiece= viewModel.getSelectedPiece(),
                        pieceClicked = viewModel.isPieceClicked(),
                        kingInCheck = viewModel.kingInCheck(),
                        currentSquare = viewModel.getCurrentSquare().value,
                        previousSquare = viewModel.getPreviousSquare().value,
                        kingSquare = viewModel.kingSquare,
                    )
                    Coordinates(size = maxWidth / 8)
                    ChessUILogic(height = maxWidth / 8, viewModel = viewModel, navController)
                }
            }
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val size = maxWidth / 7
                when (userColor) {
                    "white" -> {
                        Column() {
                            WhiteClock(viewModel = viewModel, size = size, Arrangement.End)
                        }
                        Column() {
                            WhiteCaptures(viewModel)
                        }
                    }
                    "black" -> {
                        Column() {
                            BlackClock(viewModel = viewModel, size = size, Arrangement.End)
                        }
                        Column() {
                            BlackCaptures(viewModel)
                        }
                    }
                }
            }
        }

        Row(verticalAlignment = Alignment.Bottom) {
            GameBar(viewModel = viewModel)
        }
    }
}

@Composable
fun BlackClock(viewModel: GameViewModel, size: Dp, arrangement: Arrangement.Horizontal) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(7.dp),
        horizontalArrangement = arrangement
    ) {
        CountDownView(
            size,
            viewModel.blackTimer.value,
            viewModel.blackTime,
            viewModel.blackProgress
        )
    }
}

@Composable
fun WhiteClock(viewModel: GameViewModel, size: Dp, arrangement: Arrangement.Horizontal) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(7.dp),
        horizontalArrangement = arrangement
    ) {
        CountDownView(
            size,
            viewModel.whiteTimer.value,
            viewModel.whiteTime,
            viewModel.whiteProgress
        )
    }
}


@Composable
fun ChessBoard(chessBoardVector: ImageVector) {
    Image(
        painter = painterResource(id = R.drawable.wood_board_2),
        //imageVector = chessBoardVector,
        contentDescription = "Chess Board",
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .zIndex(1f)
    )
}

@Composable
fun ChessUILogic(height: Dp, viewModel: GameViewModel, navController: NavController) {
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
////
//    val attacks = remember {
//        viewModel.getAttacks()
//    }
////
//    for(attack in attacks){
//        Highlight(height = height, square = attack, color = Color.Red, .5f)
//    }
////
//    for(attack in xRays){
//        Outline(height = height, square = attack, color = Color.Blue)
//    }

    if (showMoves) {
        val legalMoves = viewModel.onEvent(GameEvent.OnPieceClicked, clickedPiece.value)
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
            EndOfGameCard(header, message = message, cardVisible, viewModel, navController)
        }
    }
}

@Composable
fun ResignAlertDialog(viewModel: GameViewModel) {
    if (viewModel.openResignDialog.value) {
        AlertDialog(
            onDismissRequest = { viewModel.openResignDialog.value = false },
            title = { Text(text = "Resign?", color = tealDarker, fontWeight = FontWeight.Bold) },
            //text = { Text("Hello! This is our Alert Dialog..", color = textWhite) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resign()
                        viewModel.openResignDialog.value = false
                    }
                ) {
                    Text("Confirm", color = tealDarker)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.openResignDialog.value = false
                    }
                ) {
                    Text("Cancel", color = tealDarker)
                }
            },
            backgroundColor = textWhite,
            contentColor = textWhite
        )
    }
}

@Composable
fun DrawOfferAlertDialog(viewModel: GameViewModel) {
    if (viewModel.openDrawOfferDialog.value) {
        AlertDialog(
            onDismissRequest = { viewModel.openDrawOfferDialog.value = false },
            title = {
                Text(
                    text = "Offer Draw?",
                    color = tealDarker,
                    fontWeight = FontWeight.Bold
                )
            },
            //text = { Text("Hello! This is our Alert Dialog..", color = textWhite) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.drawOffer(userColor)
                        viewModel.openDrawOfferDialog.value = false
                    }
                ) {
                    Text("Confirm", color = tealDarker)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.openDrawOfferDialog.value = false
                    }
                ) {
                    Text("Cancel", color = tealDarker)
                }
            },
            backgroundColor = textWhite,
            contentColor = textWhite
        )
    }
}

@Composable
fun DrawOfferedAlertDialog(viewModel: GameViewModel) {
    if (viewModel.openDrawOfferedDialog.value) {
        AlertDialog(
            onDismissRequest = { viewModel.openDrawOfferedDialog.value = false },
            title = {
                Text(
                    text = "Accept Draw Offer?",
                    color = tealDarker,
                    fontWeight = FontWeight.Bold
                )
            },
            //text = { Text("Hello! This is our Alert Dialog..", color = textWhite) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.drawOffer("accept")
                        viewModel.openDrawOfferedDialog.value = false
                    }
                ) {
                    Text("Accept", color = tealDarker)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.drawOffer("decline")
                        viewModel.openDrawOfferedDialog.value = false
                    }
                ) {
                    Text("Decline", color = tealDarker)
                }
            },
            backgroundColor = textWhite,
            contentColor = textWhite
        )
    }
}
