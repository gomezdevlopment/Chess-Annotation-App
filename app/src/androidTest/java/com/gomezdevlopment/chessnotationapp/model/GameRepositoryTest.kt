package com.gomezdevlopment.chessnotationapp.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gomezdevlopment.chessnotationapp.R
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class GameRepositoryTest {
    val gameRepository: GameRepository = GameRepository.getGameRepository()

    private fun analyzePositions(depth: Int): Int {
        if (depth == 0) {
            return 1
        }
        var numberOfMoves = 0
        val pieces = mutableListOf<ChessPiece>()
        pieces.addAll(gameRepository.getPiecesOnBoard())
        val playerTurn = gameRepository.getPlayerTurn().value
        for (piece in pieces) {
            if (piece.color == playerTurn) {
                val originalPieceSquare = piece.square
                for (legalMove in gameRepository.checkLegalMoves(piece, false)) {
                    if(depth == 1){
                        println("${piece.piece} ${squareInAlgebraicNotation(legalMove)}")
                    }
                    //val originalSquare = piece.square
//                    val kingSquare = gameRepository.kingSquare().value
//                    val castleKingSide = gameRepository.castleKingSide()
//                    val castleQueenSide = gameRepository.castleQueenSide()
                    //println("${piece.piece} from ${piece.square} to $legalMove")
                    gameRepository.changePiecePosition(legalMove, piece, depth)
                    numberOfMoves += analyzePositions(depth - 1)
                    gameRepository.previousGameState()
                    piece.square = originalPieceSquare
                    //gameRepository.undoChangePiecePosition(piece, originalSquare, currentSquare, previousSquare, kingSquare, legalMove, castleKingSide, castleQueenSide)
                }
            }
        }
        return numberOfMoves
    }

    fun printPieces(pieces: MutableList<ChessPiece>){
        for(piece in pieces){
            println("${piece.piece} ${piece.color} ${piece.square}")
        }
    }
    fun squareInAlgebraicNotation(square: Square): String {
        val string = StringBuilder()
        when(square.file){
            0->string.append("a")
            1->string.append("b")
            2->string.append("c")
            3->string.append("d")
            4->string.append("e")
            5->string.append("f")
            6->string.append("g")
            7->string.append("h")
        }
        string.append(square.rank+1)
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
        println("Number of Pieces on Board: ${gameRepository.getPiecesOnBoard().size}")
        println("Number of Occupied Squares on Board: ${gameRepository.getHashMap().size}")
        assert(numberOfPositions == 400)
    }

    @Test
    fun testDepth3() {
        gameRepository.initialPosition()
        val numberOfPositions = analyzePositions(3)
        println(numberOfPositions)
        println("Number of Pieces on Board: ${gameRepository.getPiecesOnBoard().size}")
        println("Number of Occupied Squares on Board: ${gameRepository.getHashMap().size}")
        println("Number of Captures: ${gameRepository.getCaptures()}")
        println("Number of Checks: ${gameRepository.getChecks()}")
        assert(numberOfPositions == 8902)
    }

    @Test
    fun testDepth4() {
        gameRepository.initialPosition()
        val numberOfPositions = analyzePositions(4)
        println(numberOfPositions)
        println("Number of Pieces on Board: ${gameRepository.getPiecesOnBoard().size}")
        println("Number of Occupied Squares on Board: ${gameRepository.getHashMap().size}")
        println("Number of Captures: ${gameRepository.getCaptures()}")
        println("Number of Checks: ${gameRepository.getChecks()}")
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
        println("Number of Pieces on Board: ${gameRepository.getPiecesOnBoard().size}")
        println("Number of Occupied Squares on Board: ${gameRepository.getHashMap().size}")
        println("Number of Captures: ${gameRepository.getCaptures()}")
        println("Number of Checks: ${gameRepository.getChecks()}")
        println("Number of Castles: ${gameRepository.getCastles()}")
        println("Number of EnPassants: ${gameRepository.getEnPassants()}")
        assert(numberOfPositions == 2039)
        assert(gameRepository.getCaptures() == 351)
        assert(gameRepository.getChecks() == 3)
        assert(gameRepository.getCastles() == 91)
        assert(gameRepository.getEnPassants() == 1)
    }

    @Test
    fun testPositionKiwiDepth3() {
        gameRepository.testPositionKiwipete()
        val numberOfPositions = analyzePositions(3)
        println(numberOfPositions)
        println("Number of Pieces on Board: ${gameRepository.getPiecesOnBoard().size}")
        println("Number of Occupied Squares on Board: ${gameRepository.getHashMap().size}")
        println("Number of Captures: ${gameRepository.getCaptures()}")
        println("Number of Checks: ${gameRepository.getChecks()}")
        println("Number of Castles: ${gameRepository.getCastles()}")
        println("Number of EnPassants: ${gameRepository.getEnPassants()}")
        assert(numberOfPositions == 97862)
        assert(gameRepository.getCaptures() == 17102)
        assert(gameRepository.getChecks() == 993)
        assert(gameRepository.getCastles() == 3162)
        assert(gameRepository.getEnPassants() == 45)
    }


    @Test
    fun testPosition3Depth1() {
        gameRepository.testPosition3()
        val numberOfPositions = analyzePositions(1)
        println(numberOfPositions)
        println("Number of Pieces on Board: ${gameRepository.getPiecesOnBoard().size}")
        println("Number of Occupied Squares on Board: ${gameRepository.getHashMap().size}")
        println("Number of Captures: ${gameRepository.getCaptures()}")
        println("Number of Checks: ${gameRepository.getChecks()}")
        println("Number of Castles: ${gameRepository.getCastles()}")
        println("Number of EnPassants: ${gameRepository.getEnPassants()}")
        assert(numberOfPositions == 14)
        assert(gameRepository.getCaptures() == 1)
        assert(gameRepository.getChecks() == 2)
        assert(gameRepository.getCastles() == 0)
        assert(gameRepository.getEnPassants() == 0)
    }

    @Test
    fun testPosition3Depth2() {
        gameRepository.testPosition3()
        val numberOfPositions = analyzePositions(2)
        println(numberOfPositions)
        println("Number of Pieces on Board: ${gameRepository.getPiecesOnBoard().size}")
        println("Number of Occupied Squares on Board: ${gameRepository.getHashMap().size}")
        println("Number of Captures: ${gameRepository.getCaptures()}")
        println("Number of Checks: ${gameRepository.getChecks()}")
        println("Number of Castles: ${gameRepository.getCastles()}")
        println("Number of EnPassants: ${gameRepository.getEnPassants()}")
        assert(numberOfPositions == 191)
        assert(gameRepository.getCaptures() == 14)
        assert(gameRepository.getChecks() == 10)
        assert(gameRepository.getCastles() == 0)
        assert(gameRepository.getEnPassants() == 0)
    }

    @Test
    fun testPosition3Depth3() {
        gameRepository.testPosition3()
        val numberOfPositions = analyzePositions(3)
        println(numberOfPositions)
        println("Number of Captures: ${gameRepository.getCaptures()}")
        println("Number of Checks: ${gameRepository.getChecks()}")
        println("Number of Castles: ${gameRepository.getCastles()}")
        println("Number of EnPassants: ${gameRepository.getEnPassants()}")
        assert(numberOfPositions == 2812)
        assert(gameRepository.getCaptures() == 209)
        assert(gameRepository.getChecks() == 267)
        assert(gameRepository.getCastles() == 0)
        assert(gameRepository.getEnPassants() == 2)
    }

    @Test
    fun testPosition3Depth4() {
        gameRepository.testPosition3()
        val numberOfPositions = analyzePositions(4)
        println(numberOfPositions)
        println("Number of Pieces on Board: ${gameRepository.getPiecesOnBoard().size}")
        println("Number of Occupied Squares on Board: ${gameRepository.getHashMap().size}")
        println("Number of Captures: ${gameRepository.getCaptures()}")
        println("Number of Checks: ${gameRepository.getChecks()}")
        println("Number of Castles: ${gameRepository.getCastles()}")
        println("Number of EnPassants: ${gameRepository.getEnPassants()}")
        assert(numberOfPositions == 43238)
        assert(gameRepository.getCaptures() == 3348)
        assert(gameRepository.getChecks() == 1680)
        assert(gameRepository.getCastles() == 0)
        assert(gameRepository.getEnPassants() == 123)
    }
}