package com.gomezdevlopment.chessnotationapp.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gomezdevlopment.chessnotationapp.R
import org.junit.Test
import org.junit.runner.RunWith

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
        for (piece in pieces) {
            if (piece.color == gameRepository.getPlayerTurn().value) {
                for (legalMove in gameRepository.checkLegalMoves(piece, false)) {
                    val originalSquare = piece.square
                    val currentSquare = gameRepository.getCurrentSquare().value
                    val previousSquare = gameRepository.getPreviousSquare().value
                    val kingSquare = gameRepository.kingSquare().value
                    gameRepository.changePiecePosition(legalMove, piece)
                    numberOfMoves += analyzePositions(depth - 1)
                    gameRepository.undoChangePiecePosition(piece, originalSquare, currentSquare, previousSquare, kingSquare, legalMove)
                }
            }
        }
        return numberOfMoves
    }

    @Test
    fun testDepth1() {
        gameRepository.testPositionKiwipete()
        val numberOfPositions = analyzePositions(1)
        println(numberOfPositions)
        assert(numberOfPositions == 20)
    }

    @Test
    fun testDepth2() {
        gameRepository.testPositionKiwipete()
        val numberOfPositions = analyzePositions(2)
        println(numberOfPositions)
        println("Number of Pieces on Board: ${gameRepository.getPiecesOnBoard().size}")
        println("Number of Occupied Squares on Board: ${gameRepository.getHashMap().size}")
        assert(numberOfPositions == 400)
    }

    @Test
    fun testDepth3() {
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
        val numberOfPositions = analyzePositions(4)
        println(numberOfPositions)
        println("Number of Pieces on Board: ${gameRepository.getPiecesOnBoard().size}")
        println("Number of Occupied Squares on Board: ${gameRepository.getHashMap().size}")
        println("Number of Captures: ${gameRepository.getCaptures()}")
        println("Number of Checks: ${gameRepository.getChecks()}")
        assert(numberOfPositions == 197281)
    }

    @Test
    fun testDepth5() {
        val numberOfPositions = analyzePositions(5)
        println(numberOfPositions)
        println("Number of Pieces on Board: ${gameRepository.getPiecesOnBoard().size}")
        println("Number of Occupied Squares on Board: ${gameRepository.getHashMap().size}")
        println("Number of Captures: ${gameRepository.getCaptures()}")
        println("Number of Checks: ${gameRepository.getChecks()}")
        assert(numberOfPositions == 4865609)
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
        assert(numberOfPositions == 2039)
        assert(gameRepository.getCaptures() == 351)
        assert(gameRepository.getChecks() == 3)
    }
}