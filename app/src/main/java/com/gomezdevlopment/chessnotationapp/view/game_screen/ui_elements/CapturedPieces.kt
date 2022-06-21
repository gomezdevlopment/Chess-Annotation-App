package com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel


@Composable
fun WhiteCaptures(pieces: List<ChessPiece>) {
    LazyRow(
        Modifier
            .fillMaxWidth()
            .height(30.dp)
            .padding(10.dp, 0.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var pieceValue = 0
        itemsIndexed(pieces) { _, piece ->
            if (piece.color == "black") {
                PieceIcon(piece = piece)
                pieceValue += piece.value
            } else {
                pieceValue -= piece.value
            }
        }
        item {
            if (pieceValue > 0) {
                Text(text = "+$pieceValue", fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun BlackCaptures(pieces: List<ChessPiece>) {
    LazyRow(
        Modifier
            .fillMaxWidth()
            .height(30.dp)
            .padding(10.dp, 0.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var pieceValue = 0
        itemsIndexed(pieces) { _, piece ->
            if (piece.color == "white") {
                PieceIcon(piece = piece)
                pieceValue += piece.value
            } else {
                pieceValue -= piece.value
            }
        }
        item {
            if (pieceValue > 0) {
                Text(text = "+$pieceValue", fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun PieceIcon(piece: ChessPiece) {
    val imageVector = ImageVector.vectorResource(piece.pieceDrawable)
    Image(
        imageVector = imageVector,
        contentDescription = "Chess Piece",
        modifier = Modifier
            .height(14.dp)
            .aspectRatio(1f)
    )

}

@Composable
fun WhiteCaptures(viewModel: GameViewModel){
    val capturedPieces = remember {
        viewModel.capturedPieces
    }
    WhiteCaptures(pieces = capturedPieces)
}

@Composable
fun BlackCaptures(viewModel: GameViewModel){
    val capturedPieces = remember {
        viewModel.capturedPieces
    }
    BlackCaptures(pieces = capturedPieces)
}
