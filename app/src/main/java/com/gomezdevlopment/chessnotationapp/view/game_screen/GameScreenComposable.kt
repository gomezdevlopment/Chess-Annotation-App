package com.gomezdevlopment.chessnotationapp.view.game_screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
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
    val pieces = viewModel.getPiecesOnBoard()

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

    }
}

@Composable
fun ChessSquaresV2(height: Float, viewModel: GameViewModel) {
    val hashMap = viewModel.getHashMap()
    var isAnimated by remember { mutableStateOf(false) }
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

    @Composable
    fun ShowHighlightedSquares(height: Float, file: Int, rank: Int) {
        val offsetX = height * file
        val offsetY = (7 - rank) * height
        val circle: ImageVector =
            ImageVector.vectorResource(id = R.drawable.ic_legal_move_circle)
        Image(
            imageVector = circle,
            contentDescription = "Chess Board",
            modifier = Modifier
                .height(height.dp)
                .aspectRatio(1f)
                .absoluteOffset(offsetX.dp, offsetY.dp)
                .padding(10.dp)
                .clickable {
                    targetRank.value = rank
                    targetFile.value = file
                    squareClicked.value = true
                }
        )
    }

    @Composable
    fun ShowPreviousSquare(height: Float, file: Int, rank: Int) {
        val offsetX = height * file
        val offsetY = (7 - rank) * height
        val circle: ImageVector =
            ImageVector.vectorResource(id = R.drawable.ic_captured_highlight)
        Icon(
            tint = Color.Green,
            imageVector = circle,
            contentDescription = "Chess Board",
            modifier = Modifier
                .height(height.dp)
                .aspectRatio(1f)
                .absoluteOffset(offsetX.dp, offsetY.dp)
                .clickable {
                    targetRank.value = rank
                    targetFile.value = file
                    squareClicked.value = true
                }
        )
    }

    for (rank in 7 downTo 0) {
        for (file in 0..7) {
            val square = Square(rank, file)
            val offsetX = height * file
            val offsetY = (7 - rank) * height
            if (hashMap.containsKey(square)) {
                val chessPiece = hashMap[square]!!
                val imageVector = ImageVector.vectorResource(chessPiece.pieceDrawable)
                Image(
                    imageVector = imageVector,
                    contentDescription = "Chess Board",
                    modifier = Modifier
                        .height(height.dp)
                        .aspectRatio(1f)
                        .offset(offsetX.dp, offsetY.dp)
                        .clickable {
                            clicked.value = true
                            clickedPiece.value = chessPiece
                        }
                )
            }
        }
    }

    if (clicked.value) {
        val legalMoves = viewModel.onEvent(GameEvent.OnPieceClicked, clickedPiece.value)
        for (move in legalMoves) {
            ShowHighlightedSquares(height = height, rank = move.rank, file = move.file)
        }
    }

    if (squareClicked.value) {
        clicked.value = false
        squareClicked.value = false
        viewModel.changePiecePosition(Square(targetRank.value, targetFile.value), clickedPiece.value)
    }

    val previousSquare = viewModel.getPreviousSquare().value
    if(previousSquare.rank != 10){
        ShowPreviousSquare(height = height, file = previousSquare.file, rank = previousSquare.rank)
    }
}

//@Preview
//@Composable
//fun ChessCanvasPreview() {
//    ChessCanvas(350)
//}