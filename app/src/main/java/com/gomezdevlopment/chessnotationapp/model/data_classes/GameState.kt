package com.gomezdevlopment.chessnotationapp.model.data_classes

import com.gomezdevlopment.chessnotationapp.model.pieces.ChessPieces

data class GameState(
    var previousSquare: Square,
    var currentSquare: Square,
    var fenPosition: String
)