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
import com.gomezdevlopment.chessnotationapp.model.pieces.PromotionPieces
import com.gomezdevlopment.chessnotationapp.model.utils.Utils
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.userColor
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
    var pieces = PromotionPieces().whitePieces
    if (chessPiece.color == "black") {
        pieces = PromotionPieces().blackPieces
    }

    val list = listOf("queen", "rook", "bishop", "knight")

    val offsetX = Utils().offsetX(width.value, file).dp
    var offsetY = Utils().offsetY(width.value, rank).dp

    if (rank == 0 && userColor == "white") {
        offsetY = width * 4
    }

    if(rank == 7 && userColor == "black"){
        offsetY = width * 4
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
                list.forEach() {
                    var drawable = pieces[it]?.pieceDrawable
                    if(drawable == null){
                        drawable = com.google.android.material.R.drawable.mtrl_ic_error
                    }
                    Image(
                        imageVector = ImageVector.vectorResource(drawable),
                        contentDescription = "Chess Piece",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(5.dp)
                            .clickable {
                                promotionSelectionShowing.value = false
                                viewModel.changePiecePosition(Square(rank, file), chessPiece, pieces[it])
                            }
                    )
                }
            }
        }

    }
}