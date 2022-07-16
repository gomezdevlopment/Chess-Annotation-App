package com.gomezdevlopment.chessnotationapp.view.game_screen.board

import androidx.appcompat.resources.R
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
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
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.utils.Utils
import com.gomezdevlopment.chessnotationapp.view.theming.blue
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.*
import com.gomezdevlopment.chessnotationapp.view.theming.alpha
import com.gomezdevlopment.chessnotationapp.view.theming.redIncorrect
import com.gomezdevlopment.chessnotationapp.view.theming.yellow

//@Composable
//fun Piece(
//    piece: ChessPiece,
//    height: Dp,
//    viewModel: GameViewModel,
//    offset: Offset
//) {
//    val imageVector = ImageVector.vectorResource(piece.pieceDrawable)
//
//    //key(piece) {
//    Image(
//        imageVector = imageVector,
//        contentDescription = "Chess Piece",
//        modifier =
//        (if (piece.color == viewModel.getPlayerTurn() && (piece.color == userColor || !viewModel.isOnline.value)) Modifier.clickable(
//            viewModel,
//            piece,
//            height, offset
//        ) else Modifier.notClickable(height, offset))
//    )
//    // }
//    if (piece == viewModel.getSelectedPiece().value && piece.color == viewModel.getPlayerTurn() && piece.color == userColor) {
//        Highlight(height = height, square = piece.square, color = blue, transparency = 1f)
//    }
//    if (viewModel.kingInCheck() && piece.square == viewModel.kingSquare()) {
//        Highlight(height = height, square = piece.square, orange, 1f)
//    }
//    if (piece.square == viewModel.getCurrentSquare().value) {
//        Highlight(height = height, square = piece.square, yellow, .9f)
//    }
//    val end = System.currentTimeMillis()
//    //println(end-start)
//}
//
//fun Modifier.notClickable(height: Dp, offset: Offset) =
//    height(height)
//        .aspectRatio(1f)
//        .zIndex(3f)
//        .offset(offset.x.dp, offset.y.dp)
//
//fun Modifier.clickable(
//    viewModel: GameViewModel,
//    piece: ChessPiece, height: Dp, offset: Offset
//) =
//    notClickable(height, offset)
//        .clickable {
//            viewModel.selectPiece(piece)
//        }
//
//
//@Composable
//fun Pieces(viewModel: GameViewModel, height: Dp) {
//    val pieces = viewModel.piecesOnBoard
//    val start = System.currentTimeMillis()
//    pieces.forEach() { piece ->
//        //val piece by derivedStateOf { chessPiece }
//        //val square by derivedStateOf{occupiedSquare}
//        key(piece) {
//            val square = piece.square
//            val offsetX = Utils().offsetX(height.value, square.file)
//            val offsetY = Utils().offsetY(height.value, square.rank)
//            val offset = Offset(offsetX, offsetY)
//            //val animatedOffset by animateOffsetAsState(targetValue = offset, tween(100, easing = LinearEasing))
//            Piece(
//                piece = piece,
//                height = height,
//                viewModel = viewModel,
//                offset
//            )
//        }
//    }
//    val end = System.currentTimeMillis()
//    //println("Time for piece recomposition: ${end - start}ms")
//    val previousSquare = viewModel.getPreviousSquare().value
//    if (previousSquare.rank != 10) {
//        Highlight(height = height, square = previousSquare, color = yellow, .9f)
//    }
//}

private fun pieceIcon(piece: ChessPiece, theme: Map<String, Int>): Int? {
    when (piece.piece) {
        "king" -> return if(piece.color == "white") theme["wk"] else theme["bk"]
        "queen" -> return if(piece.color == "white") theme["wq"] else theme["bq"]
        "rook" -> return if(piece.color == "white") theme["wr"] else theme["br"]
        "bishop" -> return if(piece.color == "white") theme["wb"] else theme["bb"]
        "knight" -> return if(piece.color == "white") theme["wn"] else theme["bn"]
        "pawn" -> return if(piece.color == "white") theme["wp"] else theme["bp"]
    }
    return theme["wp"]
}

@Composable
fun Piece(
    piece: ChessPiece,
    height: Dp,
    offset: Offset,
    playerTurn: String,
    userColor: String,
    selectedPiece: MutableState<ChessPiece>,
    pieceClicked: MutableState<Boolean>,
    theme: Map<String, Int>
) {
    val imageVector = ImageVector.vectorResource(pieceIcon(piece, theme) ?: piece.pieceDrawable)

    key(piece) {
        Image(
            imageVector = imageVector,
            contentDescription = "Chess Piece",
            modifier =
            (if (piece.color == playerTurn && (piece.color == userColor || userColor == "both")) Modifier.clickable(
                selectedPiece,
                pieceClicked,
                piece,
                height, offset
            ) else Modifier.notClickable(height, offset))
        )
    }
}

fun Modifier.notClickable(height: Dp, offset: Offset) =
    height(height)
        .aspectRatio(1f)
        .zIndex(3f)
        .offset(offset.x.dp, offset.y.dp)

fun Modifier.clickable(
    selectedPiece: MutableState<ChessPiece>,
    pieceClicked: MutableState<Boolean>,
    piece: ChessPiece,
    height: Dp,
    offset: Offset
) =
    notClickable(height, offset)
        .clickable {
            selectedPiece.value = piece
            pieceClicked.value = true
        }


@Composable
fun Pieces(
    height: Dp,
    pieces: List<ChessPiece>,
    playerTurn: String,
    userColor: String,
    pieceClicked: MutableState<Boolean>,
    selectedPiece: MutableState<ChessPiece>,
    kingInCheck: Boolean,
    currentSquare: Square,
    previousSquare: Square,
    kingSquare: MutableState<Square>,
    theme: Map<String, Int>,
    pieceAnimationSpeed: Int
) {
    pieces.forEach() { piece ->
        key(piece) {
            val square = piece.square
            val offsetX = Utils().offsetX(height.value, square.file)
            val offsetY = Utils().offsetY(height.value, square.rank)
            val offset = Offset(offsetX, offsetY)
            val animatedOffset by animateOffsetAsState(
                targetValue = offset,
                tween(pieceAnimationSpeed, easing = LinearEasing),
            )
            Piece(
                piece = piece,
                height = height,
                offset = animatedOffset,
                playerTurn = playerTurn,
                userColor = userColor,
                selectedPiece = selectedPiece,
                pieceClicked = pieceClicked,
                theme = theme
            )
        }
    }
    if (previousSquare.rank != 10) {
        Outline(height = height, square = previousSquare, color = yellow)
        //Highlight(height = height, square = previousSquare, color = yellow, .9f)
    }

    if (currentSquare.rank != 10) {
        Outline(height = height, square = currentSquare, color = yellow)
        //Highlight(height = height, square = currentSquare, yellow, .9f)
    }

    if (pieceClicked.value && selectedPiece.value.color == userColor) {
        Outline(height = height, square = selectedPiece.value.square, color = blue)
//        Highlight(
//            height = height,
//            square = selectedPiece.value.square,
//            color = blue,
//            transparency = 1f
//        )
    }

    if (kingInCheck) {
        Outline(height = height, square = kingSquare.value, color = redIncorrect)
        //Highlight(height = height, square = kingSquare.value, orange, 1f)
    }

}