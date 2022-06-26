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

class ChessPieces {
    fun blackRook(rank:Int, file: Int): ChessPiece {
        return ChessPiece(
            "black",
            "rook",
            R.drawable.ic_br_alpha,
            Square(rank, file),
            5, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
        )
    }

    fun blackQueen(rank:Int, file: Int): ChessPiece {
        return ChessPiece(
            "black",
            "queen",
            R.drawable.ic_bq_alpha,
            Square(rank, file),
            9, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
        )
    }

    fun blackBishop(rank:Int, file: Int): ChessPiece {
        return ChessPiece(
            "black",
            "bishop",
            R.drawable.ic_bb_alpha,
            Square(rank, file),
            3, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
        )
    }

    fun blackKnight(rank:Int, file: Int): ChessPiece {
        return ChessPiece(
            "black",
            "knight",
            R.drawable.ic_bn_alpha,
            Square(rank, file),
            3, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
        )
    }

    fun blackPawn(rank:Int, file: Int): ChessPiece {
        return ChessPiece(
            "black",
            "pawn",
            R.drawable.ic_bp_alpha,
            Square(rank, file),
            1, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
        )
    }

    fun blackKing(rank:Int, file: Int): ChessPiece {
        return ChessPiece(
            "black",
            "king",
            R.drawable.ic_bk_alpha,
            Square(rank, file),
            0, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
        )
    }

    fun whiteRook(rank:Int, file: Int): ChessPiece {
        return ChessPiece(
            "white",
            "rook",
            R.drawable.ic_wr_alpha,
            Square(rank, file),
            5, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
        )
    }

    fun whiteQueen(rank:Int, file: Int): ChessPiece {
        return ChessPiece(
            "white",
            "queen",
            R.drawable.ic_wq_alpha,
            Square(rank, file),
            9, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
        )
    }

    fun whiteBishop(rank:Int, file: Int): ChessPiece {
        return ChessPiece(
            "white",
            "bishop",
            R.drawable.ic_wb_alpha,
            Square(rank, file),
            3, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
        )
    }

    fun whiteKnight(rank:Int, file: Int): ChessPiece {
        return ChessPiece(
                "white",
                "knight",
                R.drawable.ic_wn_alpha,
                Square(rank, file),
                3, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
            )
    }

    fun whiteKing(rank:Int, file: Int): ChessPiece {
        return ChessPiece(
            "white",
            "king",
            R.drawable.ic_wk,
            Square(rank, file),
            0, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf())
    }

    fun whitePawn(rank:Int, file: Int): ChessPiece {
        return ChessPiece(
            "white",
            "pawn",
            R.drawable.ic_wp_alpha,
            Square(rank, file),
            1, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf())
    }
}


















