package com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel

@Composable
fun Promotion(
    width: Dp,
    chessPiece: ChessPiece,
    promotionSelectionShowing: MutableState<Boolean>,
    rank: Int,
    file: Int,
    viewModel: GameViewModel
) {
    val pieces = mutableListOf<ChessPiece>()
    val blackPieceImages = listOf(
        ChessPiece("black", "queen", R.drawable.ic_bq_alpha, Square(rank, file), 9),
        ChessPiece("black", "rook", R.drawable.ic_br_alpha, Square(rank, file), 5),
        ChessPiece("black", "bishop", R.drawable.ic_bb_alpha, Square(rank, file), 3),
        ChessPiece("black", "knight", R.drawable.ic_bn_alpha, Square(rank, file), 3)
    )

    val whitePieceImages = listOf(
        ChessPiece("white", "queen", R.drawable.ic_wq_alpha, Square(rank, file), 9),
        ChessPiece("white", "rook", R.drawable.ic_wr_alpha, Square(rank, file), 5),
        ChessPiece("white", "bishop", R.drawable.ic_wb_alpha, Square(rank, file), 3),
        ChessPiece("white", "knight", R.drawable.ic_wn_alpha, Square(rank, file), 3)
    )

    if (chessPiece.color == "white") {
        pieces.addAll(whitePieceImages)
    } else {
        pieces.addAll(blackPieceImages)
    }

    val offsetX = width * file
    var offsetY = width * (7 - rank)
    if (rank == 0) {
        offsetY =width * 4
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(id = R.color.transparentBlack))
            .zIndex(4f)
    )
    {
        Card(
            modifier = Modifier
                .width(width)
                .height((width * 4))
                .absoluteOffset(offsetX, offsetY),
            elevation = 5.dp,
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(Modifier.fillMaxWidth()) {
                pieces.forEach { selectedPiece ->
                    Image(
                        imageVector = ImageVector.vectorResource(selectedPiece.pieceDrawable),
                        contentDescription = "Chess Piece",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(5.dp)
                            .clickable {
                                promotionSelectionShowing.value = false
                                viewModel.promotion(Square(rank, file), selectedPiece)
                            }
                    )
                }
            }
        }

    }
}