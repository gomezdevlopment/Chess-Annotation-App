package com.gomezdevlopment.chessnotationapp.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.os.persistableBundleOf
import androidx.lifecycle.ViewModel
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameLogic
import com.gomezdevlopment.chessnotationapp.model.pieces.*


class GameRepository : ViewModel() {
    private var piecesOnBoard: MutableList<ChessPiece> = mutableListOf()
    private var hashMap: MutableMap<Square, ChessPiece> = HashMap()
    private var previousSquare: MutableState<Square> = mutableStateOf(Square(10, 10))
    private var currentSquare: MutableState<Square> = mutableStateOf(Square(10, 10))
    private var playerTurn: MutableState<String> = mutableStateOf("white")

    //private var whiteAttacks: MutableList<Square> = mutableListOf()
    //private var blackAttacks: MutableList<Square> = mutableListOf()
    private var squaresToBlock: MutableList<Square> = mutableListOf()
    private var whiteKingSquare: MutableState<Square> = mutableStateOf(Square(0, 4))
    private var blackKingSquare: MutableState<Square> = mutableStateOf(Square(7, 4))
    private var xRayAttacks: MutableList<Square> = mutableListOf()
    private var attacks: MutableList<Square> = mutableListOf()

    private var whiteKingMoved: MutableState<Boolean> = mutableStateOf(false)
    private var blackKingMoved: MutableState<Boolean> = mutableStateOf(false)
    private var whiteKingSideRookMoved: MutableState<Boolean> = mutableStateOf(false)
    private var whiteQueenSideRookMoved: MutableState<Boolean> = mutableStateOf(false)
    private var blackKingSideRookMoved: MutableState<Boolean> = mutableStateOf(false)
    private var blackQueenSideRookMoved: MutableState<Boolean> = mutableStateOf(false)

    private var kingInCheck: MutableState<Boolean> = mutableStateOf(false)
    private var piecesCheckingKing = mutableListOf<Square>()
    private var checksOnKing = mutableListOf<Square>()

    private lateinit var tempPreviousSquare: MutableState<Square>
    private lateinit var tempCurrentSquare: MutableState<Square>
    private lateinit var piecesOnBoardTemp: MutableList<ChessPiece>
    private lateinit var hashMapTemp: MutableMap<Square, ChessPiece>
    private lateinit var currentPlayerTurn: MutableState<String>
    private lateinit var kingsSquare: MutableState<Square>

