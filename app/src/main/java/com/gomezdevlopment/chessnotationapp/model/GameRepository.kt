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

//    private var whiteKingMoved: MutableState<Boolean> = mutableStateOf(false)
//    private var blackKingMoved: MutableState<Boolean> = mutableStateOf(false)
//    private var whiteKingSideRookMoved: MutableState<Boolean> = mutableStateOf(true)
//    private var whiteQueenSideRookMoved: MutableState<Boolean> = mutableStateOf(true)
//    private var blackKingSideRookMoved: MutableState<Boolean> = mutableStateOf(true)
//    private var blackQueenSideRookMoved: MutableState<Boolean> = mutableStateOf(true)

    private var whiteKingCanCastleKingSide: MutableState<Boolean> = mutableStateOf(true)
    private var whiteKingCanCastleQueenSide: MutableState<Boolean> = mutableStateOf(true)
    private var blackKingCanCastleKingSide: MutableState<Boolean> = mutableStateOf(true)
    private var blackKingCanCastleQueenSide: MutableState<Boolean> = mutableStateOf(true)

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

    private var checks = mutableStateOf(0)
    private var captures = mutableStateOf(0)

    fun getChecks(): Int {
        return checks.value
    }

    fun getCaptures(): Int {
        return captures.value
    }
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
        //addPieces()
        //testPositionKiwipete()
        initialPosition()
        checkAttacks()
        setXRayAttacks()
    }

    fun parseFEN(fen: String): MutableList<ChessPiece> {
        val pieces = mutableListOf<ChessPiece>()
        val splitFen = fen.split(" ")
        val piecePositions = splitFen[0].split("/")
        var rank = 8
        for(string in piecePositions){
            rank--
            var file = 0
            for(char in string){
                if(char.isDigit()){
                    file+=char.digitToInt()
                }else{
                    when(char) {
                        'r' -> {pieces.add(ChessPiece("black", "rook", R.drawable.ic_br_alpha, Square(rank, file)))}
                        'n' -> {pieces.add(ChessPiece("black", "knight", R.drawable.ic_bn_alpha, Square(rank, file)))}
                        'b' -> {pieces.add(ChessPiece("black", "bishop", R.drawable.ic_bb_alpha, Square(rank, file)))}
                        'q' -> {pieces.add(ChessPiece("black", "queen", R.drawable.ic_bq_alpha, Square(rank, file)))}
                        'k' -> {pieces.add(ChessPiece("black", "king", R.drawable.ic_bk_alpha, Square(rank, file)))}
                        'p' -> {pieces.add(ChessPiece("black", "pawn", R.drawable.ic_bp_alpha, Square(rank, file)))}
                        'R' -> {pieces.add(ChessPiece("white", "rook", R.drawable.ic_wr_alpha, Square(rank, file)))}
                        'N' -> {pieces.add(ChessPiece("white", "knight", R.drawable.ic_wn_alpha, Square(rank, file)))}
                        'B' -> {pieces.add(ChessPiece("white", "bishop", R.drawable.ic_wb_alpha, Square(rank, file)))}
                        'Q' -> {pieces.add(ChessPiece("white", "queen", R.drawable.ic_wq_alpha, Square(rank, file)))}
                        'K' -> {pieces.add(ChessPiece("white", "king", R.drawable.ic_wk, Square(rank, file)))}
                        'P' -> {pieces.add(ChessPiece("white", "pawn", R.drawable.ic_wp_alpha, Square(rank, file)))}
                    }
                    file++
                }
            }
        }

        when(splitFen[1]){
            "w" -> playerTurn.value = "white"
            "b" -> playerTurn.value = "black"
        }

        for(char in splitFen[2]){
            when(char) {
                'K' -> whiteKingCanCastleKingSide.value = true
                'Q' -> whiteKingCanCastleQueenSide.value = true
                'k' -> blackKingCanCastleKingSide.value = true
                'q' -> blackKingCanCastleQueenSide.value = true
            }
        }
        return pieces
    }

    fun testPositionKiwipete(){
        piecesOnBoard.clear()
        hashMap.clear()
        piecesOnBoard.addAll(parseFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - "))
        for (piece in piecesOnBoard) {
            hashMap[piece.square] = piece
        }
    }

    fun initialPosition(){
        piecesOnBoard.addAll(parseFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 "))
        for (piece in piecesOnBoard) {
            hashMap[piece.square] = piece
        }
    }

    fun resetGame() {
        piecesOnBoard.clear()
        hashMap.clear()
        squaresToBlock.clear()
        addPieces()
        playerTurn.value = "white"
        whiteKingSquare.value = Square(0, 4)
        blackKingSquare.value = Square(7, 4)
        previousSquare.value = Square(10, 10)
        currentSquare.value = Square(10, 10)
//        whiteKingMoved = mutableStateOf(false)
//        blackKingMoved = mutableStateOf(false)
//        whiteKingSideRookMoved = mutableStateOf(true)
//        whiteQueenSideRookMoved = mutableStateOf(true)
//        blackKingSideRookMoved = mutableStateOf(true)
//        blackQueenSideRookMoved = mutableStateOf(true)

        kingInCheck = mutableStateOf(false)
        piecesCheckingKing.clear()
        checksOnKing.clear()
        capturedPieces.clear()
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
                            //println("${piece.square} checking King ${kingSquare().value}")
                        }
                    }
                } else {
                    for (square in checkLegalMoves(piece, true)) {
                        if (square == kingSquare().value && !piecesCheckingKing.contains(piece.square)) {
                            piecesCheckingKing.add(piece.square)
                            //println("${piece.square} checking King ${kingSquare().value}")
                        }
                        attacks.add(square)
                    }
                }
            }
        }
        kingInCheck.value = piecesCheckingKing.isNotEmpty()
        if(kingInCheck.value){
            checks.value+=1
        }
        setChecksOnKing()
        setSquaresToBlock()
    }

    private fun checkIfKingOrRooksMoved(piece: ChessPiece) {
        if (piece.color == "white") {
            if (whiteKingCanCastleQueenSide.value || whiteKingCanCastleKingSide.value) {
                when (piece.piece) {
                    "king" -> {
                        whiteKingCanCastleQueenSide.value = false
                        whiteKingCanCastleKingSide.value = false
                    }
                    "rook" -> {
                        when (piece.square) {
                            Square(0, 0) -> whiteKingCanCastleQueenSide.value = true
                            Square(0, 7) -> whiteKingCanCastleKingSide.value = true
                        }
                    }
                }
            }

        } else {
            if (blackKingCanCastleQueenSide.value || blackKingCanCastleQueenSide.value) {
                when (piece.piece) {
                    "king" -> {
                        blackKingCanCastleQueenSide.value = false
                        blackKingCanCastleKingSide.value = false
                    }
                    "rook" -> {
                        when (piece.square) {
                            Square(7, 0) -> blackKingCanCastleQueenSide.value = true
                            Square(7, 7) -> blackKingCanCastleKingSide.value = true
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
        hashMap.remove(legalMove)
        //Add Defender
        //println(capturedPieces)
        val capturedPiecesTemp = mutableListOf<ChessPiece>()
        capturedPiecesTemp.addAll(capturedPieces)
        if (capturedPiecesTemp.isNotEmpty()) {
            //val capturedPiece = capturedPieces[capturedPieces.size - 1]
            for(capturedPiece in capturedPiecesTemp){
                if(capturedPiece.square == legalMove){
                    //println("${piece.piece} $originalSquare captured ${capturedPiece.piece} ${capturedPiece.square}")
                    //println("Captured Piece: ${capturedPiece.piece} ${capturedPiece.square}")
                    //println(legalMove)
                    //println("${capturedPiece.piece} : ${capturedPiece.square}")
                    piecesOnBoard.add(capturedPiece)
                    //println("Legal Move: $legalMove")
                    hashMap[legalMove] = capturedPiece
                    capturedPieces.remove(capturedPiece)
                }
            }
        }
        //hashMap.remove(piece.square)
        //piece.square = newSquare
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
                //setChecksOnKing()
            }
        } else {
            if (piece.piece == "king") {
                blackKingSquare.value = kingsSquare
            }
            setPlayerTurn("black")
            checkAttacks()
            if (attacks.contains(blackKingSquare.value)) {
                //setChecksOnKing()
            }
        }
        //setSquaresToBlock()
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
            captures.value+=1
            hashMap[newSquare]?.let {
                capturedPieces.add(it)
            }
            piecesOnBoard.remove(hashMap[newSquare])
        }
        else if (gameLogic.isEnPassant(
                previousSquare.value,
                currentSquare.value,
                newSquare,
                hashMap,
                piece,
                getKingSquare()
            )
        ) {
            println("--------------------en passant--------------------")
            captures.value+=1
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
                //checks.value+=1
                //setChecksOnKing()
                //setSquaresToBlock()
                //kingInCheck.value = true
            }
        } else {
            if (piece.piece == "king") {
                blackKingSquare.value = newSquare
            }
            setPlayerTurn("white")
            checkAttacks()
            if (attacks.contains(whiteKingSquare.value)) {
                //checks.value+=1
                //setChecksOnKing()
                //setSquaresToBlock()
                //kingInCheck.value = true
            }
        }
        //println(attacks)
        //println(kingInCheck.value)
        //println(piecesCheckingKing)
       // println(kingSquare().value)
        //(playerTurn)
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
                whiteKingCanCastleKingSide.value,
                whiteKingCanCastleQueenSide.value,
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
            blackKingCanCastleKingSide.value,
            blackKingCanCastleQueenSide.value,
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
}