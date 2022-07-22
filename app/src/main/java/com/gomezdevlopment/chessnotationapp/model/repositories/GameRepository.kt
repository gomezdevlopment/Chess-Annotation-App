package com.gomezdevlopment.chessnotationapp.model.repositories

import android.os.CountDownTimer
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.GameState
import com.gomezdevlopment.chessnotationapp.realtime_database.OnlineGame
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.game_logic.*
import com.gomezdevlopment.chessnotationapp.model.pieces.*
import com.gomezdevlopment.chessnotationapp.model.firestore_interaction.FirestoreInteraction
import com.gomezdevlopment.chessnotationapp.realtime_database.RealtimeDatabaseGameInteraction
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.userColor
import com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements.formatTime
import com.gomezdevlopment.chessnotationapp.view_model.UserViewModel.Companion.userGames
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class GameRepository @Inject constructor(val dbInteraction: RealtimeDatabaseGameInteraction) :
    ViewModel(), GameSetup {
    override var piecesOnBoard: MutableList<ChessPiece> = mutableStateListOf()
    override var capturedPieces: MutableList<ChessPiece> = mutableStateListOf()
    override var occupiedSquares: MutableMap<Square, ChessPiece> = mutableMapOf()
    override var previousSquare: MutableState<Square> = mutableStateOf(Square(10, 10))
    override var currentSquare: MutableState<Square> = mutableStateOf(Square(10, 10))
    override var playerTurn: MutableState<String> = mutableStateOf("white")
    override var whiteKingSquare: MutableState<Square> = mutableStateOf(Square(0, 4))
    override var blackKingSquare: MutableState<Square> = mutableStateOf(Square(7, 4))
    override var xRayAttacks: MutableList<Square> = mutableListOf()
    override var attacks: MutableList<Square> = mutableListOf()
    override var pinnedPieces = mutableListOf<ChessPiece>()
    override var allLegalMoves: MutableList<Square> = mutableListOf()
    override var whiteCanCastleKingSide: MutableState<Boolean> = mutableStateOf(false)
    override var whiteCanCastleQueenSide: MutableState<Boolean> = mutableStateOf(false)
    override var blackCanCastleKingSide: MutableState<Boolean> = mutableStateOf(false)
    override var blackCanCastleQueenSide: MutableState<Boolean> = mutableStateOf(false)
    override var kingInCheck: MutableState<Boolean> = mutableStateOf(false)
    override var piecesCheckingKing = mutableListOf<ChessPiece>()
    override var checksOnKing = mutableListOf<Square>()
    override var previousGameStates = mutableListOf<GameState>()
    override var annotations: MutableList<String> = mutableStateListOf("start:")
    override var currentNotation: StringBuilder = StringBuilder("")
    override var selectedNotationIndex: MutableState<Int> = mutableStateOf(0)
    override val openDrawOfferedDialog = mutableStateOf(false)
    override val pinnedPiecesFromPreviousTurns = mutableListOf<List<ChessPiece>>(listOf())
    override var kingSquare: MutableState<Square> = mutableStateOf(blackKingSquare.value)

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
    override var pieceSound: MutableState<Boolean> = mutableStateOf(false)
    override var checkSound: MutableState<Boolean> = mutableStateOf(false)
    override var captureSound: MutableState<Boolean> = mutableStateOf(false)
    override var castlingSound: MutableState<Boolean> = mutableStateOf(false)
    var gameEndSound: MutableState<Boolean> = mutableStateOf(false)


    init {
        initialPosition()
        //testPositionKiwipete()
        //testPosition3()
        //testPosition4()
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
            eventListener()
        } else {
            //gameDocumentReference = null
        }
    }

    val lastMove = mutableStateOf("")

    fun eventListener() {
        if (MainActivity.gameID != null) {
            dbInteraction.dbGameReference.child(MainActivity.gameID.toString())
                .addValueEventListener(object : ValueEventListener {
                    var gameMap: OnlineGame? = null
                    override fun onDataChange(snapshot: DataSnapshot) {
                        gameMap = snapshot.getValue(OnlineGame::class.java)
                        if (gameMap?.resignation != "") {
                            resignation(gameMap?.resignation)
                            startStopClocks()
                        }
                        val drawOffer = gameMap?.drawOffer
                        if (drawOffer != null) {
                            if (drawOffer == "white" || drawOffer == "black") {
                                if (drawOffer != userColor) {
                                    openDrawOfferedDialog.value = true
                                }
                            }
                            if (drawOffer == "accept") {
                                drawAccepted()
                                startStopClocks()
                            }
                        }
                        //Move
                        val previousMoveString = gameMap?.previousMove.toString()
                        if (previousMoveString != "") {
                            val piece = getPieceFromDocument(previousMoveString)
                            val square: Square = getSquareFromDocument(previousMoveString)
                            if (piece != null && piece.color != userColor) {
                                if (piece.piece == "pawn" && (square.rank == 0 || square.rank == 7)) {
                                    changePiecePosition(
                                        square,
                                        piece,
                                        getPromotionSelectionFromDocument(previousMoveString)
                                    )
                                } else {
                                    changePiecePosition(square, piece, null)
                                }
                            }
                            if (lastMove.value != previousMoveString) {
                                startStopClocks()
                            }
                            lastMove.value = previousMoveString
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error)
                    }
                })
        }
    }

    private fun createMapOfGame(result: String): Map<String, String> {
        val game = mutableMapOf<String, String>()
        val gameNotationString = StringBuilder("")
        annotations.forEach {
            gameNotationString.append("$it ")
        }
        game["notations"] = gameNotationString.toString()

        previousGameStates.forEachIndexed { index, gameState ->
            game[index.toString()] = gameState.fenPosition
        }

        game["result"] = result

        var opponentName = MainActivity.game?.whitePlayer
        var color = "black"
        if (userColor == "white") {
            opponentName = MainActivity.game?.blackPlayer
            color = "white"
        }

        if (opponentName != null) {
            game["opponent"] = opponentName
        }

        game["color"] = color

        return game
    }

    private fun writeWinToFirestore(result: String) {
        dbInteraction.incrementWins()
        val game = createMapOfGame(result)
        userGames.add(0, game)
        dbInteraction.writeGame(game)
    }

    private fun writeLossToFirestore(result: String) {
        dbInteraction.incrementLosses()
        val game = createMapOfGame(result)
        userGames.add(0, game)
        dbInteraction.writeGame(game)
    }

    private fun writeDrawToFirestore(result: String) {
        dbInteraction.incrementDraws()
        val game = createMapOfGame(result)
        userGames.add(0, game)
        dbInteraction.writeGame(game)
    }

    private fun setEndOfGameValues(result: String, message: String) {
        //FirestoreGameInteraction().writeGame()
        endOfGameResult.value = result
        endOfGameMessage.value = message
        endOfGame.value = true
        endOfGameCardVisible.value = true
        gameEndSound.value = true
    }

    private fun drawAccepted() {
        setEndOfGameValues("Draw", "by Agreement")
        writeDrawToFirestore("Draw by Agreement")
    }

    private fun resignation(playerColor: String?) {
        if (playerColor != null) {
            when (playerColor) {
                "white" -> {
                    if (userColor == "white") {
                        writeLossToFirestore("Loss by resignation")
                    }
                    if (userColor == "black") {
                        writeWinToFirestore("Win by resignation")
                    }
                    setEndOfGameValues("Black Wins!", "by Resignation")
                }
                "black" -> {
                    if (userColor == "black") {
                        writeLossToFirestore("Loss by resignation")
                    }
                    if (userColor == "white") {
                        writeWinToFirestore("Win by resignation")
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
        previousMoveString: String
    ): PromotionPiece? {
        var piece: PromotionPiece? = null
        when (playerTurn.value) {
            "black" -> {
                if (previousMoveString.contains("queen"))
                    piece = PromotionPieces().blackPieces["queen"]
                if (previousMoveString.contains("rook"))
                    piece = PromotionPieces().blackPieces["rook"]
                if (previousMoveString.contains("bishop"))
                    piece = PromotionPieces().blackPieces["bishop"]
                if (previousMoveString.contains("knight"))
                    piece = PromotionPieces().blackPieces["knight"]
            }
            "white" -> {
                if (previousMoveString.contains("queen"))
                    piece = PromotionPieces().whitePieces["queen"]
                if (previousMoveString.contains("rook"))
                    piece = PromotionPieces().whitePieces["rook"]
                if (previousMoveString.contains("bishop"))
                    piece = PromotionPieces().whitePieces["bishop"]
                if (previousMoveString.contains("knight"))
                    piece = PromotionPieces().whitePieces["knight"]
            }
        }
//        if (piece != null) {
//            promotion(newSquare, piece, 0)
//        }
        return piece
    }

    private fun setPreviousPosition(state: State) {
        //piecesOnBoard[state.index].square = state.square
        state.piece.square = state.square
        if (state.capture) {
            piecesOnBoard.add(capturedPieces.last())
            capturedPieces.removeLast()
        }

        if (state.whiteKingSquare == Square(0, 4)) {
            if (currentSquare.value == Square(0, 6)) {
                val rook = occupiedSquares[Square(0, 5)]
                if (rook != null) {
                    occupiedSquares.remove(rook.square)
                    rook.square = Square(0, 7)
                    occupiedSquares[rook.square] = rook
                }
            }

            if (currentSquare.value == Square(0, 2)) {
                val rook = occupiedSquares[Square(0, 3)]
                if (rook != null) {
                    occupiedSquares.remove(rook.square)
                    rook.square = Square(0, 0)
                    occupiedSquares[rook.square] = rook
                }
            }
        }

        if (state.blackKingSquare == Square(7, 4)) {
            if (currentSquare.value == Square(7, 6)) {
                val rook = occupiedSquares[Square(7, 5)]
                if (rook != null) {
                    occupiedSquares.remove(rook.square)
                    rook.square = Square(7, 7)
                    occupiedSquares[rook.square] = rook
                }
            }

            if (currentSquare.value == Square(7, 2)) {
                val rook = occupiedSquares[Square(7, 3)]
                if (rook != null) {
                    occupiedSquares.remove(rook.square)
                    rook.square = Square(7, 0)
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

    fun undoMove() {
        setPreviousPosition(states.last())
        states.removeLast()
    }

    override fun checkIfCastled(
        piece: ChessPiece,
        newSquare: Square,
        depth: Int
    ): String {
        //Castling
        if (piece.piece == "king") {
            if (castleKingSide().value) {
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

//    private fun convertPromotionToString(previousSquare: Square, square: Square): String {
//        return "${previousSquare.rank}${previousSquare.file}${square.rank}${square.file}"
//    }

    override fun changePiecePosition(
        newSquare: Square,
        piece: ChessPiece,
        promotion: PromotionPiece?
    ) {
        val string = convertMoveToStringOfRanksAndFiles(piece, newSquare)
        val turn: String = playerTurn.value
        viewModelScope.launch {
            if (promotion != null) {
                dbInteraction.writePromotion(turn, string, promotion.piece)
            } else {
                dbInteraction.writeMove(turn, string)
            }
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
            kingSquare.value = blackKingSquare.value
        } else {
            if (piece.piece == "king") {
                blackKingSquare.value = newSquare
            }
            playerTurn.value = "white"
            kingSquare.value = whiteKingSquare.value
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
        checkIfGameOver(notation)
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
        if (!endOfGame.value) {
            notation.checkmateOrCheck(checkmate.value, kingInCheck.value)
            annotations.add(currentNotation.toString())
            currentNotation.clear()
            selectedNotationIndex.value += 1
        }
        //startStopClocks()
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

    private fun checkIfGameOver(notation: Notation) {
        val endOfGameConditions = EndOfGameConditions(gameEndSound)
        endOfGameConditions.checkCheckmate(allLegalMoves, kingInCheck.value, checkmate)
        endOfGameConditions.checkStalemate(allLegalMoves, kingInCheck.value, stalemate)
        endOfGameConditions.checkInsufficientMaterial(piecesOnBoard, insufficientMaterial)
        endOfGameConditions.checkThreefoldRepetition(previousGameStates, threeFoldRepetition)
        endOfGameConditions.checkFiftyMoveCount(allLegalMoves, fiftyMoveCount.value, fiftyMoveRule)

        fun setNotations() {
            notation.checkmateOrCheck(checkmate.value, kingInCheck.value)
            annotations.add(currentNotation.toString())
            currentNotation.clear()
            selectedNotationIndex.value += 1
        }

        if (checkmate.value) {
            setNotations()
            var winner = "White"
            if (playerTurn.value == "white") {
                winner = "Black"
            }
            if (userColor == playerTurn.value) {
                writeLossToFirestore("Loss by Checkmate")
            }
            if (userColor != playerTurn.value) {
                writeWinToFirestore("Win by Checkmate")
            }
            setEndOfGameValues("Checkmate", "$winner Wins!")
        }
        if (insufficientMaterial.value) {
            setNotations()
            writeDrawToFirestore("Draw by Insufficient Material")
            setEndOfGameValues("Draw", "by Insufficient Material")
        }

        if (stalemate.value) {
            setNotations()
            writeDrawToFirestore("Draw by Stalemate")
            setEndOfGameValues("Draw", "by Stalemate")
        }

        if (threeFoldRepetition.value) {
            setNotations()
            writeDrawToFirestore("Draw by Threefold Repetition")
            setEndOfGameValues("Draw", "by Threefold Repetition")
        }

        if (fiftyMoveRule.value) {
            setNotations()
            writeDrawToFirestore("Draw by The Fifty Move Rule")
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
            writeDrawToFirestore("Draw by Time Out vs Insufficient Material")
            endOfGameResult.value = "Draw"
            endOfGameMessage.value = "by Time Out vs Insufficient Material"
        } else {
            if (userColor == playerTurn.value) {
                writeLossToFirestore("Loss by Timeout")
            }
            if (userColor != playerTurn.value) {
                writeWinToFirestore("Win by Timeout")
            }
        }
        endOfGame.value = true
        endOfGameCardVisible.value = true
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