    private var capturedPieces: MutableList<ChessPiece> = mutableListOf()

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
        checkAttacks()
        setXRayAttacks()
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
        checkAttacks()
        setXRayAttacks()
    }

    fun kingInCheck(): MutableState<Boolean> {
        return kingInCheck
    }

    fun kingSquare(): MutableState<Square> {
        if (playerTurn.value == "white") {
            return whiteKingSquare
        }
        return blackKingSquare
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


    private fun getKingSquare(): Square {
        if (playerTurn.value == "white") {
            return whiteKingSquare.value
        }
        return blackKingSquare.value
    }

    fun getChecksOnKing(): MutableList<Square> {
        return checksOnKing
    }

    private fun setChecksOnKing() {
        checksOnKing.clear()
        for (piece in piecesOnBoard) {
            if (piece.color != playerTurn.value) {
                when (piece.piece) {
                    "queen" -> {
                        checksOnKing.addAll(Queen().xRayAttacks(piece, hashMap, true))
                    }
                    "rook" -> {
                        checksOnKing.addAll(Rook().xRayAttacks(piece, hashMap, true))
                    }
                    "bishop" -> {
                        checksOnKing.addAll(Bishop().xRayAttacks(piece, hashMap, true))
                    }
                }
            }
        }
    }

    fun getAttacks(): MutableList<Square> {
        return attacks
    }

    fun getMoves(): MutableList<Square> {
        val moves = mutableListOf<Square>()
        for (piece in piecesOnBoard) {
            if (piece.color == playerTurn.value) {
                for (square in checkLegalMoves(piece, false)) {
                    moves.add(square)
                }
            }
        }
        return moves
    }

    private fun checkAttacks() {
        piecesCheckingKing.clear()
        attacks.clear()
        for (piece in piecesOnBoard) {
            if (piece.color != playerTurn.value) {
                if (piece.piece == "pawn") {
                    val pawnAttacks = Pawn().pawnAttacks(piece)
                    attacks.addAll(pawnAttacks)
                    for (square in pawnAttacks) {
                        if (square == kingSquare().value && !piecesCheckingKing.contains(piece.square)) {
                            piecesCheckingKing.add(piece.square)
                        }
                    }
                } else {
                    for (square in checkLegalMoves(piece, true)) {
                        if (square == kingSquare().value && !piecesCheckingKing.contains(piece.square)) {
                            piecesCheckingKing.add(piece.square)
                        }
                        attacks.add(square)
                    }
                }
            }
        }

    }

//    private fun checkWhiteAttacks() {
//        piecesCheckingKing.clear()
//        whiteAttacks.clear()
//        for (piece in piecesOnBoard) {
//            if (piece.color == "white") {
//                if (piece.piece == "pawn") {
//                    val pawnAttacks = Pawn().pawnAttacks(piece)
//                    whiteAttacks.addAll(pawnAttacks)
//                    for (square in pawnAttacks) {
//                        if (square == blackKingSquare.value && !piecesCheckingKing.contains(piece.square)) {
//                            piecesCheckingKing.add(piece.square)
//                        }
//                    }
//                } else {
//                    for (square in checkLegalMoves(piece, true)) {
//                        if (square == blackKingSquare.value && !piecesCheckingKing.contains(piece.square)) {
//                            piecesCheckingKing.add(piece.square)
//                        }
//                        whiteAttacks.add(square)
//                    }
//                }
//            }
//        }
//    }
//
//    private fun checkBlackAttacks() {
//        piecesCheckingKing.clear()
//        blackAttacks.clear()
//        for (piece in piecesOnBoard) {
//            if (piece.color == "black") {
//                if (piece.piece == "pawn") {
//                    val pawnAttacks = Pawn().pawnAttacks(piece)
//                    blackAttacks.addAll(pawnAttacks)
//                    for (square in pawnAttacks) {
//                        if (square == whiteKingSquare.value && !piecesCheckingKing.contains(piece.square)) {
//                            piecesCheckingKing.add(piece.square)
//                        }
//                    }
//                } else {
//                    for (square in checkLegalMoves(piece, true)) {
//                        if (square == whiteKingSquare.value && !piecesCheckingKing.contains(piece.square)) {
//                            piecesCheckingKing.add(piece.square)
//                        }
//                        blackAttacks.add(square)
//                    }
//                }
//            }
//        }
//    }

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

    fun saveGameState() {
        tempPreviousSquare = getPreviousSquare()
        tempCurrentSquare = getCurrentSquare()
        piecesOnBoardTemp = getPiecesOnBoard()
        hashMapTemp = getHashMap()
        currentPlayerTurn = getPlayerTurn()
        kingsSquare = kingSquare()
    }

    fun undoChangePiecePosition(
        piece: ChessPiece,
        originalSquare: Square,
        currentSquare: Square,
        previousSquare: Square,
        kingsSquare: Square,
        legalMove: Square
    ) {
        //val gameLogic = GameLogic()
//        if (piece.piece == "king") {
//            if (newSquare.file == piece.square.file + 2) {
//                val rook: ChessPiece = hashMap[Square(newSquare.rank, newSquare.file + 1)]!!
//                hashMap.remove(Square(newSquare.rank, newSquare.file + 1))
//                rook.square = Square(newSquare.rank, newSquare.file - 1)
//                hashMap[rook.square] = rook
//            } else if (newSquare.file == piece.square.file - 2) {
//                val rook: ChessPiece = hashMap[Square(newSquare.rank, newSquare.file - 2)]!!
//                hashMap.remove(Square(newSquare.rank, newSquare.file - 2))
//                rook.square = Square(newSquare.rank, newSquare.file + 1)
//                hashMap[rook.square] = rook
//            }
//        }

        //Add Defender
        println(capturedPieces)
        if(capturedPieces.isNotEmpty()){
            piecesOnBoard.add(capturedPieces[capturedPieces.size-1])
            hashMap[legalMove] = capturedPieces[capturedPieces.size-1]
        }
        //hashMap.remove(piece.square)
        //piece.square = newSquare
        hashMap.remove(legalMove)
        piece.square = originalSquare
        hashMap[originalSquare] = piece
        setPreviousSquare(previousSquare)
        setCurrentSquare(currentSquare)

        if (piece.color == "white") {
            if (piece.piece == "king") {
                whiteKingSquare.value = kingsSquare
            }
            setPlayerTurn("white")
            checkAttacks()
            if (attacks.contains(whiteKingSquare.value)) {
                setChecksOnKing()
            }
        } else {
            if (piece.piece == "king") {
                blackKingSquare.value = kingsSquare
            }
            setPlayerTurn("black")
            checkAttacks()
            if (attacks.contains(blackKingSquare.value)) {
                setChecksOnKing()
            }
        }
        setSquaresToBlock()
        //Check if King or Rooks Moved
        checkIfKingOrRooksMoved(piece)
        setXRayAttacks()
    }

    fun changePiecePosition(newSquare: Square, piece: ChessPiece) {
        //saveGameState()
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
            hashMap[newSquare]?.let { capturedPieces.add(it) }
            piecesOnBoard.remove(hashMap[newSquare])
            println("captured")
        } else if (gameLogic.isEnPassant(
                previousSquare.value,
                currentSquare.value,
                newSquare,
                hashMap,
                piece,
                getKingSquare()
            )
        ) {
            hashMap[currentSquare.value]?.let { capturedPieces.add(it) }
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
            checkAttacks()
            if (attacks.contains(blackKingSquare.value)) {
                setChecksOnKing()
            }
        } else {
            if (piece.piece == "king") {
                blackKingSquare.value = newSquare
            }
            setPlayerTurn("white")
            checkAttacks()
            if (attacks.contains(whiteKingSquare.value)) {
                setChecksOnKing()
            }
        }
        setSquaresToBlock()
        //Check if King or Rooks Moved
        checkIfKingOrRooksMoved(piece)
        setXRayAttacks()
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
        for (rank in kingsSquare.rank + 1..7) {
            val squareToBlock = Square(rank, kingsSquare.file)
            if (attacks.contains(squareToBlock) && checksOnKing.contains(squareToBlock)) {
                squaresToBlock.add(squareToBlock)
            } else if (isEnemyPiece(squareToBlock)) {
                //squaresToBlock.add(squareToBlock)
                break
            } else {
                break
            }
        }
        for (rank in kingsSquare.rank - 1 downTo 0) {
            val squareToBlock = Square(rank, kingsSquare.file)
            if (attacks.contains(squareToBlock) && checksOnKing.contains(squareToBlock)) {
                squaresToBlock.add(squareToBlock)
            } else if (isEnemyPiece(squareToBlock)) {
                //squaresToBlock.add(squareToBlock)
                break
            } else {
                break
            }
        }
        for (file in kingsSquare.file + 1..7) {
            val squareToBlock = Square(kingsSquare.rank, file)
            if (attacks.contains(squareToBlock) && checksOnKing.contains(squareToBlock)) {
                squaresToBlock.add(squareToBlock)
            } else if (isEnemyPiece(squareToBlock)) {
                //squaresToBlock.add(squareToBlock)
                break
            } else {
                break
            }
        }
        for (file in kingsSquare.file - 1 downTo 7) {
            val squareToBlock = Square(kingsSquare.rank, file)
            if (attacks.contains(squareToBlock) && checksOnKing.contains(squareToBlock)) {
                squaresToBlock.add(squareToBlock)
            } else if (isEnemyPiece(squareToBlock)) {
                //squaresToBlock.add(squareToBlock)
                break
            } else {
                break
            }
        }
        for (rank in kingsSquare.rank + 1..7) {
            val squareToBlock = Square(rank, kingsSquare.file + (rank - kingsSquare.rank))
            if (attacks.contains(squareToBlock) && checksOnKing.contains(squareToBlock)) {
                squaresToBlock.add(squareToBlock)
            } else if (isEnemyPiece(squareToBlock)) {
                //squaresToBlock.add(squareToBlock)
                break
            } else {
                break
            }
        }
        for (rank in kingsSquare.rank + 1..7) {
            val squareToBlock = Square(rank, kingsSquare.file - (rank - kingsSquare.rank))
            if (attacks.contains(squareToBlock) && checksOnKing.contains(squareToBlock)) {
                squaresToBlock.add(squareToBlock)
            } else if (isEnemyPiece(squareToBlock)) {
                //squaresToBlock.add(squareToBlock)
                break
            } else {
                break
            }
        }
        for (rank in kingsSquare.rank - 1 downTo 0) {
            val squareToBlock = Square(rank, kingsSquare.file + (rank - kingsSquare.rank))
            if (attacks.contains(squareToBlock) && checksOnKing.contains(squareToBlock)) {
                squaresToBlock.add(squareToBlock)
            } else if (isEnemyPiece(squareToBlock)) {
                //squaresToBlock.add(squareToBlock)
                break
            } else {
                break
            }
        }
        for (rank in kingsSquare.rank - 1 downTo 0) {
            val squareToBlock = Square(rank, kingsSquare.file - (rank - kingsSquare.rank))
            if (attacks.contains(squareToBlock) && checksOnKing.contains(squareToBlock)) {
                squaresToBlock.add(squareToBlock)
            } else if (isEnemyPiece(squareToBlock)) {
                //squaresToBlock.add(squareToBlock)
                break
            } else {
                break
            }
        }
        return squaresToBlock
    }

    fun getXRayAttacks(): MutableList<Square> {
        return xRayAttacks
    }

    fun setXRayAttacks() {
        xRayAttacks.clear()
        for (piece in piecesOnBoard) {
            if (piece.color != playerTurn.value) {
                when (piece.piece) {
                    "queen" -> {
                        xRayAttacks.addAll(Queen().xRayAttacks(piece, hashMap, false))
                    }
                    "rook" -> {
                        xRayAttacks.addAll(Rook().xRayAttacks(piece, hashMap, false))
                    }
                    "bishop" -> {
                        xRayAttacks.addAll(Bishop().xRayAttacks(piece, hashMap, false))
                    }
                }
            }
        }
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
        squaresToBlock.addAll(getAttackedSquaresNearKing(attacks, kingSquare().value))
//        if (playerTurn.value == "white") {
//            squaresToBlock.addAll(
//                getAttackedSquaresNearKing(blackAttacks, whiteKingSquare.value)
//            )
//        } else {
//            squaresToBlock.addAll(
//                getAttackedSquaresNearKing(whiteAttacks, blackKingSquare.value)
//            )
//        }
        return squaresToBlock
    }

    fun getSquaresToBlock(): MutableList<Square> {
        return squaresToBlock
    }

