package com.gomezdevlopment.chessnotationapp.model.game_logic

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.GameState
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.pieces.Piece
import com.gomezdevlopment.chessnotationapp.model.pieces.PromotionPiece

interface GameSetup {
    var piecesOnBoard: MutableList<ChessPiece>
    var capturedPieces: MutableList<ChessPiece>
    var occupiedSquares: MutableMap<Square, ChessPiece>
    var previousSquare: MutableState<Square>
    var currentSquare: MutableState<Square>
    var playerTurn: MutableState<String>
    var whiteKingSquare: MutableState<Square>
    var blackKingSquare: MutableState<Square>
    var xRayAttacks: MutableList<Square>
    var attacks: MutableList<Square>
    var pinnedPieces: MutableList<ChessPiece>
    val pinnedPiecesFromPreviousTurns: MutableList<List<ChessPiece>>
    var allLegalMoves: MutableList<Square>
    var whiteCanCastleKingSide: MutableState<Boolean>
    var whiteCanCastleQueenSide: MutableState<Boolean>
    var blackCanCastleKingSide: MutableState<Boolean>
    var blackCanCastleQueenSide: MutableState<Boolean>
    var kingInCheck: MutableState<Boolean>
    var piecesCheckingKing: MutableList<ChessPiece>
    var checksOnKing: MutableList<Square>
    var previousGameStates: MutableList<GameState>
    var annotations: MutableList<String>
    var currentNotation: StringBuilder

    //Sound FX Booleans
    var pieceSound: MutableState<Boolean>
    var checkSound: MutableState<Boolean>
    var captureSound: MutableState<Boolean>
    var castlingSound: MutableState<Boolean>

    var selectedNotationIndex: MutableState<Int>
    val openDrawOfferedDialog: MutableState<Boolean>


    fun setPositionFromFen(fen: String) {
        occupiedSquares.clear()
        piecesOnBoard.clear()
        val piecesOnBoardFromFENPosition = FEN().parseFEN(
            fen,
            playerTurn,
            whiteCanCastleKingSide,
            whiteCanCastleQueenSide,
            blackCanCastleKingSide,
            blackCanCastleQueenSide,
            whiteKingSquare,
            blackKingSquare,
        )

        piecesOnBoard.addAll(piecesOnBoardFromFENPosition)
        piecesOnBoard.forEach {
            occupiedSquares[it.square] = it
        }
        checkAllLegalMoves()
    }

    fun getGameStateAsFEN(): String {
        return FEN().getGameStateAsFEN(
            occupiedSquares,
            playerTurn,
            whiteCanCastleKingSide,
            whiteCanCastleQueenSide,
            blackCanCastleKingSide,
            blackCanCastleQueenSide
        )
    }

    fun addInitialGameState() {
        previousGameStates.clear()
        previousGameStates.add(
            GameState(
                previousSquare.value,
                currentSquare.value,
                getGameStateAsFEN()
            )
        )
    }

    fun kingSquare(): MutableState<Square> {
        if (playerTurn.value == "white") {
            return whiteKingSquare
        }
        return blackKingSquare
    }

    fun getEnemyKingSquare(): Square {
        if (playerTurn.value == "white") {
            return blackKingSquare.value
        }
        return whiteKingSquare.value
    }

    fun addXRayAttacks(
        list: MutableList<Square>
    ) {
        list.clear()
        piecesOnBoard.forEach() { piece ->
            if (piece.color != playerTurn.value)
                xRayAttacks.addAll(piece.xRays)
        }
    }

    fun castleKingSide(): MutableState<Boolean> {
        if (playerTurn.value == "white") {
            return whiteCanCastleKingSide
        }
        return blackCanCastleKingSide
    }

    fun castleQueenSide(): MutableState<Boolean> {
        if (playerTurn.value == "white") {
            return whiteCanCastleQueenSide
        }
        return blackCanCastleQueenSide
    }

    fun setGameState(index: Int) {
        setPositionFromFen(previousGameStates[index].fenPosition)
        currentSquare.value = previousGameStates[index].currentSquare
        previousSquare.value = previousGameStates[index].previousSquare
    }

    fun checkAllLegalMoves() {
        pinnedPiecesFromPreviousTurns.add(pinnedPieces.toList())
        allLegalMoves.clear()
        piecesCheckingKing.clear()
        attacks.clear()
        pinnedPieces.clear()
        piecesOnBoard.forEach { piece ->
            kingInCheck.value = false
            if (piece.color != playerTurn.value) {
                checkLegalMoves(piece, kingSquare().value)
                attacks.addAll(piece.attacks)
                if (piece.legalMoves.contains(kingSquare().value)) {
                    if (piece.piece == "pawn" || piece.piece == "knight") {
                        piecesCheckingKing.add(piece)
                    }
                    if (kingInCheck.value) {
                        piecesCheckingKing.add(piece)
                    }
                }
            }
        }
        addXRayAttacks(xRayAttacks)
        kingInCheck.value = piecesCheckingKing.isNotEmpty()
        piecesOnBoard.forEach { piece ->
            if (piece.color == playerTurn.value) {
                checkLegalMoves(piece, getEnemyKingSquare())
                allLegalMoves.addAll(piece.legalMoves)
            }
        }
    }

    fun changePieceSquare(piece: ChessPiece, newSquare: Square): ChessPiece {
        piece.square = newSquare
        return piece
    }

    fun removePiece(square: Square) {
        piecesOnBoard.remove(occupiedSquares.remove(square))
    }

