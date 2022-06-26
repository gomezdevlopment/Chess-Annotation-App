package com.gomezdevlopment.chessnotationapp.model.data_classes

data class ChessPiece(
    val color: String,
    var piece: String,
    var pieceDrawable: Int,
    var square: Square,
    var value: Int,
    val pseudoLegalMoves: MutableList<Square>,
    val attacks: MutableList<Square>,
    val xRays: MutableList<Square>,
    var legalMoves: MutableList<Square>,
    val pinnedMoves: MutableList<Square>
    )