//    fun getBlackAttacks(): MutableList<Square> {
//        return blackAttacks
//    }
//
//    fun getWhiteAttacks(): MutableList<Square> {
//        return whiteAttacks
//    }

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
            currentSquare.value,
            xRayAttacks,
            getKingSquare(),
            piecesCheckingKing
        )
    }

    private fun kingMoves(piece: ChessPiece): MutableList<Square> {
        if (piece.color == "white") {
            return King().moves(
                piece,
                hashMap,
                squaresToBlock,
                attacks,
                whiteKingMoved.value,
                whiteKingSideRookMoved.value,
                whiteQueenSideRookMoved.value,
                xRayAttacks,
                getKingSquare(),
                getChecksOnKing(),
                piecesCheckingKing
            )
        }
        return King().moves(
            piece,
            hashMap,
            squaresToBlock,
            attacks,
            blackKingMoved.value,
            blackKingSideRookMoved.value,
            blackQueenSideRookMoved.value,
            xRayAttacks,
            getKingSquare(),
            getChecksOnKing(),
            piecesCheckingKing
        )
    }

    private fun bishopMoves(piece: ChessPiece, checkDefendedPieces: Boolean): MutableList<Square> {
        return Bishop().moves(
            piece,
            hashMap,
            squaresToBlock,
            checkDefendedPieces,
            xRayAttacks,
            getKingSquare(),
            piecesCheckingKing
        )
    }

    private fun rookMoves(piece: ChessPiece, checkDefendedPieces: Boolean): MutableList<Square> {
        return Rook().moves(
            piece,
            hashMap,
            squaresToBlock,
            checkDefendedPieces,
            xRayAttacks,
            getKingSquare(),
            piecesCheckingKing
        )
    }

    private fun queenMoves(piece: ChessPiece, checkDefendedPieces: Boolean): MutableList<Square> {
        return Queen().moves(
            piece,
            hashMap,
            squaresToBlock,
            checkDefendedPieces,
            xRayAttacks,
            getKingSquare(),
            piecesCheckingKing
        )
    }

    private fun knightMoves(piece: ChessPiece, checkDefendedPieces: Boolean): MutableList<Square> {
        return Knight().moves(
            piece,
            hashMap,
            squaresToBlock,
            checkDefendedPieces,
            xRayAttacks,
            getKingSquare(),
            piecesCheckingKing
        )
    }

    fun testAllMoves() {
        var numberOfMoves = 0
        for (piece in piecesOnBoard) {
            for (move in checkLegalMoves(piece, false)) {
                numberOfMoves += 1
            }
        }
    }
}