    fun checkIfKingOrRooksMoved(piece: ChessPiece) {
        if (piece.color == "white") {
            if (whiteCanCastleQueenSide.value || whiteCanCastleKingSide.value) {
                when (piece.piece) {
                    "king" -> {
                        whiteCanCastleQueenSide.value = false
                        whiteCanCastleKingSide.value = false
                    }
                    "rook" -> {
                        when (piece.square) {
                            Square(0, 0) -> whiteCanCastleQueenSide.value = false
                            Square(0, 7) -> whiteCanCastleKingSide.value = false
                        }
                    }
                }
            }

        } else {
            if (blackCanCastleQueenSide.value || blackCanCastleQueenSide.value) {
                when (piece.piece) {
                    "king" -> {
                        blackCanCastleQueenSide.value = false
                        blackCanCastleKingSide.value = false
                    }
                    "rook" -> {
                        when (piece.square) {
                            Square(7, 0) -> blackCanCastleQueenSide.value = false
                            Square(7, 7) -> blackCanCastleKingSide.value = false
                        }
                    }
                }
            }
        }
    }

    fun checkIfCastled(
        piece: ChessPiece,
        newSquare: Square,
        depth: Int
    ): String {
        //Castling
        if (piece.piece == "king") {
            if (castleKingSide().value) {
                if (newSquare.file == piece.square.file + 2) {
                    val rook: ChessPiece = occupiedSquares[Square(newSquare.rank, newSquare.file + 1)]!!
                    occupiedSquares.remove(Square(newSquare.rank, newSquare.file + 1))
                    changePieceSquare(rook, Square(newSquare.rank, newSquare.file - 1))
                    occupiedSquares[rook.square] = rook
                    //castlingSound.value = true
                    return ("0-0")
                }
            }
            if (castleQueenSide().value) {
                if (newSquare.file == piece.square.file - 2) {
                    val rook: ChessPiece =
                        occupiedSquares[Square(newSquare.rank, newSquare.file - 2)]!!
                    occupiedSquares.remove(Square(newSquare.rank, newSquare.file - 2))
                    changePieceSquare(rook, Square(newSquare.rank, newSquare.file + 1))
                    occupiedSquares[rook.square] = rook
                    //castlingSound.value = true
                    return ("0-0-0")
                }
            }
        }
        checkIfKingOrRooksMoved(piece)
        return ""
    }

    fun changePiecePosition(newSquare: Square, piece: ChessPiece, promotion: PromotionPiece?) {
        val previousPieceSquare = piece.square
        val notation = Notation(currentNotation, newSquare)
        val castled = mutableStateOf(false)
        val capture = mutableStateOf(false)

        //Castling
        when (checkIfCastled(piece, newSquare, 0)) {
            "0-0-0" -> {
                castled.value = true
                notation.castleQueenSide()
            }
            "0-0" -> {
                castled.value = true
                notation.castleKingSide()
            }
        }

        if (!castled.value) {
            notation.piece(piece, piecesOnBoard)
        }

        //Remove Defender
        if (occupiedSquares.containsKey(newSquare)) {
            occupiedSquares[newSquare]?.let {
                capturedPieces.add(it)
            }
            removePiece(newSquare)
            capture.value = true
            notation.capture()
        } else if (isEnPassant(piece, newSquare)) {
            occupiedSquares[currentSquare.value]?.let { capturedPieces.add(it) }
            removePiece(currentSquare.value)
            capture.value = true
            notation.capture()
        } else {
            if (!castled.value) {
                notation.square(false)
            }
        }

        occupiedSquares.remove(previousPieceSquare)
        changePieceSquare(piece, newSquare)

        if (promotion != null) {
            piece.piece = promotion.piece
            piece.pieceDrawable = promotion.pieceDrawable
            piece.value = promotion.value
            notation.promotion(piece)
        }

        occupiedSquares[newSquare] = piece
        previousSquare.value = previousPieceSquare
        currentSquare.value = newSquare
        if (piece.color == "white") {
            if (piece.piece == "king") {
                whiteKingSquare.value = newSquare
            }
            playerTurn.value = "black"
        } else {
            if (piece.piece == "king") {
                blackKingSquare.value = newSquare
            }
            playerTurn.value = "white"
        }
        previousGameStates.add(
            GameState(
                previousSquare.value,
                currentSquare.value,
                getGameStateAsFEN()
            )
        )
        checkAllLegalMoves()

        if (kingInCheck.value) {
            checkSound.value = true
        } else {
            if (castled.value) {
                castlingSound.value = true
            } else if (capture.value) {
                captureSound.value = true
            } else {
                pieceSound.value = true
            }
        }
    }

    private fun isEnPassant(piece: ChessPiece, square: Square): Boolean {
        //If pawn moves to an empty diagonal square it should be en passant
        if (piece.square.file != square.file && piece.piece == "pawn") {
            if (!occupiedSquares.containsKey(square)) {
                return true
            }
        }
        return false
    }

    fun checkLegalMoves(piece: ChessPiece, kingSquare: Square): List<Square> {
        return Piece(
            piece,
            occupiedSquares,
            attacks,
            castleKingSide().value,
            castleQueenSide().value,
            kingInCheck,
            kingSquare,
            piecesCheckingKing,
            previousSquare.value,
            currentSquare.value,
            pinnedPieces,
            pinnedPiecesFromPreviousTurns.last()
        ).moves()
    }
}