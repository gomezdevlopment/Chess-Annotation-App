package com.gomezdevlopment.chessnotationapp.view.game_screen

import android.app.Application
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
        ChessSquaresV2(height = rowWidthAndHeight, pieces = pieces, viewModel = viewModel)

    }
}


@Composable
fun ChessSquares(height: Float, pieces: MutableList<ChessPiece>, viewModel: GameViewModel) {
    val files = listOf(0, 1, 2, 3, 4, 5, 6, 7)
    val clicked = remember { mutableStateOf(false) }
    for (rank in 7 downTo 0) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(height.dp)
        ) {

            items(files) { file ->
                var squareOccupied = false
                var chessPiece: ChessPiece = pieces[0]
                for (piece in pieces) {
                    if (piece.square.file == file && piece.square.rank == rank) {
                        squareOccupied = true
                        chessPiece = piece
                        break
                    }
                }
                if (squareOccupied) {
                    val imageVector = ImageVector.vectorResource(chessPiece.pieceDrawable)
                    Image(
                        imageVector = imageVector,
                        contentDescription = "Chess Board",
                        modifier = Modifier
                            .height(height.dp)
                            .aspectRatio(1f)
                            .absoluteOffset(0.dp, 0.dp)
                            .clickable {
                                //viewModel.onEvent(GameEvent.OnPieceClicked, chessPiece)
                                clicked.value = true
                            }
                    )
                }

                if (clicked.value) {
                    clicked.value = false
                }
            }
        }
    }
}

@Composable
fun ChessSquaresV2(height: Float, pieces: MutableList<ChessPiece>, viewModel: GameViewModel) {
    val hashMap = viewModel.getHashMap()
    val clicked = remember { mutableStateOf(false) }
    val clickedPiece = remember {
        mutableStateOf(ChessPiece("black", "rook", R.drawable.ic_br_alpha, Square(7,0)))
    }
    for (rank in 7 downTo 0) {
        for (file in 0..7) {
            val square = Square(rank, file)
            var offsetX = height * file
            var offsetY = (7-rank) * height
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
        for(move in legalMoves){
            val offsetX = height * move.file
            val offsetY = (7-move.rank) * height
            ShowHighlightedSquares(height = height, offsetX = offsetX, offsetY = offsetY)
        }
    }
}

@Composable
fun ShowHighlightedSquares(height: Float, offsetX: Float, offsetY: Float) {
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
            }
    )

}

@Composable
private fun Highlight(
    modifier: Modifier
) {

}
//@Preview
//@Composable
//fun ChessCanvasPreview() {
//    ChessCanvas(350)
//}