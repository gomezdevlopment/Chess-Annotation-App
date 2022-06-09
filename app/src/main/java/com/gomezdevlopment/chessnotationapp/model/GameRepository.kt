package com.gomezdevlopment.chessnotationapp.model

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.gomezdevlopment.chessnotationapp.model.game_logic.EndOfGameConditions
import com.gomezdevlopment.chessnotationapp.model.game_logic.FEN
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameLogic
import com.gomezdevlopment.chessnotationapp.model.game_logic.Notation
import com.gomezdevlopment.chessnotationapp.model.pieces.*


class GameRepository() : ViewModel() {
   var piecesOnBoard: MutableList<ChessPiece> = mutableStateListOf()
     var occupiedSquares: MutableMap<Square, ChessPiece> = mutableMapOf()
     var mapOfPiecesAndTheirLegalMoves: MutableMap<ChessPiece, MutableList<Square>> =
        mutableMapOf()
     var previousSquare: MutableState<Square> = mutableStateOf(Square(10, 10))
     var currentSquare: MutableState<Square> = mutableStateOf(Square(10, 10))
     var playerTurn: MutableState<String> = mutableStateOf("white")
     var squaresToBlock: MutableList<Square> = mutableListOf()
     var whiteKingSquare: MutableState<Square> = mutableStateOf(Square(0, 4))
     var blackKingSquare: MutableState<Square> = mutableStateOf(Square(7, 4))
     var xRayAttacks: MutableList<Square> = mutableListOf()
     var attacks: MutableList<Square> = mutableListOf()
     var allLegalMoves: MutableList<Square> = mutableListOf()
     var whiteCanCastleKingSide: MutableState<Boolean> = mutableStateOf(false)
     var whiteCanCastleQueenSide: MutableState<Boolean> = mutableStateOf(false)
     var blackCanCastleKingSide: MutableState<Boolean> = mutableStateOf(false)
     var blackCanCastleQueenSide: MutableState<Boolean> = mutableStateOf(false)
     var kingInCheck: MutableState<Boolean> = mutableStateOf(false)
     var piecesCheckingKing = mutableListOf<Square>()
     var checksOnKing = mutableListOf<Square>()
     var capturedPieces: MutableList<ChessPiece> = mutableListOf()
     var checks = mutableStateOf(0)
     var captures = mutableStateOf(0)
     var castles = mutableStateOf(0)
     var enPassants = mutableStateOf(0)
     var promotions = mutableStateOf(0)
     //var gameStateAsFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 "
     var previousGameStates = mutableListOf<GameState>()
     var gameState = GameState(previousSquare.value, currentSquare.value, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 ")
     var checkmate: MutableState<Boolean> = mutableStateOf(false)
     var stalemate: MutableState<Boolean> = mutableStateOf(false)
     var insufficientMaterial: MutableState<Boolean> = mutableStateOf(false)
     var threeFoldRepetition: MutableState<Boolean> = mutableStateOf(false)
     var fiftyMoveRule: MutableState<Boolean> = mutableStateOf(false)
     var fiftyMoveCount = mutableStateOf(0)

     var pieceSound: MutableState<Boolean> = mutableStateOf(false)
     var checkSound: MutableState<Boolean> = mutableStateOf(false)
     var captureSound: MutableState<Boolean> = mutableStateOf(false)
     var castlingSound: MutableState<Boolean> = mutableStateOf(false)
     var gameEndSound: MutableState<Boolean> = mutableStateOf(false)

     var annotations: MutableList<String> = mutableStateListOf("start:")
     var currentNotation: StringBuilder = StringBuilder("")

//    fun getAnnotations(): MutableList<String> {
//        return annotations
//    }
//
//    fun getPieceSound(): MutableState<Boolean> {
//        return pieceSound
//    }
//
//    fun getCheckSound(): MutableState<Boolean> {
//        return checkSound
//    }
//
//    fun getCaptureSound(): MutableState<Boolean> {
//        return captureSound
//    }
//
//    fun getCastlingSound(): MutableState<Boolean> {
//        return castlingSound
//    }
//
//    fun getGameEndSound(): MutableState<Boolean> {
//        return gameEndSound
//    }
//
//    fun getCheckmate(): MutableState<Boolean> {
//        return checkmate
//    }
//
//    fun getStalemate(): MutableState<Boolean> {
//        return stalemate
//    }
//
//    fun getInsufficientMaterial(): MutableState<Boolean> {
//        return insufficientMaterial
//    }
//
//    fun getThreeFoldRepetition(): MutableState<Boolean> {
//        return threeFoldRepetition
//    }
//
//    fun getFiftyMoveRule(): MutableState<Boolean> {
//        return fiftyMoveRule
//    }
//
//    fun getChecks(): Int {
//        return checks.value
//    }
//
//    fun getCaptures(): Int {
//        return captures.value
//    }
//
//    fun getCastles(): Int {
//        return castles.value
//    }
//
//    fun getEnPassants(): Int {
//        return enPassants.value
//    }
//
//    fun getPromotions(): Int {
//        return promotions.value
//    }


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
        //testPosition4()
        //promotionPosition()
        //addPieces()
        //testPositionKiwipete()
        testPosition3()
//        previousGameStates.add(GameState(previousSquare.value, currentSquare.value, gameStateAsFEN))
        //initialPosition()
    }

