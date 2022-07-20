package com.gomezdevlopment.chessnotationapp.view.game_screen.board

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.utils.Utils
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.*
import com.gomezdevlopment.chessnotationapp.view.theming.*

private fun pieceIcon(piece: ChessPiece, theme: Map<String, Int>): Int? {
    when (piece.piece) {
        "king" -> return if (piece.color == "white") theme["wk"] else theme["bk"]
        "queen" -> return if (piece.color == "white") theme["wq"] else theme["bq"]
        "rook" -> return if (piece.color == "white") theme["wr"] else theme["br"]
        "bishop" -> return if (piece.color == "white") theme["wb"] else theme["bb"]
        "knight" -> return if (piece.color == "white") theme["wn"] else theme["bn"]
        "pawn" -> return if (piece.color == "white") theme["wp"] else theme["bp"]
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
    theme: Map<String, Int>,
    hint: Boolean,
    correctPiece: ChessPiece?
) {
    val imageVector = ImageVector.vectorResource(pieceIcon(piece, theme) ?: piece.pieceDrawable)

    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        )
    )
    key(piece) {
        Image(
            imageVector = imageVector,
            contentDescription = "Chess Piece",
            modifier =
            (if (piece.color == playerTurn && (piece.color == userColor || userColor == "both"))
                Modifier.clickable(
                    selectedPiece,
                    pieceClicked,
                    piece,
                    height, offset
                ).scale(if (hint && piece == correctPiece) scale else 1f)
            else Modifier.notClickable(height, offset))
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
    pieceAnimationSpeed: Int,
    highlightStyle: String,
    hint: Boolean,
    correctPiece: ChessPiece?
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
                theme = theme,
                hint = hint,
                correctPiece = correctPiece
            )
        }
    }
    if (previousSquare.rank != 10) {
        if (highlightStyle == "Outline") {
            Outline(height = height, square = previousSquare, color = yellow)
        } else {
            Highlight(height = height, square = previousSquare, color = yellow, .9f)
        }
    }

    if (currentSquare.rank != 10) {
        if (highlightStyle == "Outline") {
            Outline(height = height, square = currentSquare, color = yellow)
        } else {
            Highlight(height = height, square = currentSquare, yellow, .9f)
        }
    }

    if (pieceClicked.value && (selectedPiece.value.color == userColor || userColor == "both")) {
        if (highlightStyle == "Outline") {
            Outline(height = height, square = selectedPiece.value.square, color = blue)
        } else {
            Highlight(
                height = height,
                square = selectedPiece.value.square,
                color = blue,
                transparency = 1f
            )
        }
    }

    if (kingInCheck) {
        if (highlightStyle == "Outline") {
            Outline(height = height, square = kingSquare.value, color = redIncorrect)
        } else {
            Highlight(height = height, square = kingSquare.value, redIncorrect, 1f)
        }
    }

}