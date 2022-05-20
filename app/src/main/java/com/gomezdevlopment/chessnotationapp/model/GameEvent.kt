package com.gomezdevlopment.chessnotationapp.model

sealed class GameEvent {
    object OnPieceClicked : GameEvent()
}
