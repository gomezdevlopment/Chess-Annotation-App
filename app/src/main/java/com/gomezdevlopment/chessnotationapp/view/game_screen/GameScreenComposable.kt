package com.gomezdevlopment.chessnotationapp.view.game_screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.GameEvent
import com.gomezdevlopment.chessnotationapp.model.Square
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel


@Composable
fun ChessCanvas(width: Int, viewModel: GameViewModel) {
    val chessBoardVector: ImageVector =
        ImageVector.vectorResource(id = R.drawable.ic_chess_board_teal)

    val rowWidthAndHeight: Float = (width.toFloat() / 8F)

    Box(
        modifier = Modifier
            .width(width.dp)
            .aspectRatio(1f)
            .background(colorResource(id = R.color.orange))
    ) {
        Image(
            imageVector = chessBoardVector,
            contentDescription = "Chess Board",
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        )
        ChessSquaresV2(height = rowWidthAndHeight, viewModel = viewModel)
        Button(
            onClick = { viewModel.resetGame()},
            modifier = Modifier.offset((50).dp, (-50).dp),
            enabled = true
        ) {
            Text(text = "Reset Game")
        }
    }

}

@Composable
fun ChessSquaresV2(height: Float, viewModel: GameViewModel) {
    val hashMap = viewModel.getHashMap()
    val clicked = remember { mutableStateOf(false) }
    val squareClicked = remember { mutableStateOf(false) }
    val clickedPiece = remember {
        mutableStateOf(ChessPiece("black", "rook", R.drawable.ic_br_alpha, Square(7, 0)))
    }
    val targetRank = remember {
        mutableStateOf(0)
    }
    val targetFile = remember {
        mutableStateOf(0)
    }

    val xRays = remember {
        viewModel.xRays()
    }

    val attackedSquares = remember {
        viewModel.getSquaresToBlock()
    }

    val attacks = remember {
        viewModel.getAttacks()
    }

    for (rank in 7 downTo 0) {
        for (file in 0..7) {
            val square = Square(rank, file)
            val offsetX = height * file
            val offsetY = (7 - rank) * height
            if(attackedSquares.contains(square)){
                Highlight(height = height, square = square, Color.Blue)
            }
            if(xRays.contains(square)){
                Outline(height = height, square = square, Color.Yellow)
            }
            if(attacks.contains(square)){
                Attack(height = height, square = square)
            }

            if (hashMap.containsKey(square)) {
                val chessPiece = hashMap[square]!!
                val imageVector = ImageVector.vectorResource(chessPiece.pieceDrawable)
                if (square == viewModel.getCurrentSquare().value) {
                    //Outline(height = height, square = square, Color.Blue)
                    Highlight(height = height, square = square, Color.Blue)
                }
                if(viewModel.kingInCheck() && square == viewModel.kingSquare()){
                    Outline(height = height, square = square, Color.Red)
                }
                Image(
                    imageVector = imageVector,
                    contentDescription = "Chess Board",
                    modifier = Modifier
                        .height(height.dp)
                        .aspectRatio(1f)
                        .offset(offsetX.dp, offsetY.dp)
                        .clickable {
                            clicked.value = false
                            clickedPiece.value = chessPiece
                            if (viewModel.getPlayerTurn() == clickedPiece.value.color) {
                                clickedPiece.value.square
                                clicked.value = true
                            }
                        }
                )
            }
        }
    }

    if (clicked.value) {
        val legalMoves = viewModel.onEvent(GameEvent.OnPieceClicked, clickedPiece.value)
        for (move in legalMoves) {
            if (viewModel.getHashMap().containsKey(move)) {
                PossibleCapture(height, move, targetRank, targetFile, squareClicked)
            }else{
                PossibleMove(height, move, targetRank, targetFile, squareClicked)
            }
        }
    }

    if (squareClicked.value) {
        clicked.value = false
        squareClicked.value = false
        viewModel.changePiecePosition(
            Square(targetRank.value, targetFile.value),
            clickedPiece.value
        )
    }

    val previousSquare = viewModel.getPreviousSquare().value
    if (previousSquare.rank != 10) {
        //Outline(height = height, square = previousSquare, Color.Blue)
        Highlight(height = height, square = previousSquare, color = Color.Blue)
    }
}

@Composable
private fun Highlight(
    height: Float, square: Square, color: Color
) {
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
            alpha = .5f
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
            radius = height / 4,
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
            radius = height*.8f,
            alpha = .5f,
            style = Stroke(5f)
        )
    }
}

@Composable
private fun Attack(
    height: Float,
    square: Square
) {
    val offsetX = height * square.file
    val offsetY = (7 - square.rank) * height
    Canvas(
        modifier = Modifier
            .height(height.dp)
            .aspectRatio(1f)
            .absoluteOffset(offsetX.dp, offsetY.dp)

    ) {
        drawCircle(
            color = Color.Red,
            radius = height*.8f,
            alpha = .5f,
            style = Stroke(5f)
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
            style = Stroke(5f)
        )
    }
}
