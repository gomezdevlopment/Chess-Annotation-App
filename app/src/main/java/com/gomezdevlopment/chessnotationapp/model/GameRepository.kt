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
    private var squaresToBlock: MutableList<Square> = mutableListOf()
    private var whiteKingSquare: MutableState<Square> = mutableStateOf(Square(0, 4))
    private var blackKingSquare: MutableState<Square> = mutableStateOf(Square(7, 4))
    private var xRayAttacks: MutableList<Square> = mutableListOf()
    private var attacks: MutableList<Square> = mutableListOf()
    private var whiteKingCanCastleKingSide: MutableState<Boolean> = mutableStateOf(true)
    private var whiteKingCanCastleQueenSide: MutableState<Boolean> = mutableStateOf(true)
    private var blackKingCanCastleKingSide: MutableState<Boolean> = mutableStateOf(true)
    private var blackKingCanCastleQueenSide: MutableState<Boolean> = mutableStateOf(true)
    private var kingInCheck: MutableState<Boolean> = mutableStateOf(false)
    private var piecesCheckingKing = mutableListOf<Square>()
    private var checksOnKing = mutableListOf<Square>()
    private var capturedPieces: MutableList<ChessPiece> = mutableListOf()
    private var checks = mutableStateOf(0)
    private var captures = mutableStateOf(0)
    private var castles = mutableStateOf(0)
    private var enPassants = mutableStateOf(0)
    private var gameStateAsFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 "
    private var previousGameStates = mutableListOf<GameState>()
    private var gameState = GameState(previousSquare.value, currentSquare.value, gameStateAsFEN)
    private var previousGameState = mutableStateOf(GameState(previousSquare.value, currentSquare.value, gameStateAsFEN))
    private var testDepth = 0

    fun getChecks(): Int {
        return checks.value
    }

    fun getCaptures(): Int {
        return captures.value
    }

    fun getCastles(): Int {
        return castles.value
    }

    fun getEnPassants(): Int {
        return enPassants.value
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
        testPosition3()
//        previousGameStates.add(GameState(previousSquare.value, currentSquare.value, gameStateAsFEN))
        //initialPosition()
    }

    fun getGameStateAsFEN(): String{
        val fenString = StringBuilder()
        for(rank in 7 downTo 0){
            var emptySquares = 0
            for(file in 0..7){
                if(hashMap.containsKey(Square(rank, file))){
                    if(emptySquares != 0){
                        fenString.append(emptySquares)
                    }
                    emptySquares = 0
                    val piece = hashMap[Square(rank, file)]
                    when(piece?.piece){
                        "rook" -> {
                            if(piece.color == "white") {
                                fenString.append("R")
                            }else{
                                fenString.append("r")
                            }
                        }
                        "knight" -> {
                            if(piece.color == "white") {
                                fenString.append("N")
                            }else{
                                fenString.append("n")
                            }
                        }
                        "bishop" -> {
                            if(piece.color == "white") {
                                fenString.append("B")
                            }else{
                                fenString.append("b")
                            }
                        }
                        "queen" -> {
                            if(piece.color == "white") {
                                fenString.append("Q")
                            }else{
                                fenString.append("q")
                            }
                        }
                        "king" -> {
                            if(piece.color == "white") {
                                fenString.append("K")
                            }else{
                                fenString.append("k")
                            }
                        }
                        "pawn" -> {
                            if(piece.color == "white") {
                                fenString.append("P")
                            }else{
                                fenString.append("p")
                            }
                        }
                    }
                }else{
                    emptySquares+=1
                    if(file == 7){
                        if(emptySquares != 0){
                            fenString.append(emptySquares)
                        }
                    }
                }
            }
            if(rank!=0){
                fenString.append("/")
            }
        }
        fenString.append(" ")
        when(playerTurn.value){
            "white" -> fenString.append("w ")
            "black" -> fenString.append("b ")
        }

        if(!whiteKingCanCastleKingSide.value && !whiteKingCanCastleQueenSide.value && !blackKingCanCastleKingSide.value && !blackKingCanCastleQueenSide.value){
            fenString.append("- ")
        }else{
            if(whiteKingCanCastleKingSide.value) {fenString.append("K")}
            if(whiteKingCanCastleQueenSide.value) {fenString.append("Q")}
            if(blackKingCanCastleKingSide.value) {fenString.append("k")}
            if(blackKingCanCastleQueenSide.value) {fenString.append("q")}
            fenString.append(" ")
        }
        return fenString.toString()
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
                        'k' -> {
                            pieces.add(ChessPiece("black", "king", R.drawable.ic_bk_alpha, Square(rank, file)))
                            blackKingSquare.value = Square(rank, file)
                        }
                        'p' -> {pieces.add(ChessPiece("black", "pawn", R.drawable.ic_bp_alpha, Square(rank, file)))}
                        'R' -> {pieces.add(ChessPiece("white", "rook", R.drawable.ic_wr_alpha, Square(rank, file)))}
                        'N' -> {pieces.add(ChessPiece("white", "knight", R.drawable.ic_wn_alpha, Square(rank, file)))}
                        'B' -> {pieces.add(ChessPiece("white", "bishop", R.drawable.ic_wb_alpha, Square(rank, file)))}
                        'Q' -> {pieces.add(ChessPiece("white", "queen", R.drawable.ic_wq_alpha, Square(rank, file)))}
                        'K' -> {
                            pieces.add(ChessPiece("white", "king", R.drawable.ic_wk, Square(rank, file)))
                            whiteKingSquare.value = Square(rank, file)
                        }
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
                '-' -> {
                    whiteKingCanCastleKingSide.value = false
                    whiteKingCanCastleQueenSide.value = false
                    blackKingCanCastleKingSide.value = false
                    blackKingCanCastleQueenSide.value = false
                }
            }
        }
        return pieces
    }

    fun setPositionFromFen(fen: String){
        piecesOnBoard.clear()
        hashMap.clear()
        piecesOnBoard.addAll(parseFEN(fen))
        for (piece in piecesOnBoard) {
            hashMap[piece.square] = piece
        }
        checkAttacks()
        setXRayAttacks()
        //gameState.value.fenPosition = getGameStateAsFEN()
    }

    fun testPositionKiwipete(){
        setPositionFromFen("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ")
        gameState.fenPosition = getGameStateAsFEN()
    }

    fun testPosition3(){
        setPositionFromFen("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -")
        gameState.fenPosition = getGameStateAsFEN()
    }
    fun initialPosition(){
        setPositionFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 ")
        gameState.fenPosition = getGameStateAsFEN()
    }

