package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.puzzles_component

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.pieces.PromotionPieces
import com.gomezdevlopment.chessnotationapp.model.utils.Utils
import com.gomezdevlopment.chessnotationapp.view.*
import com.gomezdevlopment.chessnotationapp.view.game_screen.board.*
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.Highlight
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.Outline
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.PossibleCapture
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.PossibleMove
import com.gomezdevlopment.chessnotationapp.view.theming.*
import com.gomezdevlopment.chessnotationapp.view_model.PuzzleViewModel

@Composable
fun PuzzleScreen(viewModel: PuzzleViewModel, navController: NavController) {

    if (viewModel.showNoMorePuzzlesCard.value) {
        AllPuzzlesCompletedCard(
            header = "Congratulations!",
            message = "You completed all Puzzles!"
        ) {
            navController.popBackStack()
        }
    }

    val config = LocalConfiguration.current
    when (config.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
            ) {
                PuzzleUIElements(
                    viewModel = viewModel,
                    navController = navController,
                    weight = Modifier.weight(1f),
                    config = config,
                    backButtonModifier = Modifier.fillMaxWidth()
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
            ) {
                PuzzleUIElements(
                    viewModel = viewModel,
                    navController = navController,
                    weight = Modifier.weight(1f),
                    config = config,
                    backButtonModifier = Modifier
                )
            }
        }
    }
}

@Composable
fun PuzzleUIElements(
    viewModel: PuzzleViewModel,
    navController: NavController,
    weight: Modifier,
    config: Configuration,
    backButtonModifier: Modifier
) {
    Row(
        backButtonModifier
            .padding(10.dp, 5.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Go Back",
            tint = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .size(30.dp)
                .clickable {
                    navController.popBackStack()
                })
    }
    BoxWithConstraints(
        weight
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                viewModel.puzzleRating.value,
                fontSize = 20.sp,
                modifier = Modifier.padding(20.dp),
                color = MaterialTheme.colors.onBackground
            )
        }
    }
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
            userColor = viewModel.userColor.value,
            selectedPiece = viewModel.selectedPiece,
            pieceClicked = viewModel.pieceClicked,
            kingInCheck = viewModel.kingInCheck.value,
            currentSquare = viewModel.getCurrentSquare().value,
            previousSquare = viewModel.getPreviousSquare().value,
            kingSquare = viewModel.kingSquare,
            theme = viewModel.pieceTheme,
            pieceAnimationSpeed = 300,
            highlightStyle = viewModel.highlightStyle,
            hint = viewModel.hint.value,
            correctPiece = viewModel.correctPiece.value
        )
        Coordinates(size = maxWidth / 8)
        PuzzleUILogic(height = maxWidth / 8, viewModel = viewModel)
    }
    BoxWithConstraints(
        weight
    ) {
        if (!viewModel.endOfPuzzle.value) {
            HintButton(viewModel = viewModel)
        }
        Column(Modifier.height(maxHeight), verticalArrangement = Arrangement.Bottom) {
            if (viewModel.endOfPuzzle.value) {
                val landscape = config.orientation == Configuration.ORIENTATION_LANDSCAPE
                if (landscape) {
                    EndOfPuzzleLandscape(viewModel = viewModel)
                } else {
                    EndOfPuzzleCard(viewModel)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun EndOfPuzzleCard(viewModel: PuzzleViewModel) {
    val message by viewModel.message
    val image by viewModel.image
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Card(
            backgroundColor = MaterialTheme.colors.surface,
            elevation = 10.dp,
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.Start) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            imageVector = ImageVector.vectorResource(id = image),
                            contentDescription = message,
                            modifier = Modifier.padding(10.dp)
                        )
                        Text(message, modifier = Modifier.padding(10.dp))
                    }
                }
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                    Row() {
                        EndOfPuzzleCardButtons(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun EndOfPuzzleLandscape(viewModel: PuzzleViewModel) {
    val message by viewModel.message
    val image by viewModel.image
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.End) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                imageVector = ImageVector.vectorResource(id = image),
                contentDescription = message,
                modifier = Modifier.padding(10.dp)
            )
            Text(message, modifier = Modifier.padding(10.dp))
        }
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .weight(1f)
                .padding(10.dp)
        ) {
            EndOfPuzzleCardButtons(viewModel = viewModel)
        }

    }
}

