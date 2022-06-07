package com.gomezdevlopment.chessnotationapp.model

import androidx.compose.runtime.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gomezdevlopment.chessnotationapp.model.game_logic.FEN
import com.gomezdevlopment.chessnotationapp.model.game_logic.GameLogic
import com.gomezdevlopment.chessnotationapp.model.game_logic.Notation
import com.gomezdevlopment.chessnotationapp.model.pieces.*


class GameRepository() : ViewModel() {
    private var piecesOnBoard: MutableList<ChessPiece> = mutableListOf()
    private var hashMap: MutableMap<Square, ChessPiece> = HashMap()
    private var mapOfPiecesAndTheirLegalMoves: MutableMap<ChessPiece, MutableList<Square>> =
        mutableMapOf()
    private var previousSquare: MutableState<Square> = mutableStateOf(Square(10, 10))
    private var currentSquare: MutableState<Square> = mutableStateOf(Square(10, 10))
    private var playerTurn: MutableState<String> = mutableStateOf("white")
    private var squaresToBlock: MutableList<Square> = mutableListOf()
    private var whiteKingSquare: MutableState<Square> = mutableStateOf(Square(0, 4))
    private var blackKingSquare: MutableState<Square> = mutableStateOf(Square(7, 4))
    private var xRayAttacks: MutableList<Square> = mutableListOf()
    private var attacks: MutableList<Square> = mutableListOf()
    private var allLegalMoves: MutableList<Square> = mutableListOf()
    private var whiteCanCastleKingSide: MutableState<Boolean> = mutableStateOf(false)
    private var whiteCanCastleQueenSide: MutableState<Boolean> = mutableStateOf(false)
    private var blackCanCastleKingSide: MutableState<Boolean> = mutableStateOf(false)
    private var blackCanCastleQueenSide: MutableState<Boolean> = mutableStateOf(false)
    private var kingInCheck: MutableState<Boolean> = mutableStateOf(false)
    private var piecesCheckingKing = mutableListOf<Square>()
    private var checksOnKing = mutableListOf<Square>()
    private var capturedPieces: MutableList<ChessPiece> = mutableListOf()
    private var checks = mutableStateOf(0)
    private var captures = mutableStateOf(0)
    private var castles = mutableStateOf(0)
    private var enPassants = mutableStateOf(0)
    private var promotions = mutableStateOf(0)
    private var gameStateAsFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 "
    private var previousGameStates = mutableListOf<GameState>()
    private var gameState = GameState(previousSquare.value, currentSquare.value, gameStateAsFEN)
    private var checkmate: MutableState<Boolean> = mutableStateOf(false)
    private var stalemate: MutableState<Boolean> = mutableStateOf(false)
    private var insufficientMaterial: MutableState<Boolean> = mutableStateOf(false)
    private var threeFoldRepetition: MutableState<Boolean> = mutableStateOf(false)
    private var fiftyMoveRule: MutableState<Boolean> = mutableStateOf(false)
    private var fiftyMoveCount = mutableStateOf(0)

    private var pieceSound: MutableState<Boolean> = mutableStateOf(false)
    private var checkSound: MutableState<Boolean> = mutableStateOf(false)
    private var captureSound: MutableState<Boolean> = mutableStateOf(false)
    private var castlingSound: MutableState<Boolean> = mutableStateOf(false)
    private var gameEndSound: MutableState<Boolean> = mutableStateOf(false)

    private var annotations: MutableList<String> = mutableListOf()
    private var currentNotation: StringBuilder = StringBuilder("")

    fun getAnnotations(): MutableList<String> {
        return annotations
    }

    fun getPieceSound(): MutableState<Boolean> {
        return pieceSound
    }

    fun getCheckSound(): MutableState<Boolean> {
        return checkSound
    }

    fun getCaptureSound(): MutableState<Boolean> {
        return captureSound
    }

    fun getCastlingSound(): MutableState<Boolean> {
        return castlingSound
    }

    fun getGameEndSound(): MutableState<Boolean> {
        return gameEndSound
    }

    fun getCheckmate(): MutableState<Boolean> {
        return checkmate
    }

    fun getStalemate(): MutableState<Boolean> {
        return stalemate
    }