//    fun resetGame() {
//        piecesOnBoard.clear()
//        hashMap.clear()
//        squaresToBlock.clear()
//        addPieces()
//        playerTurn.value = "white"
//        whiteKingSquare.value = Square(0, 4)
//        blackKingSquare.value = Square(7, 4)
//        previousSquare.value = Square(10, 10)
//        currentSquare.value = Square(10, 10)
//        whiteKingCanCastleKingSide.value = true
//        whiteKingCanCastleQueenSide.value = true
//        blackKingCanCastleKingSide.value = true
//        blackKingCanCastleQueenSide.value = true
//        kingInCheck = mutableStateOf(false)
//        piecesCheckingKing.clear()
//        checksOnKing.clear()
//        capturedPieces.clear()
//        checkAttacks()
//        setXRayAttacks()
//    }

    fun kingInCheck(): MutableState<Boolean> {
        return kingInCheck
    }

    fun kingSquare(): MutableState<Square> {
        if (playerTurn.value == "white") {
            return whiteKingSquare
        }
        return blackKingSquare
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
        kingInCheck.value = piecesCheckingKing.isNotEmpty()
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
                            Square(0, 0) -> whiteKingCanCastleQueenSide.value = false
                            Square(0, 7) -> whiteKingCanCastleKingSide.value = false
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
                            Square(7, 0) -> blackKingCanCastleQueenSide.value = false
                            Square(7, 7) -> blackKingCanCastleKingSide.value = false
                        }
                    }
                }
            }
        }
    }

    val previousMoveWasEnPassant = mutableStateOf(false)
    fun castleKingSide(): Boolean {
        if(playerTurn.value == "white"){
            return whiteKingCanCastleKingSide.value
        }
        return blackKingCanCastleKingSide.value
    }

    fun castleQueenSide(): Boolean {
        if(playerTurn.value == "white"){
            return whiteKingCanCastleQueenSide.value
        }
        return blackKingCanCastleQueenSide.value
    }

    fun previousGameState(){
        if(previousGameStates.size>1){
            previousGameStates.removeAt(previousGameStates.size-1)
            setPositionFromFen(previousGameStates[previousGameStates.size-1].fenPosition)
            currentSquare.value = previousGameStates[previousGameStates.size-1].currentSquare
            previousSquare.value = previousGameStates[previousGameStates.size-1].previousSquare
        }

    }

//    fun getPreviousGameStates(): MutableList<GameState> {
//        return previousGameStates
//    }

    fun getCurrentGameState(): GameState {
        return GameState(previousSquare.value, currentSquare.value, gameStateAsFEN)
    }

