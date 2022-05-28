package com.gomezdevlopment.chessnotationapp.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gomezdevlopment.chessnotationapp.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameRepositoryTest {
    val gameRepository: GameRepository = GameRepository.getGameRepository()

    @Test
    fun testAllMoves() {
        var numberOfMoves = 0
        for (piece in gameRepository.getPiecesOnBoard()) {
            if (piece.color == "white") {
                for (move in gameRepository.checkLegalMoves(piece, false)) {
                    val originalPosition = piece.square
                    gameRepository.changePiecePosition(move, piece)
                    for (bpiece in gameRepository.getPiecesOnBoard()) {
                        if (bpiece.color == "black") {
                            val boriginalPosition = bpiece.square
                            for (bmove in gameRepository.checkLegalMoves(bpiece, false)) {
                                gameRepository.changePiecePosition(bmove, bpiece)
                                numberOfMoves += 1
                                gameRepository.changePiecePosition(boriginalPosition, bpiece)
                            }
                        }
                    }
                    gameRepository.changePiecePosition(originalPosition, piece)
                }
            }
        }
        println(numberOfMoves)
        assert(numberOfMoves == 400)
    }

    private fun analyzePositions(depth: Int): Int {
        if (depth == 0) {
            return 1
        }
        var numberOfMoves = 0
        val pieces = mutableListOf<ChessPiece>()
        pieces.addAll(gameRepository.getPiecesOnBoard())
        for (piece in pieces) {
            if (piece.color == gameRepository.getPlayerTurn().value) {
                //println(piece.piece)
                for (legalMove in gameRepository.checkLegalMoves(piece, false)) {
                    val originalSquare = piece.square
                    val currentSquare = gameRepository.getCurrentSquare().value
                    val previousSquare = gameRepository.getPreviousSquare().value
                    val kingSquare = gameRepository.kingSquare().value
                    //println(legalMove)
                    //println("Piece ${piece.piece} from ${piece.square} to $legalMove")
//                    println(currentPlayerTurn)
//                    println(kingsSquare)
                    gameRepository.changePiecePosition(legalMove, piece)
                    numberOfMoves += analyzePositions(depth - 1)
                    gameRepository.undoChangePiecePosition(piece, originalSquare, currentSquare, previousSquare, kingSquare, legalMove)
                }
            }
        }

//        for(piece in gameRepository.getPiecesOnBoard()){
//            if(piece.color == gameRepository.getPlayerTurn().value){
//                for(move in gameRepository.checkLegalMoves(piece, false)){
//                    println("Piece ${piece.piece} from ${piece.square} to $move")
//                    val originalPosition = piece.square
//                    gameRepository.changePiecePosition(move, piece)
//                    numberOfMoves += analyzePositions(depth-1)
//                    gameRepository.undoChangePiecePosition(originalPosition, piece)
//                }
//            }
//        }
        //println("Number of Moves: $numberOfMoves")
        return numberOfMoves
    }

    @Test
    fun testDepth1() {
        val numberOfPositions = analyzePositions(1)
        println(numberOfPositions)
        assert(numberOfPositions == 20)
    }

    @Test
    fun testDepth2() {
        val numberOfPositions = analyzePositions(2)
        println(numberOfPositions)
        assert(numberOfPositions == 400)
    }

    @Test
    fun testDepth3() {
        val numberOfPositions = analyzePositions(3)
        println(numberOfPositions)
        assert(numberOfPositions == 8902)
    }

    @Test
    fun testDepth4() {
        val numberOfPositions = analyzePositions(4)
        println(numberOfPositions)
        assert(numberOfPositions == 197281)
    }
}