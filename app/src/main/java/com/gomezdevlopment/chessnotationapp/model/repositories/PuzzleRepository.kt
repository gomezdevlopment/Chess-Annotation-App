package com.gomezdevlopment.chessnotationapp.model.repositories

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.GameState
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameSetup
import kotlin.math.pow
import kotlin.math.roundToInt

class PuzzleRepository() : ViewModel(), GameSetup {
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

    val playerRating = mutableStateOf(600)
    private val k = 20
    var correct = mutableStateOf(false)
    var endOfPuzzle = mutableStateOf(false)

    val moveIndex = mutableStateOf(0)

    val userColor: MutableState<String> = mutableStateOf("white")

    fun makeComputerMove(move: String) {
        val pieceToMove: ChessPiece? = occupiedSquares[convertMoveToSquare(move, 0, 1)]
        val square: Square = convertMoveToSquare(move, 2, 3)

        println(move)
        println(square)
        if (pieceToMove != null) {
            changePiecePosition(square, pieceToMove, null)
            moveIndex.value += 1
            println(moveIndex.value)
        }else{
            println("null piece")
        }
    }

    fun checkIfMoveIsCorrect(square: Square, piece: ChessPiece, correctMove: String, puzzleRating: Float) {
        val squareFromMove = convertMoveToSquare(correctMove, 0, 1)
        println("square from move: $squareFromMove")
        val correctPiece: ChessPiece? = occupiedSquares[squareFromMove]
        val correctSquare: Square = convertMoveToSquare(correctMove, 2, 3)

        if (piece == correctPiece && square == correctSquare) {
            correct.value = true
            playerRating.value = Elo().eloRating(playerRating.value.toFloat(), puzzleRating, k, correct.value)
        } else {
            correct.value = false
            endOfPuzzle.value = true
            playerRating.value = Elo().eloRating(playerRating.value.toFloat(), puzzleRating, k, correct.value)
        }
    }


    private fun convertMoveToSquare(move: String, fileIndex: Int, rankIndex: Int): Square {
        val rank: Int = move[rankIndex].digitToInt() - 1
        val file = when (move[fileIndex]) {
            'a' -> 0
            'b' -> 1
            'c' -> 2
            'd' -> 3
            'e' -> 4
            'f' -> 5
            'g' -> 6
            'h' -> 7
            else -> {
                8
            }
        }
        return Square(rank, file)
    }
}

class Elo() {

    private fun probability(
        rating1: Float,
        rating2: Float
    ): Float {
        return 1.0f * 1.0f / (1 + 1.0f * 10.0.pow(
            (1.0f *
                    (rating1 - rating2) / 400).toDouble()
        ).toFloat())
    }

    fun eloRating(eloPlayer: Float, eloPuzzle: Float, K: Int, d: Boolean): Int {
        val pA = probability(eloPuzzle, eloPlayer)
        val pB = probability(eloPlayer, eloPuzzle)
        var newElo = eloPlayer

        newElo += if (d) {
            K * (1 - pA)
        } else {
            K * (0 - pA)
        }

        return newElo.roundToInt()
    }
}