    fun getInsufficientMaterial(): MutableState<Boolean> {
        return insufficientMaterial
    }

    fun getThreeFoldRepetition(): MutableState<Boolean> {
        return threeFoldRepetition
    }

    fun getFiftyMoveRule(): MutableState<Boolean> {
        return fiftyMoveRule
    }

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

    fun getPromotions(): Int {
        return promotions.value
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
        //testPosition4()
        promotionPosition()
        //addPieces()
        //testPositionKiwipete()
        // testPosition3()
//        previousGameStates.add(GameState(previousSquare.value, currentSquare.value, gameStateAsFEN))
        initialPosition()
    }

    private fun getGameStateAsFEN(): String {
        return FEN().getGameStateAsFEN(
            hashMap,
            playerTurn,
            whiteCanCastleKingSide,
            whiteCanCastleQueenSide,
            blackCanCastleKingSide,
            blackCanCastleQueenSide
        )
    }

    private fun setPositionFromFen(fen: String) {
        piecesOnBoard.clear()
        hashMap.clear()
        piecesOnBoard.addAll(
            FEN().parseFEN(
                fen,
                playerTurn,
                whiteCanCastleKingSide,
                whiteCanCastleQueenSide,
                blackCanCastleKingSide,
                blackCanCastleQueenSide,
                whiteKingSquare,
                blackKingSquare
            )
        )
        for (piece in piecesOnBoard) {
            hashMap[piece.square] = piece
        }
        checkAttacks()
        setXRayAttacks()
        checkAllLegalMoves()
        //gameState.value.fenPosition = getGameStateAsFEN()
    }

    fun testPositionKiwipete() {
        setPositionFromFen("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ")
        gameState.fenPosition = getGameStateAsFEN()
    }

    fun testPosition3() {
        setPositionFromFen("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -")
        gameState.fenPosition = getGameStateAsFEN()
    }

    fun testPosition4() {
        setPositionFromFen("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq -")
        gameState.fenPosition = getGameStateAsFEN()
    }

    fun initialPosition() {
        setPositionFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 ")
        gameState.fenPosition = getGameStateAsFEN()
    }

