package com.gomezdevlopment.chessnotationapp.model.repositories

import android.os.CountDownTimer
import androidx.compose.runtime.*
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
    var mapOfPiecesAndTheirLegalMoves: MutableMap<ChessPiece, MutableList<Square>> =
        mutableMapOf()
    var previousSquare: MutableState<Square> = mutableStateOf(Square(10, 10))
    var currentSquare: MutableState<Square> = mutableStateOf(Square(10, 10))
    var playerTurn: MutableState<String> = mutableStateOf("white")
    var squaresToBlock: MutableList<Square> = mutableListOf()
    private var whiteKingSquare: MutableState<Square> = mutableStateOf(Square(0, 4))
    private var blackKingSquare: MutableState<Square> = mutableStateOf(Square(7, 4))
    var xRayAttacks: MutableList<Square> = mutableListOf()
    var attacks: MutableList<Square> = mutableListOf()
    private var allLegalMoves: MutableList<Square> = mutableListOf()
    private var whiteCanCastleKingSide: MutableState<Boolean> = mutableStateOf(false)
    private var whiteCanCastleQueenSide: MutableState<Boolean> = mutableStateOf(false)
    private var blackCanCastleKingSide: MutableState<Boolean> = mutableStateOf(false)
    private var blackCanCastleQueenSide: MutableState<Boolean> = mutableStateOf(false)
    var kingInCheck: MutableState<Boolean> = mutableStateOf(false)
    private var piecesCheckingKing = mutableListOf<Square>()
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

    init {
        initialPosition()
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
        if(isOnline){
            snapshotListener()
        }else{
            gameDocumentReference = null
        }
    }

    private fun snapshotListener() {
        if (gameDocumentReference != null) {
            gameDocumentReference?.addSnapshotListener { value, error ->
                val onlineGame = value?.toObject(OnlineGame::class.java)
                if(onlineGame?.resignation != ""){
                    resignation(onlineGame?.resignation)
                }
                val drawOffer = onlineGame?.drawOffer
                if(drawOffer != null){
                    if(drawOffer == "white" || drawOffer == "black"){
                        if(drawOffer != userColor){
                            openDrawOfferedDialog.value = true
                        }
                    }
                    if(drawOffer == "accept"){
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

    private fun setEndOfGameValues(result: String, message: String){
        endOfGameResult.value = result
        endOfGameMessage.value = message
        endOfGame.value = true
        endOfGameCardVisible.value = true
        gameEndSound.value = true
    }

    private fun drawAccepted(){
        setEndOfGameValues("Draw", "by Agreement")
        FirestoreGameInteraction().incrementDraws()
    }

    private fun resignation(playerColor: String?){
        if(playerColor != null){
            when(playerColor) {
                "white" -> {
                    if(userColor == "white"){FirestoreGameInteraction().incrementLosses()}
                    if(userColor == "black"){FirestoreGameInteraction().incrementWins()}
                    setEndOfGameValues("Black Wins!", "by Resignation")
                }
                "black" -> {
                    if(userColor == "black"){FirestoreGameInteraction().incrementLosses()}
                    if(userColor == "white"){FirestoreGameInteraction().incrementWins()}
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
                    piece = ChessPiece("black", "queen", R.drawable.ic_bq_alpha, newSquare, 9)
                if (previousMoveString.contains("rook"))
                    piece = ChessPiece("black", "rook", R.drawable.ic_br_alpha, newSquare, 5)
                if (previousMoveString.contains("bishop"))
                    piece = ChessPiece("black", "bishop", R.drawable.ic_bb_alpha, newSquare, 3)
                if (previousMoveString.contains("knight"))
                    piece = ChessPiece("black", "knight", R.drawable.ic_bn_alpha, newSquare, 3)
            }
            "white" -> {
                if (previousMoveString.contains("queen"))
                    piece = ChessPiece("white", "queen", R.drawable.ic_wq_alpha, newSquare, 9)
                if (previousMoveString.contains("rook"))
                    piece = ChessPiece("white", "rook", R.drawable.ic_wr_alpha, newSquare, 5)
                if (previousMoveString.contains("bishop"))
                    piece = ChessPiece("white", "bishop", R.drawable.ic_wb_alpha, newSquare, 3)
                if (previousMoveString.contains("knight"))
                    piece = ChessPiece("white", "knight", R.drawable.ic_wn_alpha, newSquare, 3)
            }
        }
        if (piece != null) {
            promotion(newSquare, piece, 0)
        }
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
    }

    fun testPosition3() {
        setPositionFromFen("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -")
        addInitialGameState()
    }

    fun testPosition4() {
        setPositionFromFen("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq -")
        addInitialGameState()
    }

    fun initialPosition() {
        setPositionFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 ")
        addInitialGameState()
    }

    fun promotionPosition() {
        setPositionFromFen("2r5/KP6/8/8/8/8/6pk/8 w - - 0 1")
        addInitialGameState()
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

    private fun setXRayAttacks(
        list: MutableList<Square>,
        lookForChecks: Boolean
    ) {
        list.clear()
        piecesOnBoard.forEach() { piece ->
            if (piece.color != playerTurn.value) {
                when (piece.piece) {
                    "queen" -> {
                        list.addAll(
                            Queen().xRayAttacks(
                                piece,
                                occupiedSquares,
                                lookForChecks,
                                squaresToBlock
                            )
                        )
                    }
                    "rook" -> {
                        list.addAll(
                            Rook().xRayAttacks(
                                piece,
                                occupiedSquares,
                                lookForChecks,
                                squaresToBlock
                            )
                        )
                    }
                    "bishop" -> {
                        list.addAll(
                            Bishop().xRayAttacks(
                                piece,
                                occupiedSquares,
                                lookForChecks,
                                squaresToBlock
                            )
                        )
                    }
                }
            }
        }
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
        squaresToBlock.clear()
        setXRayAttacks(xRayAttacks, false)
        piecesCheckingKing.addAll(piecesCheckingKingTemp)
        kingInCheck.value = piecesCheckingKing.isNotEmpty()
        if (kingInCheck.value) {
            setXRayAttacks(checksOnKing, true)
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

    fun setGameState(index: Int) {
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
        setPositionFromFen(getGameStateAsFEN())
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
        println(piecesOnBoard.remove(occupiedSquares.remove(square)))
    }

    fun checkIfCastled(
        piece: ChessPiece,
        newSquare: Square,
        depth: Int): String{
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
                    return("0-0")
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
                    return("0-0-0")
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
        when(checkIfCastled(piece, newSquare, depth)){
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
            notation.piece(piece, piecesOnBoard, mapOfPiecesAndTheirLegalMoves)
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
        checkIfGameOver()
        if(!gameEndSound.value){
            if (kingInCheck.value) {
                checkSound.value = true
            }
            else {
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

    fun makeMove(piece: ChessPiece, newSquare: Square, depth: Int){
        val previousPieceSquare = piece.square
        checkIfCastled(piece, newSquare, depth)
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
        } else if (isEnPassant(piece, newSquare)) {
            fiftyMoveCount.value = 0
            if (depth == 1) {
                enPassants.value += 1
                captures.value += 1
            }
            occupiedSquares[currentSquare.value]?.let { capturedPieces.add(it) }
            removePiece(currentSquare.value)
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
        if (kingInCheck.value && depth == 1) {
            checks.value += 1
        }
        setPositionFromFen(getGameStateAsFEN())
    }

    private fun isEnPassant(piece: ChessPiece, square: Square): Boolean {
        //If pawn moves to an empty diagonal square it should be en passant
        if(piece.square.file != square.file && piece.piece == "pawn"){
            if(!occupiedSquares.containsKey(square)){
                return true
            }
        }
        return false
    }

    private fun checkIfGameOver() {
        val endOfGameConditions = EndOfGameConditions(gameEndSound)
        checkAllLegalMoves()
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
            if(userColor == playerTurn.value){
                FirestoreGameInteraction().incrementLosses()
            }
            if(userColor != playerTurn.value){
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
        }else{
            if(userColor == playerTurn.value){
                FirestoreGameInteraction().incrementLosses()
            }
            if(userColor != playerTurn.value){
                FirestoreGameInteraction().incrementWins()
            }
        }
        endOfGame.value = true
        endOfGameCardVisible.value = true
    }

    private fun addNotation() {

    }

    private fun setSquaresToBlock(): MutableList<Square> {
        for (square in checksOnKing) {
            if (!attacks.contains(square)) {
                squaresToBlock.remove(square)
            }
        }
        return squaresToBlock
    }

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