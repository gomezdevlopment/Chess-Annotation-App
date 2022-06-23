package com.gomezdevlopment.chessnotationapp.model.data_classes

data class ChessPiece(
    val color: String,
    val piece: String,
    val pieceDrawable: Int,
    var square: Square,
    val value: Int,
    val pseudoLegalMoves: MutableList<Square>,
    val attacks: MutableList<Square>,
    val xRays: MutableList<Square>,
    var legalMoves: MutableList<Square>,
    val pinnedMoves: MutableList<Square>
    )