//    fun undoChangePiecePosition(
//        piece: ChessPiece,
//        originalSquare: Square,
//        currentSquare: Square,
//        previousSquare: Square,
//        kingsSquare: Square,
//        legalMove: Square,
//        canCastleKingSide: Boolean,
//        canCastleQueenSide: Boolean
//    ) {
//        //val gameLogic = GameLogic()
//        if (piece.piece == "king") {
//            if(canCastleKingSide){
//                if (legalMove.file == originalSquare.file + 2) {
//                    val rook: ChessPiece = hashMap[Square(legalMove.rank, legalMove.file - 1)]!!
//                    hashMap.remove(Square(legalMove.rank, legalMove.file - 1))
//                    rook.square = Square(legalMove.rank, legalMove.file + 1)
//                    hashMap[rook.square] = rook
//
//                }
//            }else if(canCastleQueenSide){
//                if (legalMove.file == originalSquare.file - 2) {
//                    val rook: ChessPiece = hashMap[Square(legalMove.rank, legalMove.file + 1)]!!
//                    hashMap.remove(Square(legalMove.rank, legalMove.file + 1))
//                    rook.square = Square(legalMove.rank, legalMove.file - 2)
//                    hashMap[rook.square] = rook
//                }
//            }
//        }
//        if(piece.color == "white"){
//            whiteKingCanCastleKingSide.value = canCastleKingSide
//            whiteKingCanCastleQueenSide.value = canCastleQueenSide
//        }else{
//            blackKingCanCastleKingSide.value = canCastleKingSide
//            blackKingCanCastleQueenSide.value = canCastleQueenSide
//        }
//        hashMap.remove(legalMove)
//        //Add Defender
//        val capturedPiecesTemp = mutableListOf<ChessPiece>()
//        capturedPiecesTemp.addAll(capturedPieces)
//        if (capturedPiecesTemp.isNotEmpty()) {
//            for(capturedPiece in capturedPiecesTemp){
//                if(capturedPiece.square == legalMove){
//                    piecesOnBoard.add(capturedPiece)
//                    hashMap[legalMove] = capturedPiece
//                    capturedPieces.remove(capturedPiece)
//                }else if(capturedPiece.square == currentSquare && previousMoveWasEnPassant.value){
//                    piecesOnBoard.add(capturedPiece)
//                    hashMap[currentSquare] = capturedPiece
//                    capturedPieces.remove(capturedPiece)
//                    previousMoveWasEnPassant.value = false
//                }
//            }
//        }
//
//        piece.square = originalSquare
//        hashMap[originalSquare] = piece
//        setPreviousSquare(previousSquare)
//        setCurrentSquare(currentSquare)
//
//        if (piece.color == "white") {
//            if (piece.piece == "king") {
//                whiteKingSquare.value = kingsSquare
//            }
//            setPlayerTurn("white")
//            checkAttacks()
//        } else {
//            if (piece.piece == "king") {
//                blackKingSquare.value = kingsSquare
//            }
//            setPlayerTurn("black")
//            checkAttacks()
//        }
//
//        //checkIfKingOrRooksMoved(piece)
//        setXRayAttacks()
//    }

    fun changePiecePosition(newSquare: Square, piece: ChessPiece, depth: Int) {
        //testDepth = depth
        if(previousGameStates.isEmpty()){
            previousGameStates.add(GameState(previousSquare.value, currentSquare.value, getGameStateAsFEN()))
        }
        val gameLogic = GameLogic()
        //Castling
        if (piece.piece == "king") {
            if(castleKingSide()){
                if (newSquare.file == piece.square.file + 2) {
                    val rook: ChessPiece = hashMap[Square(newSquare.rank, newSquare.file + 1)]!!
                    hashMap.remove(Square(newSquare.rank, newSquare.file + 1))
                    rook.square = Square(newSquare.rank, newSquare.file - 1)
                    hashMap[rook.square] = rook
                    if(depth == 1){
                        castles.value+=1
                    }
                }
            }
            if(castleQueenSide()){
                if (newSquare.file == piece.square.file - 2) {
                    val rook: ChessPiece = hashMap[Square(newSquare.rank, newSquare.file - 2)]!!
                    hashMap.remove(Square(newSquare.rank, newSquare.file - 2))
                    rook.square = Square(newSquare.rank, newSquare.file + 1)
                    hashMap[rook.square] = rook
                    if(depth == 1){
                        castles.value+=1
                    }
                }
            }
        }
        checkIfKingOrRooksMoved(piece)
       // previousMoveWasEnPassant.value = false
        //Remove Defender
        if (hashMap.containsKey(newSquare)) {
            if(depth == 1){
                captures.value+=1
            }
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
            //previousMoveWasEnPassant.value = true
            if(depth == 1){
                enPassants.value+=1
                captures.value+=1
            }
            hashMap[currentSquare.value]?.let { capturedPieces.add(it) }
            piecesOnBoard.remove(hashMap[currentSquare.value])
            hashMap.remove(currentSquare.value)
        }
        val previousPieceSquare = piece.square
        hashMap.remove(piece.square)
        piece.square = newSquare
        hashMap[newSquare] = piece
        setPreviousSquare(previousPieceSquare)
        setCurrentSquare(newSquare)

        if (piece.color == "white") {
            if (piece.piece == "king") {
                whiteKingSquare.value = newSquare
            }
            setPlayerTurn("black")
            checkAttacks()
        } else {
            if (piece.piece == "king") {
                blackKingSquare.value = newSquare
            }
            setPlayerTurn("white")
            checkAttacks()
        }
        setXRayAttacks()
        previousGameStates.add(GameState(previousSquare.value, currentSquare.value, getGameStateAsFEN()))
        setPositionFromFen(getGameStateAsFEN())
        if(kingInCheck.value && depth == 1){
            checks.value+=1
        }
    }

    fun getGameState(): GameState {
        return gameState
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

    private fun setSquaresToBlock(): MutableList<Square> {
        squaresToBlock.clear()
        squaresToBlock.addAll(getAttackedSquaresNearKing(attacks, kingSquare().value))
        return squaresToBlock
    }

    fun getSquaresToBlock(): MutableList<Square> {
        return squaresToBlock
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