    fun promotionPosition() {
        setPositionFromFen("2r5/KP6/8/8/8/8/6pk/8 w - - 0 1")
        gameState.fenPosition = getGameStateAsFEN()
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

    fun previousGameState() {
        if (previousGameStates.size > 1) {
            previousGameStates.removeAt(previousGameStates.size - 1)
            setPositionFromFen(previousGameStates[previousGameStates.size - 1].fenPosition)
            currentSquare.value = previousGameStates[previousGameStates.size - 1].currentSquare
            previousSquare.value = previousGameStates[previousGameStates.size - 1].previousSquare
        }

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

    private fun checkCheckmate() {
        if (allLegalMoves.isEmpty() && kingInCheck().value) {
            checkmate.value = true
            gameEndSound.value = true
        }
    }

    private fun checkStalemate() {
        if (allLegalMoves.isEmpty() && !kingInCheck().value) {
            stalemate.value = true
            gameEndSound.value = true
        }
    }

    private fun checkFiftyMoveCount() {
        if (allLegalMoves.isNotEmpty()) {
            if (fiftyMoveCount.value == 100) {
                fiftyMoveRule.value = true
                gameEndSound.value = true
            }
        }
    }

    private fun checkThreefoldRepetition() {
        val fenPositions = arrayListOf<String>()
        for (gameState in previousGameStates) {
            fenPositions.add(gameState.fenPosition)
        }
        var duplicatePositions: Int
        for (position in fenPositions.asReversed()) {
            duplicatePositions = 0
            for (positionToCompare in fenPositions.asReversed()) {
                if (position == positionToCompare) {
                    duplicatePositions++
                    if (duplicatePositions == 3) {
                        threeFoldRepetition.value = true
                        gameEndSound.value = true
                        break
                    }
                }
            }
            if (threeFoldRepetition.value) {
                break
            }
        }
    }

    private fun checkInsufficientMaterial() {
        if (piecesOnBoard.size == 2) {
            insufficientMaterial.value = (piecesOnBoard.size == 2)
        } else {
            var blackBishopsAndKnights = 0
            var whiteBishopsAndKnights = 0
            var onlyKnightsOrBishops = true
            if (piecesOnBoard.size < 5) {
                for (piece in piecesOnBoard) {
                    when (piece.color) {
                        "white" -> {
                            whiteBishopsAndKnights += when (piece.piece) {
                                "bishop" -> 1
                                "knight" -> 1
                                "king" -> 0
                                else -> {
                                    onlyKnightsOrBishops = false
                                    break
                                }
                            }
                        }
                        "black" -> {
                            blackBishopsAndKnights += when (piece.piece) {
                                "bishop" -> 1
                                "knight" -> 1
                                "king" -> 0
                                else -> {
                                    onlyKnightsOrBishops = false
                                    break
                                }
                            }
                        }
                    }
                }
                if (whiteBishopsAndKnights < 2 && blackBishopsAndKnights < 2 && onlyKnightsOrBishops) {
                    insufficientMaterial.value = true
                    gameEndSound.value = true
                }
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

        if (hashMap.containsKey(newSquare)) {
            hashMap[newSquare]?.let {
                capturedPieces.add(it)
            }
            piecesOnBoard.remove(hashMap[newSquare])
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
        hashMap.remove(piece.square)
        piecesOnBoard.remove(piece)
        setPreviousSquare(previousPieceSquare)
        setCurrentSquare(newSquare)
    }

    fun promotion(newSquare: Square, promotionSelection: ChessPiece, depth: Int) {
        val notation = Notation(currentNotation, newSquare)
        notation.promotion(promotionSelection)
        if (depth == 1) {
            promotions.value += 1
        }
        hashMap[newSquare] = promotionSelection
        piecesOnBoard.add(promotionSelection)
        //setCurrentSquare(piece.square)
        if (playerTurn.value == "white") {
            setPlayerTurn("black")
        } else {
            setPlayerTurn("white")
        }
        checkAttacks()
        setXRayAttacks()
        previousGameStates.add(
            GameState(
                previousSquare.value,
                currentSquare.value,
                getGameStateAsFEN()
            )
        )
        checkAllLegalMoves()
        checkCheckmate()
        checkStalemate()
        checkInsufficientMaterial()
        checkFiftyMoveCount()
        checkThreefoldRepetition()
        setPositionFromFen(getGameStateAsFEN())
        if (kingInCheck.value && depth == 1) {
            checks.value += 1
        }
        notation.checkmateOrCheck(checkmate.value, kingInCheck().value)
        annotations.add(currentNotation.toString())
        //annotations.add(currentNotation.toString())
        currentNotation.clear()
        println(annotations)
    }

    fun changePiecePosition(newSquare: Square, piece: ChessPiece, depth: Int) {
        val notation = Notation(currentNotation, newSquare)
        var castled = false

        fiftyMoveCount.value += 1
        if (previousGameStates.isEmpty()) {
            previousGameStates.add(
                GameState(
                    previousSquare.value,
                    currentSquare.value,
                    getGameStateAsFEN()
                )
            )
        }
        val gameLogic = GameLogic()

        if (piece.piece == "pawn") {
            fiftyMoveCount.value = 0
        }
        //Castling
        if (piece.piece == "king") {
            if (castleKingSide()) {
                if (newSquare.file == piece.square.file + 2) {
                    val rook: ChessPiece = hashMap[Square(newSquare.rank, newSquare.file + 1)]!!
                    hashMap.remove(Square(newSquare.rank, newSquare.file + 1))
                    rook.square = Square(newSquare.rank, newSquare.file - 1)
                    hashMap[rook.square] = rook
                    if (depth == 1) {
                        castles.value += 1
                    }
                    castlingSound.value = true
                    castled = true
                    notation.castleKingSide()
                }
            }
            if (castleQueenSide()) {
                if (newSquare.file == piece.square.file - 2) {
                    val rook: ChessPiece = hashMap[Square(newSquare.rank, newSquare.file - 2)]!!
                    hashMap.remove(Square(newSquare.rank, newSquare.file - 2))
                    rook.square = Square(newSquare.rank, newSquare.file + 1)
                    hashMap[rook.square] = rook
                    if (depth == 1) {
                        castles.value += 1
                    }
                    castlingSound.value = true
                    castled = true
                    notation.castleQueenSide()
                }
            }
        }
        checkIfKingOrRooksMoved(piece)

        if (!castled) {
            notation.piece(piece, piecesOnBoard, mapOfPiecesAndTheirLegalMoves)
        }

        //Remove Defender
        if (hashMap.containsKey(newSquare)) {
            fiftyMoveCount.value = 0
            if (depth == 1) {
                captures.value += 1
            }
            hashMap[newSquare]?.let {
                capturedPieces.add(it)
            }
            piecesOnBoard.remove(hashMap[newSquare])
            captureSound.value = true
            notation.capture()
        } else if (gameLogic.isEnPassant(
                previousSquare.value,
                currentSquare.value,
                newSquare,
                hashMap,
                piece,
                getKingSquare()
            )
        ) {
            fiftyMoveCount.value = 0
            if (depth == 1) {
                enPassants.value += 1
                captures.value += 1
            }
            hashMap[currentSquare.value]?.let { capturedPieces.add(it) }
            piecesOnBoard.remove(hashMap[currentSquare.value])
            hashMap.remove(currentSquare.value)
            captureSound.value = true
            notation.capture()
        } else {
            if (!castled) {
                notation.square(false)
                pieceSound.value = true
            }
        }

        //Check Promotion

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
        previousGameStates.add(
            GameState(
                previousSquare.value,
                currentSquare.value,
                getGameStateAsFEN()
            )
        )
        //setPositionFromFen(getGameStateAsFEN())
        if (kingInCheck.value) {
            checkSound.value = true
        }
        if (kingInCheck.value && depth == 1) {
            checks.value += 1
        }

        checkAllLegalMoves()
        checkCheckmate()
        checkStalemate()
        checkInsufficientMaterial()
        checkThreefoldRepetition()
        notation.checkmateOrCheck(checkmate.value, kingInCheck().value)
        annotations.add(currentNotation.toString())
        currentNotation.clear()
        println(annotations)
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
        for (square in checksOnKing) {
            if (attacks.contains(square)) {
                squaresToBlock.add(square)
            }
        }
        //squaresToBlock.addAll(getAttackedSquaresNearKing(attacks, kingSquare().value))
        return squaresToBlock
    }

    fun getSquaresToBlock(): MutableList<Square> {
        return squaresToBlock
    }

    fun getLegalMoves(piece: ChessPiece): MutableList<Square>? {
        return mapOfPiecesAndTheirLegalMoves[piece]
    }

    fun checkLegalMoves(piece: ChessPiece, checkDefendedPieces: Boolean): List<Square> {
        var listOfMoves = mutableListOf<Square>()
        when (piece.piece) {
            "pawn" -> {
                listOfMoves = pawnMoves(piece, checkDefendedPieces)
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
                listOfMoves = kingMoves(piece, checkDefendedPieces)
            }
            "queen" -> {
                listOfMoves = queenMoves(piece, checkDefendedPieces)
            }
        }
        return listOfMoves
    }

    private fun pawnMoves(piece: ChessPiece, checkDefendedPieces: Boolean): MutableList<Square> {
        return Pawn().moves(
            piece,
            hashMap,
            squaresToBlock,
            previousSquare.value,
            currentSquare.value,
            xRayAttacks,
            getKingSquare(),
            piecesCheckingKing,
            checkDefendedPieces
        )
    }

    private fun kingMoves(piece: ChessPiece, checkDefendedPieces: Boolean): MutableList<Square> {
        if (piece.color == "white") {
            return King().moves(
                piece,
                hashMap,
                squaresToBlock,
                attacks,
                whiteCanCastleKingSide.value,
                whiteCanCastleQueenSide.value,
                xRayAttacks,
                getKingSquare(),
                getChecksOnKing(),
                piecesCheckingKing,
                checkDefendedPieces
            )
        }
        return King().moves(
            piece,
            hashMap,
            squaresToBlock,
            attacks,
            blackCanCastleKingSide.value,
            blackCanCastleQueenSide.value,
            xRayAttacks,
            getKingSquare(),
            getChecksOnKing(),
            piecesCheckingKing,
            checkDefendedPieces
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