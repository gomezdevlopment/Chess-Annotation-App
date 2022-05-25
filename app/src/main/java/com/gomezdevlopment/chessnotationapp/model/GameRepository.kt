package com.gomezdevlopment.chessnotationapp.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.os.persistableBundleOf
import androidx.lifecycle.ViewModel
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameLogic
import com.gomezdevlopment.chessnotationapp.model.pieces.*


class GameRepository : ViewModel() {
    private val piecesOnBoard: MutableList<ChessPiece> = mutableListOf()
    private val hashMap: MutableMap<Square, ChessPiece> = HashMap()
    private var previousSquare: MutableState<Square> = mutableStateOf(Square(10, 10))
    private var currentSquare: MutableState<Square> = mutableStateOf(Square(10, 10))
    private var playerTurn: MutableState<String> = mutableStateOf("white")
    private var whiteAttacks: MutableList<Square> = mutableListOf()
    private var blackAttacks: MutableList<Square> = mutableListOf()
    private var squaresToBlock: MutableList<Square> = mutableListOf()
    private var whiteKingSquare: MutableState<Square> = mutableStateOf(Square(0, 4))
    private var blackKingSquare: MutableState<Square> = mutableStateOf(Square(7, 4))

    private var whiteKingMoved: MutableState<Boolean> = mutableStateOf(false)
    private var blackKingMoved: MutableState<Boolean> = mutableStateOf(false)
    private var whiteKingSideRookMoved: MutableState<Boolean> = mutableStateOf(false)
    private var whiteQueenSideRookMoved: MutableState<Boolean> = mutableStateOf(false)
    private var blackKingSideRookMoved: MutableState<Boolean> = mutableStateOf(false)
    private var blackQueenSideRookMoved: MutableState<Boolean> = mutableStateOf(false)

    private var kingInCheck: MutableState<Boolean> = mutableStateOf(false)