@Composable
fun EndOfPuzzleCardButtons(viewModel: PuzzleViewModel) {
    if (viewModel.endOfPuzzle.value) {
        if (!viewModel.correct.value) {
            TextButton(onClick = {
                viewModel.setPuzzle()
            }) {
                Text(
                    text = "Try Again",
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
    TextButton(onClick = {
        viewModel.puzzleIndex += 1
        viewModel.setPuzzle()
    }) {
        Text(
            text = "Next",
            fontSize = 14.sp,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(10.dp)
        )
    }
}

@Composable
fun HintButton(viewModel: PuzzleViewModel) {
    TextButton(
        onClick = { viewModel.hint.value = true },
        Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_hint),
                contentDescription = "Hint",
                tint = MaterialTheme.colors.onBackground
            )
            Text(
                text = "Hint",
                fontSize = 20.sp,
                modifier = Modifier.padding(10.dp),
                color = MaterialTheme.colors.onBackground
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
    val pieceTheme = viewModel.pieceTheme
    val checkers = pieceTheme == checkers

    if (showMoves) {
        val legalMoves: List<Square> = clickedPiece.value.legalMoves
        for (move in legalMoves) {
            if (hashMap.containsKey(move)) {
                PossibleCapture(height, move, targetRank, targetFile, isMoveSelected, checkers)
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

    if (viewModel.hint.value) {
        val correctPiece = viewModel.correctPiece.value
        if (correctPiece != null) {
            if (viewModel.highlightStyle == "Outline") {
                Outline(height, correctPiece.square, pink)
            } else {
                Highlight(height = height, square = correctPiece.square, color = pink, .8f)
            }

        }

    }
    SoundFX(viewModel = viewModel)
    if (promotionSelectionShowing.value) {
        PuzzlePromotion(
            height,
            clickedPiece.value,
            promotionSelectionShowing,
            targetRank.value,
            targetFile.value,
            viewModel
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

@Composable
fun PuzzlePromotion(
    width: Dp,
    chessPiece: ChessPiece,
    promotionSelectionShowing: MutableState<Boolean>,
    rank: Int,
    file: Int,
    viewModel: PuzzleViewModel
) {
    var pieces = PromotionPieces().whitePieces
    if (chessPiece.color == "black") {
        pieces = PromotionPieces().blackPieces
    }

    val list = listOf("queen", "rook", "bishop", "knight")

    val offsetX = Utils().offsetX(width.value, file).dp
    var offsetY = Utils().offsetY(width.value, rank).dp

    if (rank == 0 && MainActivity.userColor == "white") {
        offsetY = width * 4
    }

    if (rank == 7 && MainActivity.userColor == "black") {
        offsetY = width * 4
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(id = R.color.transparentBlack))
            .zIndex(4f)
    )
    {
        Card(
            modifier = Modifier
                .width(width)
                .height((width * 4))
                .absoluteOffset(offsetX, offsetY),
            elevation = 5.dp,
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(Modifier.fillMaxWidth()) {
                list.forEach() {
                    var drawable = pieces[it]?.pieceDrawable
                    if (drawable == null) {
                        drawable = com.google.android.material.R.drawable.mtrl_ic_error
                    }
                    Image(
                        imageVector = ImageVector.vectorResource(drawable),
                        contentDescription = "Chess Piece",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(5.dp)
                            .clickable {
                                promotionSelectionShowing.value = false
                                viewModel.changePiecePosition(
                                    Square(rank, file),
                                    chessPiece,
                                    pieces[it]
                                )
                            }
                    )
                }
            }
        }

    }
}

@Composable
fun AllPuzzlesCompletedCard(
    header: String,
    message: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(id = R.color.transparentBlack))
            .zIndex(4f)
            .padding(50.dp)
    )
    {
        Card(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .padding(10.dp),
            elevation = 10.dp,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Column(
                    Modifier
                        .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(50.dp))
                    Text(
                        text = header,
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = message, textAlign = TextAlign.Center, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(50.dp))
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextButton(onClick = { onClick() }) {
                        Text("Close", color = MaterialTheme.colors.primary)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

