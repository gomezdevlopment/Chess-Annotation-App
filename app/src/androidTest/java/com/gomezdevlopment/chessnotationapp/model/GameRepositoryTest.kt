package com.gomezdevlopment.chessnotationapp.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.repositories.GameRepository
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameRepositoryTest {
    private val gameRepository: GameRepository = GameRepository()
    private val promotionPieces = mutableListOf<ChessPiece>()
    private val blackPieceImages = listOf(
        ChessPiece("black", "queen", R.drawable.ic_bq_alpha, Square(10, 10), 9, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()),
        ChessPiece("black", "rook", R.drawable.ic_br_alpha, Square(10, 10), 5, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()),
        ChessPiece("black", "bishop", R.drawable.ic_bb_alpha, Square(10, 10), 3, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()),
        ChessPiece("black", "knight", R.drawable.ic_bn_alpha, Square(10, 10), 3, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf())
    )

    private val whitePieceImages = listOf(
        ChessPiece("white", "queen", R.drawable.ic_wq_alpha, Square(10, 10), 9, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()),
        ChessPiece("white", "rook", R.drawable.ic_wr_alpha, Square(10, 10), 5, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()),
        ChessPiece("white", "bishop", R.drawable.ic_wb_alpha, Square(10, 10), 3, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()),
        ChessPiece("white", "knight", R.drawable.ic_wn_alpha, Square(10, 10), 3, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf())
    )

    private fun analyzePositions(depth: Int): Int {
        if (depth == 0) {
            return 1
        }
        var numberOfMoves = 0
        val pieces = mutableListOf<ChessPiece>()
        pieces.addAll(gameRepository.piecesOnBoard)
        val playerTurn = gameRepository.playerTurn.value
        for (piece in pieces) {
            if (piece.color == playerTurn) {
                val originalPieceSquare = piece.square
                val originalLegalMoves = mutableListOf<Square>()
                originalLegalMoves.addAll(piece.legalMoves)

                for (legalMove in originalLegalMoves) {
                    if (piece.piece == "pawn" && (legalMove.rank == 7 || legalMove.rank == 0)) {
                        promotionPieces.clear()
                        if (piece.color == "white") {
                            promotionPieces.addAll(whitePieceImages)
                        } else {
                            promotionPieces.addAll(blackPieceImages)
                        }
                        for(promotionPiece in promotionPieces){
                            promotionPiece.square = Square(legalMove.rank, legalMove.file)
                            gameRepository.movePiece(Square(legalMove.rank, legalMove.file), piece, depth)
                            gameRepository.promotion(legalMove, promotionPiece, depth)
                            numberOfMoves += analyzePositions(depth - 1)
                            gameRepository.undoMove()
                            //piece.square = originalPieceSquare
                        }
                    }else{
                        gameRepository.makeMove(piece, legalMove, depth)
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
        println("Number of Pieces on Board: ${gameRepository.piecesOnBoard.size}")
        println("Number of Occupied Squares on Board: ${gameRepository.occupiedSquares.size}")
        println("Number of Captures: ${gameRepository.captures}")
        println("Number of Checks: ${gameRepository.checks}")
        println("Number of Castles: ${gameRepository.castles}")
        println("Number of EnPassants: ${gameRepository.enPassants}")
        println("Number of Promotions: ${gameRepository.promotions}")
        assert(numberOfPositions == 400)
    }

    @Test
    fun testDepth3() {
        gameRepository.initialPosition()
        val numberOfPositions = analyzePositions(3)
        println(numberOfPositions)
        println("Number of Pieces on Board: ${gameRepository.piecesOnBoard.size}")
        println("Number of Occupied Squares on Board: ${gameRepository.occupiedSquares.size}")
        println("Number of Captures: ${gameRepository.captures}")
        println("Number of Checks: ${gameRepository.checks}")
        println("Number of Castles: ${gameRepository.castles}")
        println("Number of EnPassants: ${gameRepository.enPassants}")
        println("Number of Promotions: ${gameRepository.promotions}")
        assert(numberOfPositions == 8902)
    }

    @Test
    fun testDepth4() {
        gameRepository.initialPosition()
        val numberOfPositions = analyzePositions(4)
        println(numberOfPositions)
        println("Number of Pieces on Board: ${gameRepository.piecesOnBoard.size}")
        println("Number of Occupied Squares on Board: ${gameRepository.occupiedSquares.size}")
        println("Number of Captures: ${gameRepository.captures}")
        println("Number of Checks: ${gameRepository.checks}")
        println("Number of Castles: ${gameRepository.castles}")
        println("Number of EnPassants: ${gameRepository.enPassants}")
        println("Number of Promotions: ${gameRepository.promotions}")
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
        println("Number of Pieces on Board: ${gameRepository.piecesOnBoard.size}")
        println("Number of Occupied Squares on Board: ${gameRepository.occupiedSquares.size}")
        println("Number of Captures: ${gameRepository.captures}")
        println("Number of Checks: ${gameRepository.checks}")
        println("Number of Castles: ${gameRepository.castles}")
        println("Number of EnPassants: ${gameRepository.enPassants}")
        println("Number of Promotions: ${gameRepository.promotions}")
        assert(numberOfPositions == 2039)
//        assert(gameRepository.captures.value == 351)
//        assert(gameRepository.checks.value == 3)
//        assert(gameRepository.castles.value == 91)
//        assert(gameRepository.enPassants.value == 1)
    }

    @Test
    fun testPositionKiwiDepth3() {
        gameRepository.testPositionKiwipete()
        val numberOfPositions = analyzePositions(3)
        println(numberOfPositions)
        println("Number of Pieces on Board: ${gameRepository.piecesOnBoard.size}")
        println("Number of Occupied Squares on Board: ${gameRepository.occupiedSquares.size}")
        println("Number of Captures: ${gameRepository.captures}")
        println("Number of Checks: ${gameRepository.checks}")
        println("Number of Castles: ${gameRepository.castles}")
        println("Number of EnPassants: ${gameRepository.enPassants}")
        println("Number of Promotions: ${gameRepository.promotions}")
        assert(numberOfPositions == 97862)
//        assert(gameRepository.getCaptures() == 17102)
//        assert(gameRepository.getChecks() == 993)
//        assert(gameRepository.getCastles() == 3162)
//        assert(gameRepository.getEnPassants() == 45)
    }


    @Test
    fun testPosition3Depth1() {
        gameRepository.testPosition3()
        val numberOfPositions = analyzePositions(1)
        println(numberOfPositions)
        println("Number of Pieces on Board: ${gameRepository.piecesOnBoard.size}")
        println("Number of Occupied Squares on Board: ${gameRepository.occupiedSquares.size}")
        println("Number of Captures: ${gameRepository.captures}")
        println("Number of Checks: ${gameRepository.checks}")
        println("Number of Castles: ${gameRepository.castles}")
        println("Number of EnPassants: ${gameRepository.enPassants}")
        println("Number of Promotions: ${gameRepository.promotions}")
        assert(numberOfPositions == 14)
//        assert(gameRepository.getCaptures() == 1)
//        assert(gameRepository.getChecks() == 2)
//        assert(gameRepository.getCastles() == 0)
//        assert(gameRepository.getEnPassants() == 0)
    }

    @Test
    fun testPosition3Depth2() {
        gameRepository.testPosition3()
        val numberOfPositions = analyzePositions(2)
        println(numberOfPositions)
        println("Number of Pieces on Board: ${gameRepository.piecesOnBoard.size}")
        println("Number of Occupied Squares on Board: ${gameRepository.occupiedSquares.size}")
        println("Number of Captures: ${gameRepository.captures}")
        println("Number of Checks: ${gameRepository.checks}")
        println("Number of Castles: ${gameRepository.castles}")
        println("Number of EnPassants: ${gameRepository.enPassants}")
        println("Number of Promotions: ${gameRepository.promotions}")
        assert(numberOfPositions == 191)
//        assert(gameRepository.getCaptures() == 14)
//        assert(gameRepository.getChecks() == 10)
//        assert(gameRepository.getCastles() == 0)
//        assert(gameRepository.getEnPassants() == 0)
    }

    @Test
    fun testPosition3Depth3() {
        gameRepository.testPosition3()
        val numberOfPositions = analyzePositions(3)
        println(numberOfPositions)
        println("Number of Pieces on Board: ${gameRepository.piecesOnBoard.size}")
        println("Number of Occupied Squares on Board: ${gameRepository.occupiedSquares.size}")
        println("Number of Captures: ${gameRepository.captures}")
        println("Number of Checks: ${gameRepository.checks}")
        println("Number of Castles: ${gameRepository.castles}")
        println("Number of EnPassants: ${gameRepository.enPassants}")
        println("Number of Promotions: ${gameRepository.promotions}")
        assert(numberOfPositions == 2812)
//        assert(gameRepository.getCaptures() == 209)
//        assert(gameRepository.getChecks() == 267)
//        assert(gameRepository.getCastles() == 0)
//        assert(gameRepository.getEnPassants() == 2)
    }

    @Test
    fun testPosition3Depth4() {
        gameRepository.testPosition3()
        val numberOfPositions = analyzePositions(4)
        println(numberOfPositions)
        println("Number of Pieces on Board: ${gameRepository.piecesOnBoard.size}")
        println("Number of Occupied Squares on Board: ${gameRepository.occupiedSquares.size}")
        println("Number of Captures: ${gameRepository.captures}")
        println("Number of Checks: ${gameRepository.checks}")
        println("Number of Castles: ${gameRepository.castles}")
        println("Number of EnPassants: ${gameRepository.enPassants}")
        println("Number of Promotions: ${gameRepository.promotions}")
        assert(numberOfPositions == 43238)
//        assert(gameRepository.getCaptures() == 3348)
//        assert(gameRepository.getChecks() == 1680)
//        assert(gameRepository.getCastles() == 0)
//        assert(gameRepository.getEnPassants() == 123)
    }

    @Test
    fun testPosition4Depth1() {
        gameRepository.testPosition4()
        val numberOfPositions = analyzePositions(1)
        println(numberOfPositions)
        println("Number of Pieces on Board: ${gameRepository.piecesOnBoard.size}")
        println("Number of Occupied Squares on Board: ${gameRepository.occupiedSquares.size}")
        println("Number of Captures: ${gameRepository.captures}")
        println("Number of Checks: ${gameRepository.checks}")
        println("Number of Castles: ${gameRepository.castles}")
        println("Number of EnPassants: ${gameRepository.enPassants}")
        println("Number of Promotions: ${gameRepository.promotions}")
        assert(numberOfPositions == 6)
//        assert(gameRepository.getCaptures() == 0)
//        assert(gameRepository.getChecks() == 0)
//        assert(gameRepository.getCastles() == 0)
//        assert(gameRepository.getEnPassants() == 0)
    }

    @Test
    fun testPosition4Depth2() {
        gameRepository.testPosition4()
        val numberOfPositions = analyzePositions(2)
        println(numberOfPositions)
        println("Number of Pieces on Board: ${gameRepository.piecesOnBoard.size}")
        println("Number of Occupied Squares on Board: ${gameRepository.occupiedSquares.size}")
        println("Number of Captures: ${gameRepository.captures}")
        println("Number of Checks: ${gameRepository.checks}")
        println("Number of Castles: ${gameRepository.castles}")
        println("Number of EnPassants: ${gameRepository.enPassants}")
        println("Number of Promotions: ${gameRepository.promotions}")
        assert(numberOfPositions == 264)
        assert(gameRepository.captures.value == 87)
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