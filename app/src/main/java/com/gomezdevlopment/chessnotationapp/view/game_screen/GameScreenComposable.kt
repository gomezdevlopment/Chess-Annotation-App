package com.gomezdevlopment.chessnotationapp.view.game_screen

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        Column() {
            ChessSquares(height = rowWidthAndHeight, pieces = pieces, viewModel = viewModel)
        }
    }
}


@Composable
fun ChessSquares(height: Float, pieces: MutableList<ChessPiece>, viewModel: GameViewModel) {
    val files = listOf(0, 1, 2, 3, 4, 5, 6, 7)
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
                    Image(
                        imageVector = ImageVector.vectorResource(chessPiece.pieceDrawable),
                        contentDescription = "Chess Board",
                        modifier = Modifier
                            .height(height.dp)
                            .aspectRatio(1f)
                            .absoluteOffset(0.dp, 0.dp)
                            .clickable {
                                viewModel.onEvent(GameEvent.OnPieceClicked, chessPiece)
                            }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .height(height.dp)
                            .aspectRatio(1f)
                            .absoluteOffset(0.dp, 0.dp)
                    )
                }
            }
        }
    }
}

//@Preview
//@Composable
//fun ChessCanvasPreview() {
//    ChessCanvas(350)
//}