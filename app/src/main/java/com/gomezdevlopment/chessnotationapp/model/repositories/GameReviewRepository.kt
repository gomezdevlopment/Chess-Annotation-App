package com.gomezdevlopment.chessnotationapp.model.repositories

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.GameState
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameSetup

class GameReviewRepository: ViewModel(), GameSetup {
    override var piecesOnBoard: MutableList<ChessPiece> = mutableStateListOf()
    override var capturedPieces: MutableList<ChessPiece> = mutableStateListOf()
    override var occupiedSquares: MutableMap<Square, ChessPiece> = mutableMapOf()
    override var previousSquare: MutableState<Square> = mutableStateOf(Square(10, 10))
    override var currentSquare: MutableState<Square> = mutableStateOf(Square(10, 10))
    override var playerTurn: MutableState<String> = mutableStateOf("white")
    override var whiteKingSquare: MutableState<Square> = mutableStateOf(Square(0, 4))
    override var blackKingSquare: MutableState<Square> = mutableStateOf(Square(7, 4))
    override var xRayAttacks: MutableList<Square> = mutableListOf()
    override var attacks: MutableList<Square> = mutableListOf()
    override var pinnedPieces = mutableListOf<ChessPiece>()
    override var allLegalMoves: MutableList<Square> = mutableListOf()
    override var whiteCanCastleKingSide: MutableState<Boolean> = mutableStateOf(false)
    override var whiteCanCastleQueenSide: MutableState<Boolean> = mutableStateOf(false)
    override var blackCanCastleKingSide: MutableState<Boolean> = mutableStateOf(false)
    override var blackCanCastleQueenSide: MutableState<Boolean> = mutableStateOf(false)
    override var kingInCheck: MutableState<Boolean> = mutableStateOf(false)
    override var piecesCheckingKing = mutableListOf<ChessPiece>()
    override var checksOnKing = mutableListOf<Square>()
    override var previousGameStates = mutableListOf<GameState>()
    override var annotations: MutableList<String> = mutableStateListOf("start:")
    override var currentNotation: StringBuilder = StringBuilder("")
    override var selectedNotationIndex: MutableState<Int> = mutableStateOf(0)
    override val openDrawOfferedDialog = mutableStateOf(false)
    override val pinnedPiecesFromPreviousTurns = mutableListOf<List<ChessPiece>>(listOf())
    override var pieceSound: MutableState<Boolean> = mutableStateOf(false)
    override var checkSound: MutableState<Boolean> = mutableStateOf(false)
    override var captureSound: MutableState<Boolean> = mutableStateOf(false)
    override var castlingSound: MutableState<Boolean> = mutableStateOf(false)
    override var kingSquare: MutableState<Square> = mutableStateOf(blackKingSquare.value)
}