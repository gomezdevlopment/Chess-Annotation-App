package com.gomezdevlopment.chessnotationapp.model.pieces

import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square

data class PromotionPiece(
    val color: String,
    val piece: String,
    val pieceDrawable: Int,
    val value: Int,
)

class PromotionPieces {
    val blackPieces = mapOf(
        "queen" to PromotionPiece("black", "queen", R.drawable.ic_bq_alpha, 9),
        "rook" to PromotionPiece("black", "rook", R.drawable.ic_br_alpha, 5),
        "bishop" to PromotionPiece("black", "bishop", R.drawable.ic_bb_alpha, 3),
        "queen" to PromotionPiece("black", "knight", R.drawable.ic_bn_alpha, 3)
    )

    val whitePieces = mapOf(
        "queen" to PromotionPiece("white", "queen", R.drawable.ic_wq_alpha, 9),
        "rook" to PromotionPiece("white", "rook", R.drawable.ic_wr_alpha, 5),
        "bishop" to PromotionPiece("white", "bishop", R.drawable.ic_wb_alpha, 3),
        "queen" to PromotionPiece("white", "knight", R.drawable.ic_wn_alpha, 3)
    )
}