    fun resetGame() {
        previousGameStates.clear()
        checkmate = mutableStateOf(false)
        stalemate = mutableStateOf(false)
        insufficientMaterial = mutableStateOf(false)
        threeFoldRepetition = mutableStateOf(false)
        fiftyMoveRule = mutableStateOf(false)
        fiftyMoveCount = mutableStateOf(0)
        previousSquare = mutableStateOf(Square(10, 10))
        currentSquare = mutableStateOf(Square(10, 10))
        playerTurn = mutableStateOf("white")
        annotations.clear()
        currentNotation = StringBuilder("")
        initialPosition()
    }

    private fun getGameStateAsFEN(): String {
        return FEN().getGameStateAsFEN(
            occupiedSquares,
            playerTurn,
            whiteCanCastleKingSide,
            whiteCanCastleQueenSide,
            blackCanCastleKingSide,
            blackCanCastleQueenSide
        )
    }

    fun clearPieces() {
        piecesOnBoard.clear()
    }

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
            blackKingSquare
        )

        piecesOnBoard.addAll(piecesOnBoardFromFENPosition)
        for (piece in piecesOnBoard) {
            occupiedSquares[piece.square] = piece
        }
        checkAttacks()
        checkAllLegalMoves()
        //gameState.value.fenPosition = getGameStateAsFEN()
    }

    private fun addInitialGameState() {
        previousGameStates.clear()
        previousGameStates.add(
            GameState(
                previousSquare.value,
                currentSquare.value,
                getGameStateAsFEN()
            )
        )
    }

    fun testPositionKiwipete() {
        setPositionFromFen("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ")
        addInitialGameState()
        //gameState.fenPosition = getGameStateAsFEN()
    }

    fun testPosition3() {
        setPositionFromFen("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -")
        addInitialGameState()
        //gameState.fenPosition = getGameStateAsFEN()
    }

    fun testPosition4() {
        setPositionFromFen("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq -")
        addInitialGameState()
        //gameState.fenPosition = getGameStateAsFEN()
    }

    fun initialPosition() {
        setPositionFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 ")
        addInitialGameState()
        //gameState.fenPosition = getGameStateAsFEN()
    }

    fun promotionPosition() {
        setPositionFromFen("2r5/KP6/8/8/8/8/6pk/8 w - - 0 1")
        addInitialGameState()
        //gameState.fenPosition = getGameStateAsFEN()
    }

//    fun kingInCheck(): MutableState<Boolean> {
//        return kingInCheck
//    }

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

//    fun getChecksOnKing(): MutableList<Square> {
//        return checksOnKing
//    }

    private fun setChecksOnKing() {
        checksOnKing.clear()
        for (piece in piecesOnBoard) {
            if (piece.color != playerTurn.value) {
                when (piece.piece) {
                    "queen" -> {
                        checksOnKing.addAll(
                            Queen().xRayAttacks(
                                piece,
                                occupiedSquares,
                                true,
                                squaresToBlock
                            )
                        )
                    }
                    "rook" -> {
                        checksOnKing.addAll(
                            Rook().xRayAttacks(
                                piece,
                                occupiedSquares,
                                true,
                                squaresToBlock
                            )
                        )
                    }
                    "bishop" -> {
                        checksOnKing.addAll(
                            Bishop().xRayAttacks(
                                piece,
                                occupiedSquares,
                                true,
                                squaresToBlock
                            )
                        )
                    }
                }
            }
        }
    }

