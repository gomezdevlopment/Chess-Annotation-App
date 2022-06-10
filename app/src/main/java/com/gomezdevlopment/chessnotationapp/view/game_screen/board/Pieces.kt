package com.gomezdevlopment.chessnotationapp.view.game_screen.board

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.Highlight
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.orange
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel

@Composable
fun Piece(
    piece: ChessPiece,
    height: Float,
    viewModel: GameViewModel
) {
    //viewModel.onUpdate.value
    val start = System.currentTimeMillis()
//    if (BuildConfig.DEBUG) {
//        SideEffect { println(System.currentTimeMillis()) }
//    }
    key(piece) {
        val square = piece.square
        val offsetX = height * (square.file).toFloat()
        val offsetY = (7 - square.rank).toFloat() * height
        val offsetXAnimated by animateDpAsState(targetValue = offsetX.dp, tween(100))
        val offsetYAnimated by animateDpAsState(targetValue = offsetY.dp, tween(100))
        val imageVector = ImageVector.vectorResource(piece.pieceDrawable)
        Image(
            imageVector = imageVector,
            contentDescription = "Chess Piece",
            modifier = Modifier
                .height(height.dp)
                .aspectRatio(1f)
                .zIndex(3f)
                .offset(offsetXAnimated, offsetYAnimated)
                .clickable {
                    viewModel.selectPiece(piece)
                }
        )
    }
    val end = System.currentTimeMillis()
    println(end-start)
}

@Composable
fun Pieces(viewModel: GameViewModel, height: Float) {
    val pieces = remember {
        viewModel.piecesOnBoard
    }
    val start = System.currentTimeMillis()
    pieces.asReversed().forEach() { piece ->
        key(piece) {
            Piece(
                piece = piece,
                height = height,
                viewModel = viewModel
            )
        }
        if (viewModel.kingInCheck() && piece.square == viewModel.kingSquare()) {
            Highlight(height = height, square = piece.square, orange, 1f)
        }
        if (piece.square == viewModel.getCurrentSquare().value) {
            Highlight(height = height, square = piece.square, Color.Yellow, .5f)
        }
    }
    val end = System.currentTimeMillis()
    println(end-start)
    val previousSquare = viewModel.getPreviousSquare().value
    if (previousSquare.rank != 10) {
        Highlight(height = height, square = previousSquare, color = Color.Yellow, .5f)
    }
}