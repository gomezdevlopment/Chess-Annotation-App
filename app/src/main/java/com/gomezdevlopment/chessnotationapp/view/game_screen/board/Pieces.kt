package com.gomezdevlopment.chessnotationapp.view.game_screen.board

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.utils.Utils
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.userColor
import com.gomezdevlopment.chessnotationapp.view.blue
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.*
import com.gomezdevlopment.chessnotationapp.view.orange
import com.gomezdevlopment.chessnotationapp.view.yellow
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel

@Composable
fun Piece(
    piece: ChessPiece,
    height: Dp,
    viewModel: GameViewModel,
    offset: Offset
) {
    val imageVector = ImageVector.vectorResource(piece.pieceDrawable)

    //key(piece) {
    Image(
        imageVector = imageVector,
        contentDescription = "Chess Piece",
        modifier =
        (if (piece.color == viewModel.getPlayerTurn() && (piece.color == userColor || !viewModel.isOnline.value)) Modifier.clickable(
            viewModel,
            piece,
            height, offset
        ) else Modifier.notClickable(height, offset))
    )
    // }
    if (piece == viewModel.getSelectedPiece().value && piece.color == viewModel.getPlayerTurn() && piece.color == userColor) {
        Highlight(height = height, square = piece.square, color = blue, transparency = 1f)
    }
    if (viewModel.kingInCheck() && piece.square == viewModel.kingSquare()) {
        Highlight(height = height, square = piece.square, orange, 1f)
    }
    if (piece.square == viewModel.getCurrentSquare().value) {
        Highlight(height = height, square = piece.square, yellow, .9f)
    }
    val end = System.currentTimeMillis()
    //println(end-start)
}

fun Modifier.notClickable(height: Dp, offset: Offset) =
    height(height)
        .aspectRatio(1f)
        .zIndex(3f)
        .offset(offset.x.dp, offset.y.dp)

fun Modifier.clickable(
    viewModel: GameViewModel,
    piece: ChessPiece, height: Dp, offset: Offset
) =
    notClickable(height, offset)
        .clickable {
            viewModel.selectPiece(piece)
        }


@Composable
fun Pieces(viewModel: GameViewModel, height: Dp) {
    val pieces = viewModel.piecesOnBoard
    val start = System.currentTimeMillis()
    pieces.forEach() { piece ->
        //val piece by derivedStateOf { chessPiece }
        //val square by derivedStateOf{occupiedSquare}
        key(piece) {
            val square = piece.square
            val offsetX = Utils().offsetX(height.value, square.file)
            val offsetY = Utils().offsetY(height.value, square.rank)
            val offset = Offset(offsetX, offsetY)
            //val animatedOffset by animateOffsetAsState(targetValue = offset, tween(100, easing = LinearEasing))
            Piece(
                piece = piece,
                height = height,
                viewModel = viewModel,
                offset
            )
        }
    }
    val end = System.currentTimeMillis()
    //println("Time for piece recomposition: ${end - start}ms")
    val previousSquare = viewModel.getPreviousSquare().value
    if (previousSquare.rank != 10) {
        Highlight(height = height, square = previousSquare, color = yellow, .9f)
    }
}