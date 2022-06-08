package com.gomezdevlopment.chessnotationapp.view.game_screen

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.GameEvent
import com.gomezdevlopment.chessnotationapp.model.Square
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel

@Composable
fun ChessCanvas(width: Float, viewModel: GameViewModel) {
    val chessBoardVector: ImageVector =
        ImageVector.vectorResource(id = R.drawable.ic_chess_board_teal)
    val rowWidthAndHeight: Float = (width / 8f)

    Column() {
        Row(Modifier.weight(1f)) {
            AnnotationBar(viewModel)
        }
        Row(modifier = Modifier.weight(2f)) {
            Box(
                modifier = Modifier
                    .width(width.dp)
                    .aspectRatio(1f),
            ) {
                Image(
                    imageVector = chessBoardVector,
                    contentDescription = "Chess Board",
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1f)
                )
                println("recomposing main")
                Pieces(viewModel = viewModel, height = rowWidthAndHeight)
                ChessSquaresV2(height = rowWidthAndHeight, viewModel = viewModel)
            }
        }

        Row(Modifier.wrapContentHeight()) {
            val onBackPressedDispatcher =
                LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            Column(
                Modifier
                    .weight(1f)
                    .clickable {
                        onBackPressedDispatcher?.onBackPressed()
                        viewModel.resetGame()
                    }) {

                val home: ImageVector =
                    ImageVector.vectorResource(id = R.drawable.ic_home)
                Icon(
                    imageVector = home,
                    contentDescription = "Go to Home Screen",
                    tint = tealDarker,
                    modifier = Modifier
                        .height(50.dp)
                        .padding(10.dp)
                        .aspectRatio(1f)
                        .align(CenterHorizontally)
                )
            }

            Column(
                Modifier
                    .weight(1f)
                    .clickable {
                        viewModel.previousNotation()
                        //viewModel.undoMove()
                    }
            ) {
                val leftArrow: ImageVector =
                    ImageVector.vectorResource(id = R.drawable.ic_round_arrow_left)
                Icon(
                    imageVector = leftArrow,
                    contentDescription = "Previous Move",
                    tint = tealDarker,
                    modifier = Modifier
                        .height(50.dp)
                        .padding(10.dp)
                        .aspectRatio(1f)
                        .align(CenterHorizontally)
                )
            }

            Column(
                Modifier
                    .weight(1f)
                    .clickable {
                        viewModel.nextNotation()
                    }) {
                val rightArrow: ImageVector =
                    ImageVector.vectorResource(id = R.drawable.ic_round_arrow_right)
                Icon(
                    imageVector = rightArrow,
                    contentDescription = "Next Move",
                    tint = tealDarker,
                    modifier = Modifier
                        .height(50.dp)
                        .padding(10.dp)
                        .aspectRatio(1f)
                        .align(CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun Piece(
    piece: ChessPiece,
    height: Float,
    viewModel: GameViewModel
) {
    key(piece) {
        val square = piece.square
        val offsetX = height * (square.file).toFloat()
        val offsetY = (7 - square.rank).toFloat() * height
        val offsetXAnimated by animateDpAsState(targetValue = offsetX.dp, tween(250))
        val offsetYAnimated by animateDpAsState(targetValue = offsetY.dp, tween(250))
        val imageVector = ImageVector.vectorResource(piece.pieceDrawable)
        Image(
            imageVector = imageVector,
            contentDescription = "Chess Piece",
            modifier = Modifier
                .height(height.dp)
                .aspectRatio(1f)
                .offset(offsetXAnimated, offsetYAnimated)
                .clickable {
                    viewModel.selectPiece(piece)
                }
        )
    }

}

@Composable
fun Pieces(viewModel: GameViewModel, height: Float) {
    println("printing pieces again")
    viewModel.onUpdate.value
    viewModel.piecesOnBoard.value.forEach { piece ->
        if (viewModel.kingInCheck() && piece.square == viewModel.kingSquare()) {
            Highlight(height = height, square = piece.square, orange, 1f)
        }
        if (piece.square == viewModel.getCurrentSquare().value) {
            Highlight(height = height, square = piece.square, Color.Yellow, .5f)
        }
        key(piece) {
            Piece(
                piece = piece,
                height = height,
                viewModel = viewModel
            )
        }
    }
    val previousSquare = viewModel.getPreviousSquare().value
    if (previousSquare.rank != 10) {
        Highlight(height = height, square = previousSquare, color = Color.Yellow, .5f)
    }
}

@Composable
fun ChessSquaresV2(height: Float, viewModel: GameViewModel) {
    val cardVisible = remember { mutableStateOf(false) }
    // val piecesOnBoard = viewModel.getPiecesOnBoard() as List<ChessPiece>
    val hashMap = viewModel.getHashMap()
    val clicked = remember { viewModel.isPieceClicked() }
    val squareClicked = remember { mutableStateOf(false) }
    val promotionSelectionShowing = remember { mutableStateOf(false) }
    val clickedPiece = remember {
        viewModel.getSelectedPiece()
    }
    val targetRank = remember {
        mutableStateOf(0)
    }
    val targetFile = remember {
        mutableStateOf(0)
    }

    val checkmate = remember {
        viewModel.getCheckmate()
    }

    val stalemate = remember {
        viewModel.getStalemate()
    }

    val insufficientMaterial = remember {
        viewModel.getInsufficientMaterial()
    }

    val threeFoldRepetition = remember {
        viewModel.getThreeFoldRepetition()
    }

    val fiftyMoveRule = remember {
        viewModel.getFiftyMoveRule()
    }
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


    if (clicked.value) {
        val legalMoves = viewModel.onEvent(GameEvent.OnPieceClicked, clickedPiece.value)
        if (legalMoves != null) {
            for (move in legalMoves) {
                if (hashMap.containsKey(move)) {
                    PossibleCapture(height, move, targetRank, targetFile, squareClicked)
                } else {
                    PossibleMove(height, move, targetRank, targetFile, squareClicked)
                }
            }
        }
    }

    if (squareClicked.value) {
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
        clicked.value = false
        squareClicked.value = false
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

@Composable
private fun Highlight(
    height: Float, square: Square, color: Color, transparency: Float
) {
    println(height)
    val offsetX = height * square.file
    val offsetY = (7 - square.rank) * height
    Canvas(
        modifier = Modifier
            .height(height.dp)
            .aspectRatio(1f)
            .absoluteOffset(offsetX.dp, offsetY.dp)
    ) {
        drawRect(
            color = color,
            size = size,
            alpha = transparency
        )
    }
}

@Composable
private fun PossibleMove(
    height: Float,
    square: Square,
    targetRank: MutableState<Int>,
    targetFile: MutableState<Int>,
    squareClicked: MutableState<Boolean>
) {
    val offsetX = height * square.file
    val offsetY = (7 - square.rank) * height
    Canvas(
        modifier = Modifier
            .height(height.dp)
            .aspectRatio(1f)
            .absoluteOffset(offsetX.dp, offsetY.dp)
            .clickable {
                targetRank.value = square.rank
                targetFile.value = square.file
                squareClicked.value = true
            }
    ) {
        drawCircle(
            color = Color.Black,
            radius = size.width / 8,
            alpha = .75f
        )
    }
}

@Composable
private fun PossibleCapture(
    height: Float,
    square: Square,
    targetRank: MutableState<Int>,
    targetFile: MutableState<Int>,
    squareClicked: MutableState<Boolean>
) {
    val offsetX = height * square.file
    val offsetY = (7 - square.rank) * height
    Canvas(
        modifier = Modifier
            .height(height.dp)
            .aspectRatio(1f)
            .absoluteOffset(offsetX.dp, offsetY.dp)
            .clickable {
                targetRank.value = square.rank
                targetFile.value = square.file
                squareClicked.value = true
            }
    ) {
        drawCircle(
            color = Color.Red,
            radius = size.width * .45f,
            alpha = .5f,
            style = Stroke(size.width * .05f)
        )
    }
}

@Composable
private fun Outline(
    height: Float,
    square: Square,
    color: Color
) {
    val offsetX = height * square.file
    val offsetY = (7 - square.rank) * height
    Canvas(
        modifier = Modifier
            .height(height.dp)
            .aspectRatio(1f)
            .absoluteOffset(offsetX.dp, offsetY.dp)
            .padding(1.dp)

    ) {
        drawRect(
            color = color,
            size = size,
            alpha = 1f,
            style = Stroke(size.width * .05f)
        )
    }
}