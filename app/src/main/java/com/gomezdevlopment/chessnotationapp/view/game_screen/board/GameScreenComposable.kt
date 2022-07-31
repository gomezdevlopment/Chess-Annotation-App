package com.gomezdevlopment.chessnotationapp.view.game_screen.board

import android.content.res.Configuration
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.gomezdevlopment.chessnotationapp.model.GameEvent
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.user
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.userColor
import com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements.*
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.*
import com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.puzzles_component.PuzzleUIElements
import com.gomezdevlopment.chessnotationapp.view.theming.purplePearlBoard
import com.gomezdevlopment.chessnotationapp.view.theming.tealDarker
import com.gomezdevlopment.chessnotationapp.view.theming.textWhite
import com.gomezdevlopment.chessnotationapp.view.theming.woodBoard
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun GameScreen(viewModel: GameViewModel, navController: NavController) {
    rememberSystemUiController().setStatusBarColor(MaterialTheme.colors.background)
    ResignAlertDialog(viewModel = viewModel)
    DrawOfferAlertDialog(viewModel = viewModel)
    DrawOfferedAlertDialog(viewModel = viewModel)
    val config = LocalConfiguration.current
    Column(Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.Top) {
            AnnotationBar(viewModel)
        }
        when (config.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background)
                        .weight(1f)
                ) {
                    GameUIElements(
                        viewModel = viewModel,
                        navController = navController,
                        weight = Modifier.weight(1f),
                        landscape = false
                    )
                }
            }
            else -> {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background)
                        .weight(1f)
                ) {
                    GameUIElements(
                        viewModel = viewModel,
                        navController = navController,
                        weight = Modifier.weight(1f),
                        landscape = true
                    )
                }
            }
        }
        Row(verticalAlignment = Alignment.Bottom) {
            if (userColor == "both") {
                LocalGameBar(viewModel = viewModel, navController = navController)
            } else {
                GameBar(viewModel = viewModel)
            }
        }
    }

}

@Composable
fun GameUIElements(
    viewModel: GameViewModel,
    navController: NavController,
    weight: Modifier,
    landscape: Boolean
) {
    BoxWithConstraints(modifier = weight) {
        val size = if (landscape) maxWidth / 2 else maxWidth / 7
        when (userColor) {
            "white" -> {
                if (landscape) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(maxWidth),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        BlackClock(viewModel = viewModel, size = size)
                        BlackCaptures(viewModel, Arrangement.End)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        BlackClock(viewModel = viewModel, size = size)
                        BlackCaptures(viewModel, Arrangement.End)
                    }
                }

            }
            "black" -> {
                if (landscape) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(maxWidth),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        WhiteClock(
                            viewModel = viewModel,
                            size = size
                        )
                        WhiteCaptures(viewModel, Arrangement.End)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        WhiteClock(
                            viewModel = viewModel,
                            size = size
                        )
                        WhiteCaptures(viewModel, Arrangement.End)
                    }
                }
            }
        }

    }
    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = CenterVertically) {
        val config = LocalConfiguration.current
        val modifier = if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
            Modifier.fillMaxHeight()
        else Modifier.fillMaxWidth()

        BoxWithConstraints(
            modifier = modifier
                .aspectRatio(1f),
        ) {
            ChessBoard(
                chessBoard = viewModel.chessBoardTheme,
                modifier = Modifier.chessBoardFullScreen()
            )
            Pieces(
                height = maxWidth / 8,
                pieces = viewModel.piecesOnBoard,
                playerTurn = viewModel.getPlayerTurn(),
                userColor = userColor,
                selectedPiece = viewModel.getSelectedPiece(),
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
            ChessUILogic(height = maxWidth / 8, viewModel = viewModel, navController)
        }
    }

    BoxWithConstraints(modifier = weight) {
        val size = if (landscape) maxWidth / 2 else maxWidth / 7
        when (userColor) {
            "white" -> {
                if (landscape) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(maxWidth),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.End
                    ) {
                        WhiteCaptures(viewModel, Arrangement.Start)
                        WhiteClock(viewModel = viewModel, size = size)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        WhiteCaptures(viewModel, Arrangement.Start)
                        WhiteClock(viewModel = viewModel, size = size)
                    }
                }

            }
            "black" -> {
                if (landscape) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(maxWidth),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.End
                    ) {
                        BlackCaptures(viewModel, Arrangement.Start)
                        BlackClock(viewModel = viewModel, size = size)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        BlackCaptures(viewModel, Arrangement.Start)
                        BlackClock(viewModel = viewModel, size = size)
                    }
                }

            }
        }
    }
}

@Composable
fun BlackClock(viewModel: GameViewModel, size: Dp) {
    Row(
        modifier = Modifier
            .padding(7.dp)
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
fun WhiteClock(viewModel: GameViewModel, size: Dp) {
    Row(
        modifier = Modifier
            .padding(7.dp)
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
fun ChessBoard(chessBoard: Int, modifier: Modifier) {
    if (chessBoard == woodBoard || chessBoard == purplePearlBoard) {
        Image(
            painterResource(id = chessBoard),
            contentDescription = "Chess Board",
            modifier = modifier
        )
    } else {
        Image(
            ImageVector.vectorResource(id = chessBoard),
            contentDescription = "Chess Board",
            modifier = modifier
        )
    }
}

fun Modifier.chessBoardFullScreen() =
    fillMaxSize()
        .aspectRatio(1f)
        .zIndex(1f)

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

    if (showMoves && viewModel.pieceIsClickable()) {
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
    GameDialog(
        title = "Resign?",
        positiveButtonText = "Confirm",
        negativeButtonText = "Cancel",
        showDialog = viewModel.openResignDialog,
        onAccept = {
            viewModel.resign()
            viewModel.openResignDialog.value = false
        },
        onDismiss = {
            viewModel.openResignDialog.value = false
        })
}

@Composable
fun DrawOfferAlertDialog(viewModel: GameViewModel) {
    GameDialog(
        title = "Offer Draw?",
        positiveButtonText = "Confirm",
        negativeButtonText = "Cancel",
        showDialog = viewModel.openDrawOfferDialog,
        onAccept = {
            viewModel.drawOffer(userColor)
            viewModel.openDrawOfferDialog.value = false
        },
        onDismiss = {
            viewModel.openDrawOfferDialog.value = false
        })
}

@Composable
fun DrawOfferedAlertDialog(viewModel: GameViewModel) {
    GameDialog(
        title = "Accept Draw Offer?",
        positiveButtonText = "Accept",
        negativeButtonText = "Decline",
        showDialog = viewModel.openDrawOfferedDialog,
        onAccept = {
            viewModel.drawOffer("accept")
            viewModel.openDrawOfferedDialog.value = false
        },
        onDismiss = {
            viewModel.drawOffer("decline")
            viewModel.openDrawOfferedDialog.value = false
        })
}

@Composable
fun GameDialog(
    title: String,
    positiveButtonText: String,
    negativeButtonText: String,
    showDialog: MutableState<Boolean>,
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Text(
                    text = title,
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAccept()
                    }
                ) {
                    Text(positiveButtonText, color = MaterialTheme.colors.primary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text(negativeButtonText, color = MaterialTheme.colors.primary)
                }
            },
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.background
        )
    }
}
