package com.gomezdevlopment.chessnotationapp.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.pieces.PromotionPieces
import com.gomezdevlopment.chessnotationapp.model.repositories.GameRepository
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameRepositoryTest {
    private val gameRepository: GameRepository = GameRepository()

    private fun analyzePositions(depth: Int): Int {
        //println(gameRepository.piecesOnBoard.size)
        gameRepository.checkAllLegalMoves()
        val legalMoves = mutableListOf<Square>()
        legalMoves.addAll(gameRepository.allLegalMoves)
        var numberOfMoves = 0
        val pieces = mutableListOf<ChessPiece>()
        pieces.addAll(gameRepository.piecesOnBoard)
        val playerTurn = gameRepository.playerTurn.value

        if (depth == 1) {
            var promotions = 0
            pieces.forEach { piece ->
                if (piece.piece == "pawn") {
                    if (piece.color == playerTurn) {
                        piece.legalMoves.forEach { square ->
                            if (square.rank == 7 || square.rank == 0) {
                                promotions += 3
                            }
                        }
                    }
                }
            }
            return (legalMoves.size + promotions)
        }

        for (piece in pieces) {
            gameRepository.checkAllLegalMoves()
            val moves = mutableListOf<Square>()
            moves.addAll(piece.legalMoves)
            if (piece.color == playerTurn) {
                println("${piece.piece} : Legal Move Count - ${moves.size}")
                for (legalMove in moves) {
                    if (piece.piece == "pawn" && (legalMove.rank == 7 || legalMove.rank == 0)) {
                        for (promotion in listOf("queen", "rook", "bishop", "knight")) {
                            gameRepository.makeMove(piece, legalMove, depth, promotion)
                            numberOfMoves += analyzePositions(depth - 1)
                            gameRepository.undoMove()
                        }
                    } else {
                        gameRepository.makeMove(piece, legalMove, depth, "")
                        numberOfMoves += analyzePositions(depth - 1)
                        gameRepository.undoMove()
                    }
                }
            }
        }
        return numberOfMoves
    }

    private fun analyzePositionsV2(depth: Int): Int {
        gameRepository.checkAllLegalMoves()
        val legalMoves = mutableListOf<Square>()
        legalMoves.addAll(gameRepository.allLegalMoves)
        var numberOfMoves = 0
        val pieces = mutableListOf<ChessPiece>()
        pieces.addAll(gameRepository.piecesOnBoard)
        val playerTurn = gameRepository.playerTurn.value

        if (depth == 1) {
            var promotions = 0
//            pieces.forEach { piece ->
//                if (piece.piece == "pawn") {
//                    if (piece.color == playerTurn) {
//                        piece.legalMoves.forEach { square ->
//                            if (square.rank == 7 || square.rank == 0) {
//                                promotions += 3
//                            }
//                        }
//                    }
//                }
//            }
            return (gameRepository.allLegalMoves.size)
        }


        for (piece in pieces) {
            if (piece.color == playerTurn) {
                val originalPieceSquare = piece.square
                val originalLegalMoves = mutableListOf<Square>()
                originalLegalMoves.addAll(piece.legalMoves)
                println("${piece.piece} : Legal Move Count - ${originalLegalMoves.size}")
                for (legalMove in originalLegalMoves) {
                    if (piece.piece == "pawn" && (legalMove.rank == 7 || legalMove.rank == 0)) {
                        for (promotion in listOf("queen", "rook", "bishop", "knight")) {
                            gameRepository.makeMove(piece, legalMove, depth, promotion)
                            numberOfMoves += analyzePositions(depth - 1)
                            gameRepository.undoMove()

                        }
                    } else {
                        gameRepository.makeMove(piece, legalMove, depth, "")
                        numberOfMoves += analyzePositions(depth - 1)
                        gameRepository.undoMove()
                        piece.square = originalPieceSquare
                    }
                }
                piece.legalMoves = originalLegalMoves
            }
        }
        return numberOfMoves
    }

    fun printPieces(pieces: MutableList<ChessPiece>) {
        for (piece in pieces) {
            println("${piece.piece} ${piece.color} ${piece.square}")
        }
    }

    fun squareInAlgebraicNotation(square: Square): String {
        val string = StringBuilder()
        when (square.file) {
            0 -> string.append("a")
            1 -> string.append("b")
            2 -> string.append("c")
            3 -> string.append("d")
            4 -> string.append("e")
            5 -> string.append("f")
            6 -> string.append("g")
            7 -> string.append("h")
        }
        string.append(square.rank + 1)
        return string.toString()
    }

    @Test
    fun testDepth1() {
        gameRepository.initialPosition()
        val numberOfPositions = analyzePositions(1)
        println(numberOfPositions)
        assert(numberOfPositions == 20)
    }

    @Test
    fun testDepth2() {
        gameRepository.initialPosition()
        val numberOfPositions = analyzePositions(2)
        println(numberOfPositions)
        assert(numberOfPositions == 400)
    }

    @Test
    fun testDepth3() {
        gameRepository.initialPosition()
        val numberOfPositions = analyzePositions(3)
        println(numberOfPositions)
        assert(numberOfPositions == 8902)
    }

    @Test
    fun testDepth4() {
        gameRepository.initialPosition()
        val numberOfPositions = analyzePositions(4)
        println(numberOfPositions)
        assert(numberOfPositions == 197281)
    }

    @Test
    fun testPositionKiwiDepth1() {
        gameRepository.testPositionKiwipete()
        val numberOfPositions = analyzePositions(1)
        println(numberOfPositions)
        assert(numberOfPositions == 48)
    }

    @Test
    fun testPositionKiwiDepth2() {
        gameRepository.testPositionKiwipete()
        val numberOfPositions = analyzePositions(2)
        println(numberOfPositions)
        assert(numberOfPositions == 2039)
    }

    @Test
    fun testPositionKiwiDepth3() {
        gameRepository.testPositionKiwipete()
        val numberOfPositions = analyzePositions(3)
        println(numberOfPositions)
        assert(numberOfPositions == 97862)
    }


    @Test
    fun testPosition3Depth1() {
        gameRepository.testPosition3()
        val numberOfPositions = analyzePositions(1)
        println(numberOfPositions)
        assert(numberOfPositions == 14)
    }

    @Test
    fun testPosition3Depth2() {
        gameRepository.testPosition3()
        val numberOfPositions = analyzePositions(2)
        println("Number of Pieces on Board: ${gameRepository.piecesOnBoard.size}")
        gameRepository.piecesOnBoard.forEach {
            println(it)
        }
        println("Number of Occupied Squares on Board: ${gameRepository.occupiedSquares.size}")
        println(numberOfPositions)
        assert(numberOfPositions == 191)
    }

    @Test
    fun testPosition3Depth3() {
        gameRepository.testPosition3()
        val numberOfPositions = analyzePositions(3)
        println(numberOfPositions)
        assert(numberOfPositions == 2812)
    }

    @Test
    fun testPosition3Depth4() {
        gameRepository.testPosition3()
        val numberOfPositions = analyzePositions(4)
        println(numberOfPositions)
        assert(numberOfPositions == 43238)
    }

    @Test
    fun testPosition4Depth1() {
        gameRepository.testPosition4()
        val numberOfPositions = analyzePositions(1)
        println(numberOfPositions)
        assert(numberOfPositions == 6)
    }

    @Test
    fun testPosition4Depth2() {
        gameRepository.testPosition4()
        val numberOfPositions = analyzePositions(2)
        println(numberOfPositions)
        assert(numberOfPositions == 264)
    }

    @Test
    fun testPosition4Depth3() {
        gameRepository.testPosition4()
        val numberOfPositions = analyzePositions(3)
        println(numberOfPositions)
        println("Number of Pieces on Board: ${gameRepository.piecesOnBoard.size}")
        println("Number of Occupied Squares on Board: ${gameRepository.occupiedSquares.size}")
        println("Number of Captures: ${gameRepository.captures}")
        println("Number of Checks: ${gameRepository.checks}")
        println("Number of Castles: ${gameRepository.castles}")
        println("Number of EnPassants: ${gameRepository.enPassants}")
        println("Number of Promotions: ${gameRepository.promotions}")
        assert(numberOfPositions == 9467)
    }
}