    companion object {
        @Volatile
        private var INSTANCE: GameRepository? = null

        fun getGameRepository(): GameRepository {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = GameRepository()
                INSTANCE = instance
                return instance
            }
        }
    }

    init {
        addPieces()
        checkWhiteAttacks()
        checkBlackAttacks()
    }

    fun resetGame() {
        piecesOnBoard.clear()
        hashMap.clear()
        addPieces()
        playerTurn.value = "white"
        squaresToBlock.clear()
        whiteKingSquare.value = Square(0, 4)
        blackKingSquare.value = Square(7, 3)
        previousSquare.value = Square(10, 10)
        currentSquare.value = Square(10, 10)
        checkWhiteAttacks()
        checkBlackAttacks()
    }

    private fun addPieces() {
        piecesOnBoard.add(ChessPiece("white", "rook", R.drawable.ic_wr_alpha, Square(0, 0)))
        piecesOnBoard.add(ChessPiece("white", "rook", R.drawable.ic_wr_alpha, Square(0, 7)))
        piecesOnBoard.add(ChessPiece("white", "knight", R.drawable.ic_wn_alpha, Square(0, 1)))
        piecesOnBoard.add(ChessPiece("white", "knight", R.drawable.ic_wn_alpha, Square(0, 6)))
        piecesOnBoard.add(ChessPiece("white", "bishop", R.drawable.ic_wb_alpha, Square(0, 2)))
        piecesOnBoard.add(ChessPiece("white", "bishop", R.drawable.ic_wb_alpha, Square(0, 5)))
        piecesOnBoard.add(ChessPiece("white", "queen", R.drawable.ic_wq_alpha, Square(0, 3)))
        piecesOnBoard.add(ChessPiece("white", "king", R.drawable.ic_wk, Square(0, 4)))
        piecesOnBoard.add(ChessPiece("white", "pawn", R.drawable.ic_wp_alpha, Square(1, 0)))
        piecesOnBoard.add(ChessPiece("white", "pawn", R.drawable.ic_wp_alpha, Square(1, 1)))
        piecesOnBoard.add(ChessPiece("white", "pawn", R.drawable.ic_wp_alpha, Square(1, 2)))
        piecesOnBoard.add(ChessPiece("white", "pawn", R.drawable.ic_wp_alpha, Square(1, 3)))
        piecesOnBoard.add(ChessPiece("white", "pawn", R.drawable.ic_wp_alpha, Square(1, 4)))
        piecesOnBoard.add(ChessPiece("white", "pawn", R.drawable.ic_wp_alpha, Square(1, 5)))
        piecesOnBoard.add(ChessPiece("white", "pawn", R.drawable.ic_wp_alpha, Square(1, 6)))
        piecesOnBoard.add(ChessPiece("white", "pawn", R.drawable.ic_wp_alpha, Square(1, 7)))

        piecesOnBoard.add(ChessPiece("black", "rook", R.drawable.ic_br_alpha, Square(7, 0)))
        piecesOnBoard.add(ChessPiece("black", "rook", R.drawable.ic_br_alpha, Square(7, 7)))
        piecesOnBoard.add(ChessPiece("black", "knight", R.drawable.ic_bn_alpha, Square(7, 1)))
        piecesOnBoard.add(ChessPiece("black", "knight", R.drawable.ic_bn_alpha, Square(7, 6)))
        piecesOnBoard.add(ChessPiece("black", "bishop", R.drawable.ic_bb_alpha, Square(7, 2)))
        piecesOnBoard.add(ChessPiece("black", "bishop", R.drawable.ic_bb_alpha, Square(7, 5)))
        piecesOnBoard.add(ChessPiece("black", "queen", R.drawable.ic_bq_alpha, Square(7, 3)))
        piecesOnBoard.add(ChessPiece("black", "king", R.drawable.ic_bk_alpha, Square(7, 4)))
        piecesOnBoard.add(ChessPiece("black", "pawn", R.drawable.ic_bp_alpha, Square(6, 0)))
        piecesOnBoard.add(ChessPiece("black", "pawn", R.drawable.ic_bp_alpha, Square(6, 1)))
        piecesOnBoard.add(ChessPiece("black", "pawn", R.drawable.ic_bp_alpha, Square(6, 2)))
        piecesOnBoard.add(ChessPiece("black", "pawn", R.drawable.ic_bp_alpha, Square(6, 3)))
        piecesOnBoard.add(ChessPiece("black", "pawn", R.drawable.ic_bp_alpha, Square(6, 4)))
        piecesOnBoard.add(ChessPiece("black", "pawn", R.drawable.ic_bp_alpha, Square(6, 5)))
        piecesOnBoard.add(ChessPiece("black", "pawn", R.drawable.ic_bp_alpha, Square(6, 6)))
        piecesOnBoard.add(ChessPiece("black", "pawn", R.drawable.ic_bp_alpha, Square(6, 7)))

        for (piece in piecesOnBoard) {
            hashMap[piece.square] = piece
        }
    }


    private fun checkWhiteAttacks() {
        whiteAttacks.clear()
        for (piece in piecesOnBoard) {
            if (piece.color == "white") {
                if (piece.piece == "pawn") {
                    whiteAttacks.addAll(Pawn().pawnAttacks(piece))
                } else {
                    for (square in checkLegalMoves(piece, true)) {
                        whiteAttacks.add(square)
                    }
                }
            }
        }
    }

    private fun checkBlackAttacks() {
        blackAttacks.clear()
        for (piece in piecesOnBoard) {
            if (piece.color == "black") {
                if (piece.piece == "pawn") {
                    blackAttacks.addAll(Pawn().pawnAttacks(piece))
                } else {
                    for (square in checkLegalMoves(piece, true)) {
                        blackAttacks.add(square)
                    }
                }

            }
        }
    }

    private fun checkIfKingOrRooksMoved(piece: ChessPiece) {
        if (piece.color == "white") {
            if (!whiteKingMoved.value) {
                when (piece.piece) {
                    "king" -> whiteKingMoved.value = true
                    "rook" -> {
                        when (piece.square) {
                            Square(0, 0) -> whiteQueenSideRookMoved.value = true
                            Square(0, 7) -> whiteKingSideRookMoved.value = true
                        }
                    }
                }
            }

        } else {
            if (!blackKingMoved.value) {
                when (piece.piece) {
                    "king" -> blackKingMoved.value = true
                    "rook" -> {
                        when (piece.square) {
                            Square(7, 0) -> blackQueenSideRookMoved.value = true
                            Square(7, 7) -> blackKingSideRookMoved.value = true
                        }
                    }
                }
            }
        }
    }

    fun changePiecePosition(newSquare: Square, piece: ChessPiece) {
        val gameLogic = GameLogic()

        //Castling
        if (piece.piece == "king") {
            if (newSquare.file == piece.square.file + 2) {
                val rook: ChessPiece = hashMap[Square(newSquare.rank, newSquare.file + 1)]!!
                hashMap.remove(Square(newSquare.rank, newSquare.file + 1))
                rook.square = Square(newSquare.rank, newSquare.file - 1)
                hashMap[rook.square] = rook
            } else if (newSquare.file == piece.square.file - 2) {
                val rook: ChessPiece = hashMap[Square(newSquare.rank, newSquare.file - 2)]!!
                hashMap.remove(Square(newSquare.rank, newSquare.file - 2))
                rook.square = Square(newSquare.rank, newSquare.file + 1)
                hashMap[rook.square] = rook
            }
        }

        //Remove Defender
        if (hashMap.containsKey(newSquare)) {
            piecesOnBoard.remove(hashMap[newSquare])
        } else if (gameLogic.isEnPassant(
                previousSquare.value,
                currentSquare.value,
                newSquare,
                hashMap,
                piece
            )
        ) {
            piecesOnBoard.remove(hashMap[currentSquare.value])
            hashMap.remove(currentSquare.value)
        }
        val previousSquare = piece.square
        hashMap.remove(piece.square)
        piece.square = newSquare
        hashMap[newSquare] = piece
        setPreviousSquare(previousSquare)
        setCurrentSquare(newSquare)

        if (piece.color == "white") {
            if (piece.piece == "king") {
                whiteKingSquare.value = newSquare
            }
            setPlayerTurn("black")
            checkWhiteAttacks()
        } else {
            if (piece.piece == "king") {
                blackKingSquare.value = newSquare
            }
            setPlayerTurn("white")
            checkBlackAttacks()
        }
        setSquaresToBlock()
        //Check if King or Rooks Moved
        checkIfKingOrRooksMoved(piece)
    }

    fun getPiecesOnBoard(): MutableList<ChessPiece> {
        return piecesOnBoard
    }

    fun getHashMap(): MutableMap<Square, ChessPiece> {
        return hashMap
    }

    fun getPreviousSquare(): MutableState<Square> {
        return previousSquare
    }

    private fun setPreviousSquare(square: Square) {
        previousSquare.value = square
    }

    fun getCurrentSquare(): MutableState<Square> {
        return currentSquare
    }

    private fun setCurrentSquare(square: Square) {
        currentSquare.value = square
    }

    private fun setPlayerTurn(turn: String) {
        playerTurn.value = turn
    }

    fun getPlayerTurn(): MutableState<String> {
        return playerTurn
    }

    private fun isEnemyPiece(square: Square): Boolean {
        if (hashMap.containsKey(square)) {
            if (hashMap[square]?.color != playerTurn.value) {
                return true
            }
        }
        return false
    }

    private fun getAttackedSquaresNearKing(
        attacks: MutableList<Square>,
        kingsSquare: Square
    ): ArrayList<Square> {
        val squaresToBlock = arrayListOf<Square>()
        println(kingsSquare)
        println(attacks)
        if (attacks.contains(kingsSquare)) {
            println("king in check")
            kingInCheck.value = true
            for (rank in kingsSquare.rank + 1..7) {
                val squareToBlock = Square(rank, kingsSquare.file)
                if (attacks.contains(squareToBlock)) {
                    squaresToBlock.add(squareToBlock)
                } else if (isEnemyPiece(squareToBlock)) {
                    squaresToBlock.add(squareToBlock)
                    break
                } else {
                    break
                }
            }
            for (rank in kingsSquare.rank - 1 downTo 0) {
                val squareToBlock = Square(rank, kingsSquare.file)
                if (attacks.contains(squareToBlock)) {
                    squaresToBlock.add(squareToBlock)
                } else if (isEnemyPiece(squareToBlock)) {
                    squaresToBlock.add(squareToBlock)
                    break
                } else {
                    break
                }
            }
            for (file in kingsSquare.file + 1..7) {
                val squareToBlock = Square(kingsSquare.rank, file)
                if (attacks.contains(squareToBlock)) {
                    squaresToBlock.add(squareToBlock)
                } else if (isEnemyPiece(squareToBlock)) {
                    squaresToBlock.add(squareToBlock)
                    break
                } else {
                    break
                }
            }
            for (file in kingsSquare.file - 1 downTo 7) {
                val squareToBlock = Square(kingsSquare.rank, file)
                if (attacks.contains(squareToBlock)) {
                    squaresToBlock.add(squareToBlock)
                } else if (isEnemyPiece(squareToBlock)) {
                    squaresToBlock.add(squareToBlock)
                    break
                } else {
                    break
                }
            }
            for (rank in kingsSquare.rank + 1..7) {
                val squareToBlock = Square(rank, kingsSquare.file + (rank - kingsSquare.rank))
                if (attacks.contains(squareToBlock)) {
                    squaresToBlock.add(squareToBlock)
                } else if (isEnemyPiece(squareToBlock)) {
                    squaresToBlock.add(squareToBlock)
                    break
                } else {
                    break
                }
            }
            for (rank in kingsSquare.rank + 1..7) {
                val squareToBlock = Square(rank, kingsSquare.file - (rank - kingsSquare.rank))
                if (attacks.contains(squareToBlock)) {
                    squaresToBlock.add(squareToBlock)
                } else if (isEnemyPiece(squareToBlock)) {
                    squaresToBlock.add(squareToBlock)
                    break
                } else {
                    break
                }
            }
            for (rank in kingsSquare.rank - 1 downTo 0) {
                val squareToBlock = Square(rank, kingsSquare.file + (rank - kingsSquare.rank))
                if (attacks.contains(squareToBlock)) {
                    squaresToBlock.add(squareToBlock)
                } else if (isEnemyPiece(squareToBlock)) {
                    squaresToBlock.add(squareToBlock)
                    break
                } else {
                    break
                }
            }
            for (rank in kingsSquare.rank - 1 downTo 0) {
                val squareToBlock = Square(rank, kingsSquare.file - (rank - kingsSquare.rank))
                if (attacks.contains(squareToBlock)) {
                    squaresToBlock.add(squareToBlock)
                } else if (isEnemyPiece(squareToBlock)) {
                    squaresToBlock.add(squareToBlock)
                    break
                } else {
                    break
                }
            }
        } else {
            kingInCheck.value = false
        }
        return squaresToBlock
    }

    private fun getSquaresNearKing(kingsSquare: Square): ArrayList<Square> {
        val squaresToBlock = arrayListOf<Square>()
        for (rank in kingsSquare.rank + 1..7) {
            val squareToBlock = Square(rank, kingsSquare.file)
            squaresToBlock.add(squareToBlock)
        }
        for (rank in kingsSquare.rank - 1 downTo 0) {
            val squareToBlock = Square(rank, kingsSquare.file)
            squaresToBlock.add(squareToBlock)
        }
        for (file in kingsSquare.file + 1..7) {
            val squareToBlock = Square(kingsSquare.rank, file)
            squaresToBlock.add(squareToBlock)
        }
        for (file in kingsSquare.file - 1 downTo 7) {
            val squareToBlock = Square(kingsSquare.rank, file)
            squaresToBlock.add(squareToBlock)
        }
        for (rank in kingsSquare.rank + 1..7) {
            val squareToBlock = Square(rank, kingsSquare.file + (rank - kingsSquare.rank))
            squaresToBlock.add(squareToBlock)
        }
        for (rank in kingsSquare.rank + 1..7) {
            val squareToBlock = Square(rank, kingsSquare.file - (rank - kingsSquare.rank))
            squaresToBlock.add(squareToBlock)
        }
        for (rank in kingsSquare.rank - 1 downTo 0) {
            val squareToBlock = Square(rank, kingsSquare.file + (rank - kingsSquare.rank))
            squaresToBlock.add(squareToBlock)
        }
        for (rank in kingsSquare.rank - 1 downTo 0) {
            val squareToBlock = Square(rank, kingsSquare.file - (rank - kingsSquare.rank))
            squaresToBlock.add(squareToBlock)
        }
        return squaresToBlock
    }

    private fun setSquaresToBlock(): MutableList<Square> {
        squaresToBlock.clear()
        if (playerTurn.value == "white") {
            squaresToBlock.addAll(
                getAttackedSquaresNearKing(blackAttacks, whiteKingSquare.value)
            )
        } else {
            squaresToBlock.addAll(
                getAttackedSquaresNearKing(whiteAttacks, blackKingSquare.value)
            )
        }
        return squaresToBlock
    }

    fun getSquaresToBlock(): MutableList<Square> {
        return squaresToBlock
    }

    fun getBlackAttacks(): MutableList<Square> {
        return blackAttacks
    }

    fun getWhiteAttacks(): MutableList<Square> {
        return whiteAttacks
    }

    fun checkLegalMoves(piece: ChessPiece, checkDefendedPieces: Boolean): List<Square> {
        var listOfMoves = mutableListOf<Square>()
        when (piece.piece) {
            "pawn" -> {
                listOfMoves = pawnMoves(piece)
            }
            "rook" -> {
                listOfMoves = rookMoves(piece, checkDefendedPieces)
            }
            "knight" -> {
                listOfMoves = knightMoves(piece, checkDefendedPieces)
            }
            "bishop" -> {
                listOfMoves = bishopMoves(piece, checkDefendedPieces)
            }
            "king" -> {
                listOfMoves = kingMoves(piece)
            }
            "queen" -> {
                listOfMoves = queenMoves(piece, checkDefendedPieces)
            }
        }
        return listOfMoves
    }

    private fun pawnMoves(piece: ChessPiece): MutableList<Square> {
        return Pawn().moves(
            piece,
            hashMap,
            squaresToBlock,
            previousSquare.value,
            currentSquare.value
        )
    }

    private fun kingMoves(piece: ChessPiece): MutableList<Square> {
        if (piece.color == "white") {
            return King().moves(
                piece,
                hashMap,
                squaresToBlock,
                blackAttacks,
                whiteKingMoved.value,
                whiteKingSideRookMoved.value,
                whiteQueenSideRookMoved.value
            )
        }
        return King().moves(
            piece,
            hashMap,
            squaresToBlock,
            whiteAttacks,
            blackKingMoved.value,
            blackKingSideRookMoved.value,
            blackQueenSideRookMoved.value
        )
    }

    private fun bishopMoves(piece: ChessPiece, checkDefendedPieces: Boolean): MutableList<Square> {
        return Bishop().moves(piece, hashMap, squaresToBlock, checkDefendedPieces)
    }

    private fun rookMoves(piece: ChessPiece, checkDefendedPieces: Boolean): MutableList<Square> {
        return Rook().moves(piece, hashMap, squaresToBlock, checkDefendedPieces)
    }

    private fun queenMoves(piece: ChessPiece, checkDefendedPieces: Boolean): MutableList<Square> {
        return Queen().moves(piece, hashMap, squaresToBlock, checkDefendedPieces)
    }

    private fun knightMoves(piece: ChessPiece, checkDefendedPieces: Boolean): MutableList<Square> {
        return Knight().moves(piece, hashMap, squaresToBlock, checkDefendedPieces)
    }
}