package com.gomezdevlopment.chessnotationapp.model.repositories

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.GameState
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.firestore_game_interaction.FirestoreGameInteraction
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameSetup
import com.gomezdevlopment.chessnotationapp.model.pieces.PromotionPiece
import com.gomezdevlopment.chessnotationapp.model.pieces.PromotionPieces
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import com.gomezdevlopment.chessnotationapp.view.game_screen.board.Pieces
import kotlinx.coroutines.launch
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
    override var kingSquare: MutableState<Square> = mutableStateOf(blackKingSquare.value)

    val playerRating: MutableState<Int> = mutableStateOf(600)
    private val k = 20
    var correct = mutableStateOf(false)
    var endOfPuzzle = mutableStateOf(false)

    val moveIndex = mutableStateOf(0)

    val userColor: MutableState<String> = mutableStateOf("white")

    val correctPiece: MutableState<ChessPiece?> = mutableStateOf(null)
    val correctSquare: MutableState<Square?> = mutableStateOf(null)
    val correctPromotionChar: MutableState<Char?> = mutableStateOf(null)

    val message = mutableStateOf("Incorrect")
    val image = mutableStateOf(R.drawable.ic_incorrect)

    fun makeComputerMove(move: String) {
        val pieceToMove: ChessPiece? = occupiedSquares[convertMoveToSquare(move, 0, 1)]
        val square: Square = convertMoveToSquare(move, 2, 3)
        if (pieceToMove != null) {
            var promotion: PromotionPiece? = null
            if(move.length == 5){
                when(playerTurn.value){
                    "white" -> {
                        when(move.last()){
                            'q' -> promotion = PromotionPieces().whitePieces["queen"]
                            'r' -> promotion = PromotionPieces().whitePieces["rook"]
                            'b' -> promotion = PromotionPieces().whitePieces["bishop"]
                            'n' -> promotion = PromotionPieces().whitePieces["knight"]
                        }
                    }
                    "black" -> {
                        when(move.last()) {
                            'q' -> promotion = PromotionPieces().blackPieces["queen"]
                            'r' -> promotion = PromotionPieces().blackPieces["rook"]
                            'b' -> promotion = PromotionPieces().blackPieces["bishop"]
                            'n' -> promotion = PromotionPieces().blackPieces["knight"]
                        }
                    }
                }
            }
            changePiecePosition(square, pieceToMove, promotion)
            moveIndex.value += 1
        }
    }

    fun setCorrectMove(correctMove: String){
        val squareFromMove = convertMoveToSquare(correctMove, 0, 1)
        val piece: ChessPiece? = occupiedSquares[squareFromMove]
        val square: Square = convertMoveToSquare(correctMove, 2, 3)
        correctPiece.value = piece
        correctSquare.value = square
        if(correctMove.length == 5){
            correctPromotionChar.value = correctMove.last()
        }
    }

    fun checkIfMoveIsCorrect(square: Square, piece: ChessPiece, promotion: PromotionPiece?) {
        if (piece == correctPiece.value && square == correctSquare.value) {
            if(promotion != null){
                val promotionChar = when(promotion.piece){
                    "queen" -> 'q'
                    "rook" -> 'r'
                    "bishop" -> 'b'
                    "knight" -> "n"
                    else -> '0'
                }
                if(promotionChar == correctPromotionChar.value){
                    message.value = "Correct!"
                    image.value = R.drawable.ic_correct
                    correct.value = true
                }else{
                    message.value = "Incorrect"
                    image.value = R.drawable.ic_incorrect
                    correct.value = false
                    endOfPuzzle.value = true
                }
            }else{
                message.value = "Correct!"
                image.value = R.drawable.ic_correct
                correct.value = true
            }
        } else {
            message.value = "Incorrect"
            image.value = R.drawable.ic_incorrect
            correct.value = false
            endOfPuzzle.value = true
        }
    }

    fun updatePlayerRating(puzzleRating: Float){
        playerRating.value = Elo().eloRating(playerRating.value.toFloat(), puzzleRating, k, correct.value)
        if(MainActivity.user != null){
            MainActivity.user!!.puzzleRating = playerRating.value
        }
        viewModelScope.launch {
            FirestoreGameInteraction().writePuzzleRating(playerRating.value)
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