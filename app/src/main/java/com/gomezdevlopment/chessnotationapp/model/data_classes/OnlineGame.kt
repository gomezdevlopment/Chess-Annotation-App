package com.gomezdevlopment.chessnotationapp.model.data_classes

data class OnlineGame(
    val joinable: Boolean = true,
    val whitePlayer: String = "",
    val blackPlayer: String = "",
    val timeControl: Long = 300000L,
    val previousMove: String = "",
    val resignation: String = "",
    val drawOffer: String = "",
    val rematchOffer: String = "",
    val cancel: Boolean = false
)