//    fun getAttacks(): MutableList<Square> {
//        return attacks
//    }

    private fun checkAttacks() {
        piecesCheckingKing.clear()
        attacks.clear()
        val piecesCheckingKingTemp = mutableListOf<Square>()
        for (piece in piecesOnBoard) {
            if (piece.color != playerTurn.value) {
                if (piece.piece == "pawn") {
                    val pawnAttacks = Pawn().pawnAttacks(piece)
                    attacks.addAll(pawnAttacks)
                    for (square in pawnAttacks) {
                        if (square == kingSquare().value && !piecesCheckingKingTemp.contains(piece.square)) {
                            piecesCheckingKingTemp.add(piece.square)
                        }
                    }
                } else {
                    for (square in checkLegalMoves(piece, true)) {
                        if (square == kingSquare().value && !piecesCheckingKingTemp.contains(piece.square)) {
                            piecesCheckingKingTemp.add(piece.square)
                        }
                        attacks.add(square)
                    }
                }
            }
        }
        squaresToBlock.clear()
        setXRayAttacks()
        piecesCheckingKing.addAll(piecesCheckingKingTemp)
        kingInCheck.value = piecesCheckingKing.isNotEmpty()
        if (kingInCheck.value) {
            setChecksOnKing()
            setSquaresToBlock()
        }
    }

    private fun checkIfKingOrRooksMoved(piece: ChessPiece) {
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

    fun castleKingSide(): Boolean {
        if (playerTurn.value == "white") {
            return whiteCanCastleKingSide.value
        }
        return blackCanCastleKingSide.value
    }

    fun castleQueenSide(): Boolean {
        if (playerTurn.value == "white") {
            return whiteCanCastleQueenSide.value
        }
        return blackCanCastleQueenSide.value
    }

    fun undoMove() {
        if (previousGameStates.size > 1) {
            previousGameStates.removeAt(previousGameStates.size - 1)
            setPositionFromFen(previousGameStates[previousGameStates.size - 1].fenPosition)
            currentSquare.value = previousGameStates[previousGameStates.size - 1].currentSquare
            previousSquare.value = previousGameStates[previousGameStates.size - 1].previousSquare
        }
    }

    fun previousGameState(index: Int) {
        setPositionFromFen(previousGameStates[index].fenPosition)
        currentSquare.value = previousGameStates[index].currentSquare
        previousSquare.value = previousGameStates[index].previousSquare

    }

    fun nextGameState(index: Int) {
        setPositionFromFen(previousGameStates[index].fenPosition)
        currentSquare.value = previousGameStates[index].currentSquare
        previousSquare.value = previousGameStates[index].previousSquare

    }

    private fun checkAllLegalMoves() {
        allLegalMoves.clear()
        for (piece in piecesOnBoard) {
            if (piece.color == playerTurn.value) {
                val legalMoves = mutableListOf<Square>()
                for (move in checkLegalMoves(piece, false)) {
                    allLegalMoves.add(move)
                    legalMoves.add(move)
                }
                mapOfPiecesAndTheirLegalMoves[piece] = legalMoves
            }
        }
    }

    fun movePiece(newSquare: Square, piece: ChessPiece, depth: Int) {
        val notation = Notation(currentNotation, newSquare)
        notation.piece(piece, piecesOnBoard, mapOfPiecesAndTheirLegalMoves)
        fiftyMoveCount.value = 0
        if (previousGameStates.isEmpty()) {
            previousGameStates.add(
                GameState(
                    previousSquare.value,
                    currentSquare.value,
                    getGameStateAsFEN()
                )
            )
        }
        if (occupiedSquares.containsKey(newSquare)) {
            occupiedSquares[newSquare]?.let {
                capturedPieces.add(it)
            }
            removePiece(newSquare)
            captureSound.value = true
            if (depth == 1) {
                captures.value += 1
            }
            notation.capture()
        } else {
            pieceSound.value = true
            notation.square(false)
        }

        val previousPieceSquare = Square(piece.square.rank, piece.square.file)
        occupiedSquares.remove(piece.square)
        piecesOnBoard.remove(piece)
        previousSquare.value = previousPieceSquare
        currentSquare.value = newSquare
    }

    fun promotion(newSquare: Square, promotionSelection: ChessPiece, depth: Int) {
        val notation = Notation(currentNotation, newSquare)
        notation.promotion(promotionSelection)
        if (depth == 1) {
            promotions.value += 1
        }
        occupiedSquares[newSquare] = promotionSelection
        piecesOnBoard.add(promotionSelection)
        //setCurrentSquare(piece.square)
        if (playerTurn.value == "white") {
            playerTurn.value = "black"
        } else {
            playerTurn.value = "white"
        }
        checkAttacks()
        previousGameStates.add(
            GameState(
                previousSquare.value,
                currentSquare.value,
                getGameStateAsFEN()
            )
        )
        checkAllLegalMoves()
        checkIfGameOver()
        //setPositionFromFen(getGameStateAsFEN())
        if (kingInCheck.value) {
            checkSound.value = true
            if (depth == 1) {
                checks.value += 1
            }

        }
        notation.checkmateOrCheck(checkmate.value, kingInCheck.value)
        annotations.add(currentNotation.toString())
        //annotations.add(currentNotation.toString())
        currentNotation.clear()
    }

    private fun changePieceSquare(piece: ChessPiece, newSquare: Square): ChessPiece {
        piece.square = newSquare
        return piece
//        val updatedPiece = piece.copy(square = newSquare)
//        piecesOnBoard[piecesOnBoard.indexOf(piece)] = updatedPiece
//        return updatedPiece
    }

    private fun removePiece(square: Square) {
        println(piecesOnBoard.remove(occupiedSquares.remove(square)))
    }

    fun playerMove() {

    }

    fun checkIfCastled(
        piece: ChessPiece,
        newSquare: Square,
        depth: Int,
        castled: MutableState<Boolean>,
        notation: Notation
    ) {
        //Castling
        if (piece.piece == "king") {
            if (castleKingSide()) {
                if (newSquare.file == piece.square.file + 2) {
                    val rook: ChessPiece =
                        occupiedSquares[Square(newSquare.rank, newSquare.file + 1)]!!
                    occupiedSquares.remove(Square(newSquare.rank, newSquare.file + 1))
                    changePieceSquare(rook, Square(newSquare.rank, newSquare.file - 1))
                    occupiedSquares[rook.square] = rook
                    if (depth == 1) {
                        castles.value += 1
                    }
                    //castlingSound.value = true
                    castled.value = true
                    notation.castleKingSide()
                }
            }
            if (castleQueenSide()) {
                if (newSquare.file == piece.square.file - 2) {
                    val rook: ChessPiece =
                        occupiedSquares[Square(newSquare.rank, newSquare.file - 2)]!!
                    occupiedSquares.remove(Square(newSquare.rank, newSquare.file - 2))
                    changePieceSquare(rook, Square(newSquare.rank, newSquare.file + 1))
                    occupiedSquares[rook.square] = rook
                    if (depth == 1) {
                        castles.value += 1
                    }
                    //castlingSound.value = true
                    castled.value = true
                    notation.castleQueenSide()
                }
            }
        }
        checkIfKingOrRooksMoved(piece)
    }

    fun changePiecePosition(newSquare: Square, piece: ChessPiece, depth: Int) {
        val previousPieceSquare = piece.square
        val notation = Notation(currentNotation, newSquare)
        val castled = mutableStateOf(false)
        var capture = false
        var move = false

        fiftyMoveCount.value += 1
        val gameLogic = GameLogic()

        if (piece.piece == "pawn") {
            fiftyMoveCount.value = 0
        }
        //Castling
        checkIfCastled(piece, newSquare, depth, castled, notation)
        if (!castled.value) {
            notation.piece(piece, piecesOnBoard, mapOfPiecesAndTheirLegalMoves)
        }

        //Remove Defender
        if (occupiedSquares.containsKey(newSquare)) {
            fiftyMoveCount.value = 0
            if (depth == 1) {
                captures.value += 1
            }
            occupiedSquares[newSquare]?.let {
                capturedPieces.add(it)
            }
            removePiece(newSquare)
            //captureSound.value = true
            capture = true
            notation.capture()
        } else if (gameLogic.isEnPassant(
                previousSquare.value,
                currentSquare.value,
                newSquare,
                occupiedSquares,
                piece,
                getKingSquare(),
                squaresToBlock
            )
        ) {
            fiftyMoveCount.value = 0
            if (depth == 1) {
                enPassants.value += 1
                captures.value += 1
            }
            occupiedSquares[currentSquare.value]?.let { capturedPieces.add(it) }
            removePiece(currentSquare.value)
            capture = true
            //captureSound.value = true
            notation.capture()
        } else {
            if (!castled.value) {
                notation.square(false)
                move = true
                //pieceSound.value = true
            }
        }

        occupiedSquares.remove(previousPieceSquare)
        changePieceSquare(piece, newSquare)
        occupiedSquares[newSquare] = piece
        previousSquare.value = previousPieceSquare
        currentSquare.value = newSquare
        if (piece.color == "white") {
            if (piece.piece == "king") {
                whiteKingSquare.value = newSquare
            }
            playerTurn.value = "black"
            checkAttacks()
        } else {
            if (piece.piece == "king") {
                blackKingSquare.value = newSquare
            }
            playerTurn.value = "white"
            checkAttacks()
        }
        previousGameStates.add(
            GameState(
                previousSquare.value,
                currentSquare.value,
                getGameStateAsFEN()
            )
        )
        setPositionFromFen(getGameStateAsFEN())
        if (kingInCheck.value) {
            checkSound.value = true
        } else {
            if (castled.value) {
                castlingSound.value = true
            } else if (capture) {
                captureSound.value = true
            } else {
                pieceSound.value = true
            }
        }

        if (kingInCheck.value && depth == 1) {
            checks.value += 1
        }

        checkIfGameOver()

        notation.checkmateOrCheck(checkmate.value, kingInCheck.value)
        annotations.add(currentNotation.toString())
        currentNotation.clear()
    }

    private fun checkIfGameOver() {
        val endOfGameConditions = EndOfGameConditions(gameEndSound)
        checkAllLegalMoves()
        endOfGameConditions.checkCheckmate(allLegalMoves, kingInCheck.value, checkmate)
        endOfGameConditions.checkStalemate(allLegalMoves, kingInCheck.value, stalemate)
        endOfGameConditions.checkInsufficientMaterial(piecesOnBoard, insufficientMaterial)
        endOfGameConditions.checkThreefoldRepetition(previousGameStates, threeFoldRepetition)
        endOfGameConditions.checkFiftyMoveCount(allLegalMoves, fiftyMoveCount.value, fiftyMoveRule)
    }

    private fun addNotation() {

    }

//    fun getPiecesOnBoard(): MutableList<ChessPiece> {
//        return piecesOnBoard
//    }
//
//    fun getHashMap(): MutableMap<Square, ChessPiece> {
//        return occupiedSquares
//    }
//
//    fun getPreviousSquare(): MutableState<Square> {
//        return previousSquare
//    }

//    private fun setPreviousSquare(square: Square) {
//        previousSquare.value = square
//    }

//    fun getCurrentSquare(): MutableState<Square> {
//        return currentSquare
//    }

//    private fun setCurrentSquare(square: Square) {
//        currentSquare.value = square
//    }
//
//    private fun setPlayerTurn(turn: String) {
//        playerTurn.value = turn
//    }
//
//    fun getPlayerTurn(): MutableState<String> {
//        return playerTurn
//    }
//
//    fun getXRayAttacks(): MutableList<Square> {
//        return xRayAttacks
//    }

    private fun setXRayAttacks() {
        xRayAttacks.clear()
        piecesOnBoard.forEach() { piece ->
            if (piece.color != playerTurn.value) {
                when (piece.piece) {
                    "queen" -> {
                        xRayAttacks.addAll(
                            Queen().xRayAttacks(
                                piece,
                                occupiedSquares,
                                false,
                                squaresToBlock
                            )
                        )
                    }
                    "rook" -> {
                        xRayAttacks.addAll(
                            Rook().xRayAttacks(
                                piece,
                                occupiedSquares,
                                false,
                                squaresToBlock
                            )
                        )
                    }
                    "bishop" -> {
                        xRayAttacks.addAll(
                            Bishop().xRayAttacks(
                                piece,
                                occupiedSquares,
                                false,
                                squaresToBlock
                            )
                        )
                    }
                }
            }
        }
    }

    private fun setSquaresToBlock(): MutableList<Square> {
        for (square in checksOnKing) {
            if (!attacks.contains(square)) {
                squaresToBlock.remove(square)
            }
        }
        return squaresToBlock
    }

//    fun getSquaresToBlock(): MutableList<Square> {
//        return squaresToBlock
//    }
//
//    fun getLegalMoves(piece: ChessPiece): MutableList<Square>? {
//        return mapOfPiecesAndTheirLegalMoves[piece]
//    }

    fun checkLegalMoves(piece: ChessPiece, checkDefendedPieces: Boolean): List<Square> {
        return Piece(
            piece,
            occupiedSquares,
            squaresToBlock,
            attacks,
            castleKingSide(),
            castleQueenSide(),
            xRayAttacks,
            getKingSquare(),
            checksOnKing,
            piecesCheckingKing,
            checkDefendedPieces,
            previousSquare.value,
            currentSquare.value
        ).moves()
    }
}