package com.gomezdevlopment.chessnotationapp.model.repositories

import android.os.CountDownTimer
import androidx.compose.runtime.*
import androidx.core.os.persistableBundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.GameState
import com.gomezdevlopment.chessnotationapp.model.data_classes.OnlineGame
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.game_logic.*
import com.gomezdevlopment.chessnotationapp.model.pieces.*
import com.gomezdevlopment.chessnotationapp.model.utils.FirestoreGameInteraction
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.gameDocumentReference
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.user
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.userColor
import com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements.formatTime
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameRepository() : ViewModel() {
    var piecesOnBoard: MutableList<ChessPiece> = mutableStateListOf()
    var capturedPieces: MutableList<ChessPiece> = mutableStateListOf()
    var occupiedSquares: MutableMap<Square, ChessPiece> = mutableMapOf()

    //var mapOfPiecesAndTheirLegalMoves: MutableMap<ChessPiece, MutableList<Square>> =
    // mutableMapOf()
    var previousSquare: MutableState<Square> = mutableStateOf(Square(10, 10))
    var currentSquare: MutableState<Square> = mutableStateOf(Square(10, 10))
    var playerTurn: MutableState<String> = mutableStateOf("white")
    private var whiteKingSquare: MutableState<Square> = mutableStateOf(Square(0, 4))
    private var blackKingSquare: MutableState<Square> = mutableStateOf(Square(7, 4))
    var xRayAttacks: MutableList<Square> = mutableListOf()
    var attacks: MutableList<Square> = mutableListOf()
    var pinnedPieces = mutableListOf<ChessPiece>()
    var allLegalMoves: MutableList<Square> = mutableListOf()
    private var whiteCanCastleKingSide: MutableState<Boolean> = mutableStateOf(false)
    private var whiteCanCastleQueenSide: MutableState<Boolean> = mutableStateOf(false)
    private var blackCanCastleKingSide: MutableState<Boolean> = mutableStateOf(false)
    private var blackCanCastleQueenSide: MutableState<Boolean> = mutableStateOf(false)
    var kingInCheck: MutableState<Boolean> = mutableStateOf(false)
    private var piecesCheckingKing = mutableListOf<ChessPiece>()
    private var checksOnKing = mutableListOf<Square>()
    private var previousGameStates = mutableListOf<GameState>()
    var annotations: MutableList<String> = mutableStateListOf("start:")
    private var currentNotation: StringBuilder = StringBuilder("")

    var initialTime = mutableStateOf(300000L)
    var whiteTimer = mutableStateOf(initialTime.value)
    var blackTimer = mutableStateOf(initialTime.value)
    val blackProgress = MutableStateFlow(1.00F)
    val whiteProgress = MutableStateFlow(1.00F)
    var whiteTime = MutableStateFlow(formatTime(whiteTimer.value))
    val whiteTimeIsPlaying = MutableStateFlow(false)
    val blackTime = MutableStateFlow(formatTime(blackTimer.value))
    val blackTimeIsPlaying = MutableStateFlow(false)
    var countDownTimer: MutableState<CountDownTimer?> = mutableStateOf(null)


    val endOfGameResult = mutableStateOf("")
    val endOfGameMessage = mutableStateOf("")
    val endOfGame = mutableStateOf(false)
    val endOfGameCardVisible = mutableStateOf(false)

//    var whiteStartingTime =  600000L
//    var blackStartingTime = 600000L

    //Testing Variables
    var checks = mutableStateOf(0)
    var captures = mutableStateOf(0)
    var castles = mutableStateOf(0)
    var enPassants = mutableStateOf(0)
    var promotions = mutableStateOf(0)

    //Enf of Game Booleans
    var resignation: MutableState<Boolean> = mutableStateOf(false)
    var checkmate: MutableState<Boolean> = mutableStateOf(false)
    var stalemate: MutableState<Boolean> = mutableStateOf(false)
    var insufficientMaterial: MutableState<Boolean> = mutableStateOf(false)
    var threeFoldRepetition: MutableState<Boolean> = mutableStateOf(false)
    var fiftyMoveRule: MutableState<Boolean> = mutableStateOf(false)
    private var fiftyMoveCount = mutableStateOf(0)

    //Sound FX Booleans
    var pieceSound: MutableState<Boolean> = mutableStateOf(false)
    var checkSound: MutableState<Boolean> = mutableStateOf(false)
    var captureSound: MutableState<Boolean> = mutableStateOf(false)
    var castlingSound: MutableState<Boolean> = mutableStateOf(false)
    var gameEndSound: MutableState<Boolean> = mutableStateOf(false)

    var selectedNotationIndex: MutableState<Int> = mutableStateOf(0)
    val openDrawOfferedDialog = mutableStateOf(false)

    val pinnedPiecesFromPreviousTurns = mutableListOf<List<ChessPiece>>(listOf())

    init {
        //initialPosition()
        //testPositionKiwipete()
        //testPosition3()
        testPosition4()
        checkAllLegalMoves()
    }

    fun resetGame(time: Long, isOnline: Boolean) {
        previousGameStates.clear()
        checkmate.value = (false)
        stalemate.value = (false)
        insufficientMaterial.value = (false)
        threeFoldRepetition.value = (false)
        fiftyMoveRule.value = (false)
        fiftyMoveCount.value = (0)
        previousSquare.value = (Square(10, 10))
        currentSquare.value = Square(10, 10)
        playerTurn.value = "white"
        annotations.clear()
        annotations.add("start:")
        currentNotation = StringBuilder("")
        endOfGame.value = false
        endOfGameCardVisible.value = false
        whiteTimer.value = time
        blackTimer.value = time
        blackProgress.value = 1.00F
        whiteProgress.value = 1.00F
        whiteTime.value = formatTime(time)
        whiteTimeIsPlaying.value = false
        blackTime.value = formatTime(time)
        blackTimeIsPlaying.value = false
        initialTime.value = time
        openDrawOfferedDialog.value = false
        capturedPieces.clear()
        initialPosition()
        checkAllLegalMoves()
        if (isOnline) {
            snapshotListener()
        } else {
            gameDocumentReference = null
        }
    }

    private fun snapshotListener() {
        if (gameDocumentReference != null) {
            gameDocumentReference?.addSnapshotListener { value, error ->
                val onlineGame = value?.toObject(OnlineGame::class.java)
                if (onlineGame?.resignation != "") {
                    resignation(onlineGame?.resignation)
                }
                val drawOffer = onlineGame?.drawOffer
                if (drawOffer != null) {
                    if (drawOffer == "white" || drawOffer == "black") {
                        if (drawOffer != userColor) {
                            openDrawOfferedDialog.value = true
                        }
                    }
                    if (drawOffer == "accept") {
                        drawAccepted()
                    }
                }
                //Move
                val previousMoveString = onlineGame?.previousMove.toString()
                if (previousMoveString != "") {
                    val piece = getPieceFromDocument(previousMoveString)
                    val square: Square = getSquareFromDocument(previousMoveString)
                    if (piece != null && piece.color != userColor) {
                        if (piece.piece == "pawn" && (square.rank == 0 || square.rank == 7)) {
                            movePiece(square, piece, 0)
                            getPromotionSelectionFromDocument(previousMoveString, square)
                        } else {
                            changePiecePosition(square, piece, 0)
                        }
                    }
                }
            }
        }
    }

    private fun setEndOfGameValues(result: String, message: String) {
        endOfGameResult.value = result
        endOfGameMessage.value = message
        endOfGame.value = true
        endOfGameCardVisible.value = true
        gameEndSound.value = true
    }

    private fun drawAccepted() {
        setEndOfGameValues("Draw", "by Agreement")
        FirestoreGameInteraction().incrementDraws()
    }

    private fun resignation(playerColor: String?) {
        if (playerColor != null) {
            when (playerColor) {
                "white" -> {
                    if (userColor == "white") {
                        FirestoreGameInteraction().incrementLosses()
                    }
                    if (userColor == "black") {
                        FirestoreGameInteraction().incrementWins()
                    }
                    setEndOfGameValues("Black Wins!", "by Resignation")
                }
                "black" -> {
                    if (userColor == "black") {
                        FirestoreGameInteraction().incrementLosses()
                    }
                    if (userColor == "white") {
                        FirestoreGameInteraction().incrementWins()
                    }
                    setEndOfGameValues("White Wins!", "by Resignation")
                }
            }
        }
    }

    private fun getPieceFromDocument(previousMoveString: String): ChessPiece? {
        val pieceRank = previousMoveString[0].digitToInt()
        val pieceFile = previousMoveString[1].digitToInt()
        val square = Square(pieceRank, pieceFile)
        if (occupiedSquares.containsKey(square)) {
            return occupiedSquares[square]
        }
        return null
    }

    private fun getSquareFromDocument(previousMoveString: String): Square {
        val squareRank = previousMoveString[2].digitToInt()
        val squareFile = previousMoveString[3].digitToInt()
        return Square(squareRank, squareFile)
    }

    private fun getPromotionSelectionFromDocument(
        previousMoveString: String,
        newSquare: Square
    ) {
        var piece: ChessPiece? = null
        when (playerTurn.value) {
            "black" -> {
                if (previousMoveString.contains("queen"))
                    piece = ChessPieces().blackQueen(newSquare.rank, newSquare.file)
                if (previousMoveString.contains("rook"))
                    piece = ChessPieces().blackRook(newSquare.rank, newSquare.file)
                if (previousMoveString.contains("bishop"))
                    piece = ChessPieces().blackBishop(newSquare.rank, newSquare.file)
                if (previousMoveString.contains("knight"))
                    piece = ChessPieces().blackKnight(newSquare.rank, newSquare.file)
            }
            "white" -> {
                if (previousMoveString.contains("queen"))
                    piece = ChessPieces().whiteQueen(newSquare.rank, newSquare.file)
                if (previousMoveString.contains("rook"))
                    piece = ChessPieces().whiteRook(newSquare.rank, newSquare.file)
                if (previousMoveString.contains("bishop"))
                    piece = ChessPieces().whiteBishop(newSquare.rank, newSquare.file)
                if (previousMoveString.contains("knight"))
                    piece = ChessPieces().whiteKnight(newSquare.rank, newSquare.file)
            }
        }
        if (piece != null) {
            promotion(newSquare, piece, 0)
        }
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

    fun setPositionFromFen(fen: String) {
        occupiedSquares.clear()
        piecesOnBoard.clear()
        //println(fen)
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
//        val names = mutableListOf<Map<Square, String>>()
//        val piecesOnBoardTemp = mutableListOf<ChessPiece>()
//        piecesOnBoardTemp.addAll(piecesOnBoard)
//
//        piecesOnBoard.forEachIndexed() { index, piece ->
//            names.add(mapOf(piece.square to piece.color))
//        }
//
//        val fenMap = mutableListOf<Map<Square, String>>()
//        piecesOnBoardFromFENPosition.forEach {
//            fenMap.add(mapOf(it.square to it.color))
//        }
//
//        piecesOnBoardFromFENPosition.forEach { piece ->
//            if (!names.contains(mapOf(piece.square to piece.color))) {
//                piecesOnBoard.add(piece)
//            }
//        }
//
//        piecesOnBoardTemp.forEach() { piece ->
//            if (!fenMap.contains(mapOf(piece.square to piece.color))) {
//
//                piecesOnBoard.remove(piece)
//            }
//        }

        //occupiedSquares.clear()

        piecesOnBoard.addAll(piecesOnBoardFromFENPosition)
        piecesOnBoard.forEach {
            occupiedSquares[it.square] = it
        }
    }

    fun setPreviousPosition(state: State) {
        //piecesOnBoard[state.index].square = state.square
        state.piece.square = state.square
        if (state.capture) {
            piecesOnBoard.add(capturedPieces.last())
            capturedPieces.removeLast()
        }

        if(state.whiteKingSquare == Square(0, 4)){
            if(currentSquare.value == Square(0,6)){
                val rook = occupiedSquares[Square(0,5)]
                if (rook != null) {
                    occupiedSquares.remove(rook.square)
                    rook.square = Square(0,7)
                    occupiedSquares[rook.square] = rook
                }
            }

            if(currentSquare.value == Square(0,2)){
                val rook = occupiedSquares[Square(0,3)]
                if (rook != null) {
                    occupiedSquares.remove(rook.square)
                    rook.square = Square(0,0)
                    occupiedSquares[rook.square] = rook
                }
            }
        }

        if(state.blackKingSquare == Square(7, 4)){
            if(currentSquare.value == Square(7,6)){
                val rook = occupiedSquares[Square(7,5)]
                if (rook != null) {
                    occupiedSquares.remove(rook.square)
                    rook.square = Square(7,7)
                    occupiedSquares[rook.square] = rook
                }
            }

            if(currentSquare.value == Square(7,2)){
                val rook = occupiedSquares[Square(7,3)]
                if (rook != null) {
                    occupiedSquares.remove(rook.square)
                    rook.square = Square(7,0)
                    occupiedSquares[rook.square] = rook
                }
            }
        }

        playerTurn.value = state.playerTurn
        whiteCanCastleKingSide.value = state.whiteCanCastleKingSide
        whiteCanCastleQueenSide.value = state.whiteCanCastleQueenSide
        blackCanCastleKingSide.value = state.blackCanCastleKingSide
        blackCanCastleQueenSide.value = state.blackCanCastleQueenSide
        whiteKingSquare.value = state.whiteKingSquare
        blackKingSquare.value = state.blackKingSquare
        currentSquare.value = state.currentSquare
        previousSquare.value = state.previousSquare
        occupiedSquares.clear()
        piecesOnBoard.forEach {
            occupiedSquares[it.square] = it
        }
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
        piecesOnBoard.clear()
        occupiedSquares.clear()
        setPositionFromFen("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ")
        addInitialGameState()
    }

    fun testPosition3() {
        piecesOnBoard.clear()
        occupiedSquares.clear()
        setPositionFromFen("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -")
        addInitialGameState()
    }

    fun testPosition4() {
        piecesOnBoard.clear()
        occupiedSquares.clear()
        setPositionFromFen("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq -")
        addInitialGameState()
    }

    fun initialPosition() {
        piecesOnBoard.clear()
        occupiedSquares.clear()
        setPositionFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 ")
        addInitialGameState()
    }

    fun promotionPosition() {
        piecesOnBoard.clear()
        occupiedSquares.clear()
        setPositionFromFen("2r5/KP6/8/8/8/8/6pk/8 w - - 0 1")
        addInitialGameState()
    }

    fun kingSquare(): MutableState<Square> {
        if (playerTurn.value == "white") {
            return whiteKingSquare
        }
        return blackKingSquare
    }

    private fun getEnemyKingSquare(): Square {
        if (playerTurn.value == "white") {
            return blackKingSquare.value
        }
        return whiteKingSquare.value
    }

    private fun setXRayAttacks(
        list: MutableList<Square>,
        lookForChecks: Boolean
    ) {
        list.clear()
        piecesOnBoard.forEach() { piece ->
            if (piece.color != playerTurn.value)
                xRayAttacks.addAll(piece.xRays)
        }
    }

//    private fun checkAttacks() {
//        piecesCheckingKing.clear()
//        attacks.clear()
//        val attackTemp = mutableListOf<Square>()
//        for (piece in piecesOnBoard) {
//            attackTemp.clear()
//            if (piece.color != playerTurn.value) {
//                attacks.addAll(piece.attacks)
//                attackTemp.addAll(piece.attacks)
//                if (attackTemp.contains(kingSquare().value)) {
//                    piecesCheckingKing.add(piece.square)
//                }
//            }
//        }
//        squaresToBlock.clear()
//        setXRayAttacks(xRayAttacks, false)
//        kingInCheck.value = piecesCheckingKing.isNotEmpty()
//        if (kingInCheck.value) {
//            setSquaresToBlock()
//        }
//    }

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

    fun undoMove() {
//        previousGameStates.removeLast()
//        setPositionFromFen(previousGameStates.last().fenPosition)
//        currentSquare.value = previousGameStates.last().currentSquare
//        previousSquare.value = previousGameStates.last().previousSquare
//        pinnedPiecesFromPreviousTurns.removeLast()
//        println(pinnedPiecesFromPreviousTurns)
        setPreviousPosition(states.last())
        states.removeLast()
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


        setXRayAttacks(xRayAttacks, false)
        kingInCheck.value = piecesCheckingKing.isNotEmpty()
        piecesOnBoard.forEach { piece ->
            if (piece.color == playerTurn.value) {
                checkLegalMoves(piece, getEnemyKingSquare())
                allLegalMoves.addAll(piece.legalMoves)
            }
        }
    }

    fun movePiece(newSquare: Square, piece: ChessPiece, depth: Int) {
        val notation = Notation(currentNotation, newSquare)
        notation.piece(piece, piecesOnBoard)
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
        val string = convertPromotionToString(previousSquare.value, promotionSelection.square)
        viewModelScope.launch {
            FirestoreGameInteraction().writePromotion(playerTurn.value, string, promotionSelection)
        }
        val notation = Notation(currentNotation, newSquare)
        notation.promotion(promotionSelection)
        if (depth == 1) {
            promotions.value += 1
        }
        occupiedSquares[newSquare] = promotionSelection
        piecesOnBoard.add(promotionSelection)
        if (playerTurn.value == "white") {
            playerTurn.value = "black"
        } else {
            playerTurn.value = "white"
        }
        // checkAttacks()
        previousGameStates.add(
            GameState(
                previousSquare.value,
                currentSquare.value,
                getGameStateAsFEN()
            )
        )
        checkIfGameOver()
        setPositionFromFen(getGameStateAsFEN())
        checkAllLegalMoves()
        if (kingInCheck.value) {
            checkSound.value = true
            if (depth == 1) {
                checks.value += 1
            }

        }
        notation.checkmateOrCheck(checkmate.value, kingInCheck.value)
        annotations.add(currentNotation.toString())
        currentNotation.clear()
    }


    private fun changePieceSquare(piece: ChessPiece, newSquare: Square): ChessPiece {
        piece.square = newSquare
        return piece
    }

    private fun removePiece(square: Square) {
        piecesOnBoard.remove(occupiedSquares.remove(square))
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
                    if (depth == 1) {
                        castles.value += 1
                    }
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
                    if (depth == 1) {
                        castles.value += 1
                    }
                    //castlingSound.value = true
                    return ("0-0-0")
                }
            }
        }
        checkIfKingOrRooksMoved(piece)
        return ""
    }

    private fun convertMoveToStringOfRanksAndFiles(piece: ChessPiece, square: Square): String {
        return "${piece.square.rank}${piece.square.file}${square.rank}${square.file}"
    }

    private fun convertPromotionToString(previousSquare: Square, square: Square): String {
        return "${previousSquare.rank}${previousSquare.file}${square.rank}${square.file}"
    }

    fun changePiecePosition(newSquare: Square, piece: ChessPiece, depth: Int) {
        val string = convertMoveToStringOfRanksAndFiles(piece, newSquare)
        val turn: String = playerTurn.value
        viewModelScope.launch {
            FirestoreGameInteraction().writeMove(turn, string)
        }
        val previousPieceSquare = piece.square
        val notation = Notation(currentNotation, newSquare)
        val castled = mutableStateOf(false)
        val capture = mutableStateOf(false)

        fiftyMoveCount.value += 1
        if (piece.piece == "pawn") {
            fiftyMoveCount.value = 0
        }

        //Castling
        when (checkIfCastled(piece, newSquare, depth)) {
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
            fiftyMoveCount.value = 0
            occupiedSquares[newSquare]?.let {
                capturedPieces.add(it)
            }
            removePiece(newSquare)
            capture.value = true
            notation.capture()
        } else if (isEnPassant(piece, newSquare)) {
            fiftyMoveCount.value = 0
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
        //checkAttacks()
        checkIfGameOver()
        if (!gameEndSound.value) {
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
        notation.checkmateOrCheck(checkmate.value, kingInCheck.value)
        annotations.add(currentNotation.toString())
        currentNotation.clear()
        selectedNotationIndex.value += 1
        startStopClocks()
    }

    fun makeMove(pieceCopy: ChessPiece, newSquare: Square, depth: Int, promotion: String) {
        var piece = pieceCopy
        if (occupiedSquares.containsKey(pieceCopy.square)) {
            piece = occupiedSquares[pieceCopy.square]!!
        }
        val previousPieceSquare = piece.square

        if (promotion != "") {
            piece.piece = promotion
        }

        var pieceCaptured = false
        //Remove Defender
        if (occupiedSquares.containsKey(newSquare)) {
            occupiedSquares[newSquare]?.let {
                capturedPieces.add(it)
                //println("capture")
                pieceCaptured = true
            }
            removePiece(newSquare)
        } else if (isEnPassant(piece, newSquare)) {
            occupiedSquares[currentSquare.value]?.let {
                capturedPieces.add(it)
                //println("capture")
                //println("en passant")
                pieceCaptured = true
            }
            removePiece(currentSquare.value)
        }


        val index = piecesOnBoard.indexOf(piece)
        states.add(
            State(
                previousPieceSquare, pieceCaptured, index, playerTurn.value,
                whiteCanCastleKingSide.value,
                whiteCanCastleQueenSide.value,
                blackCanCastleKingSide.value,
                blackCanCastleQueenSide.value,
                whiteKingSquare.value,
                blackKingSquare.value,
                previousSquare.value,
                currentSquare.value,
                piece
            )
        )

        previousGameStates.add(
            GameState(
                previousSquare.value,
                currentSquare.value,
                getGameStateAsFEN()
            )
        )

        checkIfCastled(piece, newSquare, depth)
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
        } else {
            if (piece.piece == "king") {
                blackKingSquare.value = newSquare
            }
            playerTurn.value = "white"
        }


        //setPreviousPosition(getGameStateAsFEN(), newSquare, pieceCaptured, index)
        //setPositionFromFen(getGameStateAsFEN())
        //checkAllLegalMoves()
    }

    val states: MutableList<State> = mutableListOf()

    data class State(
        val square: Square,
        val capture: Boolean,
        val index: Int,
        val playerTurn: String,
        val whiteCanCastleKingSide: Boolean,
        val whiteCanCastleQueenSide: Boolean,
        val blackCanCastleKingSide: Boolean,
        val blackCanCastleQueenSide: Boolean,
        val whiteKingSquare: Square,
        val blackKingSquare: Square,
        val previousSquare: Square,
        val currentSquare: Square,
        val piece: ChessPiece
    )

    private fun isEnPassant(piece: ChessPiece, square: Square): Boolean {
        //If pawn moves to an empty diagonal square it should be en passant
        if (piece.square.file != square.file && piece.piece == "pawn") {
            if (!occupiedSquares.containsKey(square)) {
                return true
            }
        }
        return false
    }

    private fun checkIfGameOver() {
        val endOfGameConditions = EndOfGameConditions(gameEndSound)
        endOfGameConditions.checkCheckmate(allLegalMoves, kingInCheck.value, checkmate)
        endOfGameConditions.checkStalemate(allLegalMoves, kingInCheck.value, stalemate)
        endOfGameConditions.checkInsufficientMaterial(piecesOnBoard, insufficientMaterial)
        endOfGameConditions.checkThreefoldRepetition(previousGameStates, threeFoldRepetition)
        endOfGameConditions.checkFiftyMoveCount(allLegalMoves, fiftyMoveCount.value, fiftyMoveRule)

        if (checkmate.value) {
            var winner = "White"
            if (playerTurn.value == "white") {
                winner = "Black"
            }
            if (userColor == playerTurn.value) {
                FirestoreGameInteraction().incrementLosses()
            }
            if (userColor != playerTurn.value) {
                FirestoreGameInteraction().incrementWins()
            }
            setEndOfGameValues("Checkmate", "$winner Wins!")
        }

        if (insufficientMaterial.value) {
            FirestoreGameInteraction().incrementDraws()
            setEndOfGameValues("Draw", "by Insufficient Material")
        }

        if (stalemate.value) {
            FirestoreGameInteraction().incrementDraws()
            setEndOfGameValues("Draw", "by Stalemate")
        }

        if (threeFoldRepetition.value) {
            FirestoreGameInteraction().incrementDraws()
            setEndOfGameValues("Draw", "by Threefold Repetition")
        }

        if (fiftyMoveRule.value) {
            FirestoreGameInteraction().incrementDraws()
            setEndOfGameValues("Draw", "by The Fifty Move Rule")
        }
    }

    fun timeout() {
        var winner = "White"
        if (playerTurn.value == "white") {
            winner = "Black"
        }

        endOfGameResult.value = "$winner Wins!"
        endOfGameMessage.value = "on Time"
        if (EndOfGameConditions(gameEndSound).checkIfPlayerHasInsufficientMaterial(
                piecesOnBoard,
                winner.lowercase()
            )
        ) {
            FirestoreGameInteraction().incrementDraws()
            endOfGameResult.value = "Draw"
            endOfGameMessage.value = "by Time Out vs Insufficient Material"
        } else {
            if (userColor == playerTurn.value) {
                FirestoreGameInteraction().incrementLosses()
            }
            if (userColor != playerTurn.value) {
                FirestoreGameInteraction().incrementWins()
            }
        }
        endOfGame.value = true
        endOfGameCardVisible.value = true
    }

    private fun addNotation() {

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

    private fun startStopClocks() {
        when (playerTurn.value) {
            "white" -> {
                Clock().pauseTimer(
                    blackTime,
                    blackProgress,
                    blackTimer,
                    countDownTimer.value
                )
                Clock().startTimer(
                    whiteTime,
                    whiteProgress,
                    whiteTimer,
                    initialTime.value,
                    this,
                    countDownTimer
                )
            }
            "black" -> {
                Clock().pauseTimer(
                    whiteTime,
                    whiteProgress,
                    whiteTimer,
                    countDownTimer.value
                )
                Clock().startTimer(
                    blackTime,
                    blackProgress,
                    blackTimer,
                    initialTime.value,
                    this,
                    countDownTimer
                )
            }
        }

        if (endOfGame.value) {
            Clock().pauseTimer(whiteTime, whiteProgress, whiteTimer, countDownTimer.value)
            Clock().pauseTimer(blackTime, blackProgress, blackTimer, countDownTimer.value)
        }
    }


}