package com.gomezdevlopment.chessnotationapp.view.game_screen

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.GameEvent
import com.gomezdevlopment.chessnotationapp.model.Square
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel
import com.google.firebase.components.Lazy
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun AnnotationBar(viewModel: GameViewModel) {
    val scrollState = rememberLazyListState()
    val annotationsSize = remember {
        mutableStateOf(0)
    }
    val annotations = remember {
        viewModel.getAnnotations()
    }

    Spacer(modifier = Modifier.height(20.dp))
    LazyRow(modifier = Modifier.fillMaxWidth(), state = scrollState) {
        itemsIndexed(annotations) { index, annotation ->
            //val text by remember{mutableStateOf(annotation)}
            if (index % 2 == 0) {
                Text(
                    text = "${((index / 2) + 1)}.",
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(4.dp)
                )
            }
            Text(
                text = annotation,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                modifier = Modifier.padding(4.dp)
            )
        }

    }

    if (annotationsSize.value != annotations.size) {
        annotationsSize.value = annotations.size
        LaunchedEffect(annotations) {
            scrollState.scrollToItem(annotations.size - 1)
        }
    }

}

@Composable
fun ChessCanvas(width: Float, viewModel: GameViewModel, context: Context) {
//    val piecePlayer = MediaPlayer.create(context, R.raw.piece_sound)
//    val checkPlayer = MediaPlayer.create(context, R.raw.check_sound)
//    val capturePlayer = MediaPlayer.create(context, R.raw.capture_sound)
//    val castlingPlayer = MediaPlayer.create(context, R.raw.castling_sound)
//    val gameEndPlayer = MediaPlayer.create(context, R.raw.end_of_game_sound)
//
//    val pieceSound = remember { viewModel.getPieceSound() }
//    val checkSound = remember { viewModel.getCheckSound() }
//    val captureSound = remember { viewModel.getCaptureSound() }
//    val castlingSound = remember { viewModel.getCastlingSound() }
//    val gameEndSound = remember { viewModel.getGameEndSound() }

    val chessBoardVector: ImageVector =
        ImageVector.vectorResource(id = R.drawable.ic_chess_board_blue_outlined)
    val rowWidthAndHeight: Float = (width / 8f)

    AnnotationBar(viewModel)

    Box(
        modifier = Modifier
            .width(width.dp)
            .aspectRatio(1f)
            .background(colorResource(id = R.color.orange))
    ) {
        Image(
            imageVector = chessBoardVector,
            contentDescription = "Chess Board",
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        )
        println("Recomposing for some reason")
        ChessSquaresV2(height = rowWidthAndHeight, viewModel = viewModel)
        Button(
            onClick =
            { viewModel.resetGame() },
            modifier = Modifier.offset((50).dp, (-50).dp),
            enabled = true
        ) {
            Text(text = "Reset Game")
        }
        Button(
            onClick =
            { viewModel.undoMove() },
            modifier = Modifier.offset((200).dp, (-50).dp),
            enabled = true
        ) {
            Text(text = "Undo Move")
        }
    }

//    if (pieceSound.value) {
//        pieceSound.value = false
//        viewModel.playSound(R.raw.piece_sound)
//    }
//    if (checkSound.value) {
//        checkSound.value = false
//        checkPlayer.start()
//        checkPlayer.setOnCompletionListener {
//            checkPlayer.release()
//        }
//    }
//    if (captureSound.value) {
//        captureSound.value = false
//        capturePlayer.start()
//        capturePlayer.setOnCompletionListener {
//            capturePlayer.release()
//        }
//    }
//    if (castlingSound.value) {
//        castlingSound.value = false
//        castlingPlayer.start()
//        castlingPlayer.setOnCompletionListener {
//            castlingPlayer.release()
//        }
//    }
//    if (gameEndSound.value) {
//        gameEndSound.value = false
//        gameEndPlayer.start()
//        gameEndPlayer.setOnCompletionListener {
//            gameEndPlayer.release()
//        }
//    }
}

@Composable
fun Piece(
    piece: ChessPiece,
    promotionSelectionShowing: MutableState<Boolean>,
    height: Float,
    viewModel: GameViewModel
) {
    val square = piece.square
    val offsetX = height * square.file
    val offsetY = (7 - square.rank) * height
    val imageVector = ImageVector.vectorResource(piece.pieceDrawable)
    Image(
        imageVector = imageVector,
        contentDescription = "Chess Piece",
        modifier = Modifier
            .height(height.dp)
            .aspectRatio(1f)
            .offset(offsetX.dp, offsetY.dp)
            .clickable {
                if (!promotionSelectionShowing.value) {
                    //clicked.value = false
                    viewModel.setPieceClickedState(false)
                    viewModel.selectPiece(piece)
                    if (viewModel.getPlayerTurn() == viewModel.getSelectedPiece().value.color) {
                        //clickedPiece.value.square
                        viewModel.setPieceClickedState(true)
                        //clicked.value = true
                    }
                }
                //viewModel.setPieceClickedState(false)
            }
    )
}

@Composable
fun ChessSquaresV2(height: Float, viewModel: GameViewModel) {
    val cardVisible = remember { mutableStateOf(false) }
    val piecesOnBoard = remember {
        viewModel.getPiecesOnBoard()
    }
    val hashMap = viewModel.getHashMap()
    val clicked = remember { viewModel.isPieceClicked() }
    val squareClicked = remember { mutableStateOf(false) }
    val movePiece = remember { mutableStateOf(false) }
    val promotionSelectionShowing = remember { mutableStateOf(false) }
    val clickedPiece = remember {
        viewModel.getSelectedPiece()
    }
    val targetRank = remember {
        mutableStateOf(0)
    }
    val targetFile = remember {
        mutableStateOf(0)
    }

    val checkmate = remember {
        viewModel.getCheckmate()
    }

    val stalemate = remember {
        viewModel.getStalemate()
    }

    val insufficientMaterial = remember {
        viewModel.getInsufficientMaterial()
    }

    val threeFoldRepetition = remember {
        viewModel.getThreeFoldRepetition()
    }

    val fiftyMoveRule = remember {
        viewModel.getFiftyMoveRule()
    }
//    val xRays = remember {
//        viewModel.xRays()
//    }
//
//    val attackedSquares = remember {
//        viewModel.getSquaresToBlock()
//    }
//
    val attacks = remember {
        viewModel.getAttacks()
    }

    // with hashmap
//    for (rank in 7 downTo 0) {
//        for (file in 0..7) {
//            val square = Square(rank, file)
//            val offsetX = height * file
//            val offsetY = (7 - rank) * height
////            if(attackedSquares.contains(square)){
////                Highlight(height = height, square = square, Color.Blue)
////            }
////            if(xRays.contains(square)){
////                Outline(height = height, square = square, Color.Yellow)
////            }
//            if (attacks.contains(square)) {
//                Highlight(height = height, square = square, Color.Red)
//            }
//
//            if (hashMap.containsKey(square)) {
//                val chessPiece = hashMap[square]!!
//                val imageVector = ImageVector.vectorResource(chessPiece.pieceDrawable)
//                if (square == viewModel.getCurrentSquare().value) {
//                    //Outline(height = height, square = square, Color.Blue)
//                    Highlight(height = height, square = square, Color.Blue)
//                }
//                if (viewModel.kingInCheck() && square == viewModel.kingSquare()) {
//                    Outline(height = height, square = square, Color.Red)
//                }
//                Image(
//                    imageVector = imageVector,
//                    contentDescription = "Chess Piece",
//                    modifier = Modifier
//                        .height(height.dp)
//                        .aspectRatio(1f)
//                        .offset(offsetX.dp, offsetY.dp)
//                        .clickable {
//
//                            if (!promotionSelectionShowing.value) {
//                                clicked.value = false
//                                clickedPiece.value = chessPiece
//                                if (viewModel.getPlayerTurn() == clickedPiece.value.color) {
//                                    clickedPiece.value.square
//                                    clicked.value = true
//                                }
//                            }
//                        }
//                )
//            }
//        }
//    }

    piecesOnBoard.forEach { piece ->
        key(piece) {
            if (viewModel.kingInCheck() && piece.square == viewModel.kingSquare()) {
                Outline(height = height, square = piece.square, Color.Red)
            }
            if (piece.square == viewModel.getCurrentSquare().value) {
                Highlight(height = height, square = piece.square, Color.Blue)
            }

            Piece(
                piece = piece,
                height = height,
                promotionSelectionShowing = promotionSelectionShowing,
                viewModel = viewModel
            )
           // println("redrawing piece $piece")
        }
    }

//    for (piece in piecesOnBoard) {
//        key(piece) {
//            if (viewModel.kingInCheck() && piece.square == viewModel.kingSquare()) {
//                Outline(height = height, square = piece.square, Color.Red)
//            }
//            if (piece.square == viewModel.getCurrentSquare().value) {
//                Highlight(height = height, square = piece.square, Color.Blue)
//            }
//
//            println("redrawing piece")
//            Piece(
//                piece = piece,
//                height = height,
//                promotionSelectionShowing = promotionSelectionShowing,
//                clicked = clicked,
//                clickedPiece = clickedPiece,
//                viewModel = viewModel
//            )
//        }
//    }


    if (clicked.value) {
        val legalMoves = viewModel.onEvent(GameEvent.OnPieceClicked, clickedPiece.value)
        if (legalMoves != null) {
            for (move in legalMoves) {
                if (hashMap.containsKey(move)) {
                    PossibleCapture(height, move, targetRank, targetFile, squareClicked)
                } else {
                    PossibleMove(height, move, targetRank, targetFile, squareClicked)
                }
            }
        }
    }

    if (squareClicked.value) {
//        movePiece.value = true
//        val offsetX = height * targetFile.value
//        val offsetY = (7 - targetRank.value) * height
//        val imageVector = ImageVector.vectorResource(clickedPiece.value.pieceDrawable)
//        Highlight(
//            height = height,
//            square = Square(targetRank.value, targetFile.value),
//            Color.Blue
//        )
//        Image(
//            imageVector = imageVector,
//            contentDescription = "Chess Piece",
//            modifier = Modifier
//                .height(height.dp)
//                .aspectRatio(1f)
//                .offset(offsetX.dp, offsetY.dp)
//                .clickable {
//                    if (!promotionSelectionShowing.value) {
//                        clicked.value = false
//                        clickedPiece.value = clickedPiece.value
//                        if (viewModel.getPlayerTurn() == clickedPiece.value.color) {
//                            clickedPiece.value.square
//                            clicked.value = true
//                        }
//                    }
//                }
//        )

        if (clickedPiece.value.piece == "pawn" && (targetRank.value == 7 || targetRank.value == 0)) {
            viewModel.movePiece(
                Square(targetRank.value, targetFile.value),
                clickedPiece.value
            )
            promotionSelectionShowing.value = true
        } else {
            //clickedPiece.value.square = Square(targetRank.value, targetFile.value)
            viewModel.changePiecePosition(
                Square(targetRank.value, targetFile.value),
                clickedPiece.value
            )
        }
        clicked.value = false
        squareClicked.value = false
    }

    if (promotionSelectionShowing.value) {
        PromotionV2(
            height,
            clickedPiece.value,
            promotionSelectionShowing,
            targetRank.value,
            targetFile.value,
            viewModel
        )
    }

    val previousSquare = viewModel.getPreviousSquare().value
    if (previousSquare.rank != 10) {
        //Outline(height = height, square = previousSquare, Color.Blue)
        Highlight(height = height, square = previousSquare, color = Color.Blue)
    }


    if (checkmate.value) {
        var winner = "White"
        if (viewModel.getPlayerTurn() == "white") {
            winner = "Black"
        }
        cardVisible.value = true
        EndOfGameCard("Checkmate", message = "$winner Wins!", cardVisible)
    }

    if (insufficientMaterial.value) {
        cardVisible.value = true
        EndOfGameCard("Draw", message = "by Insufficient Material", cardVisible)
    }

    if (stalemate.value) {
        cardVisible.value = true
        EndOfGameCard("Draw", message = "by Stalemate", cardVisible)
    }

    if (threeFoldRepetition.value) {
        cardVisible.value = true
        EndOfGameCard("Draw", message = "by Threefold Repetition", cardVisible)
    }

    if (fiftyMoveRule.value) {
        cardVisible.value = true
        EndOfGameCard("Draw", message = "by Fifty Move Rule", cardVisible)
    }
}

@Composable
private fun Highlight(
    height: Float, square: Square, color: Color
) {
    val offsetX = height * square.file
    val offsetY = (7 - square.rank) * height
    Canvas(
        modifier = Modifier
            .height(height.dp)
            .aspectRatio(1f)
            .absoluteOffset(offsetX.dp, offsetY.dp)
    ) {
        drawRect(
            color = color,
            size = size,
            alpha = .5f
        )
    }
}

@Composable
private fun PossibleMove(
    height: Float,
    square: Square,
    targetRank: MutableState<Int>,
    targetFile: MutableState<Int>,
    squareClicked: MutableState<Boolean>
) {
    val offsetX = height * square.file
    val offsetY = (7 - square.rank) * height
    Canvas(
        modifier = Modifier
            .height(height.dp)
            .aspectRatio(1f)
            .absoluteOffset(offsetX.dp, offsetY.dp)
            .clickable {
                targetRank.value = square.rank
                targetFile.value = square.file
                squareClicked.value = true
            }
    ) {
        drawCircle(
            color = Color.Black,
            radius = size.width / 8,
            alpha = .75f
        )
    }
}

@Composable
private fun PossibleCapture(
    height: Float,
    square: Square,
    targetRank: MutableState<Int>,
    targetFile: MutableState<Int>,
    squareClicked: MutableState<Boolean>
) {
    val offsetX = height * square.file
    val offsetY = (7 - square.rank) * height
    Canvas(
        modifier = Modifier
            .height(height.dp)
            .aspectRatio(1f)
            .absoluteOffset(offsetX.dp, offsetY.dp)
            .clickable {
                targetRank.value = square.rank
                targetFile.value = square.file
                squareClicked.value = true
            }
    ) {
        drawCircle(
            color = Color.Red,
            radius = size.width * .45f,
            alpha = .5f,
            style = Stroke(size.width * .05f)
        )
    }
}

@Composable
private fun Outline(
    height: Float,
    square: Square,
    color: Color
) {
    val offsetX = height * square.file
    val offsetY = (7 - square.rank) * height
    Canvas(
        modifier = Modifier
            .height(height.dp)
            .aspectRatio(1f)
            .absoluteOffset(offsetX.dp, offsetY.dp)
            .padding(1.dp)

    ) {
        drawRect(
            color = color,
            size = size,
            alpha = 1f,
            style = Stroke(size.width * .05f)
        )
    }
}

@Composable
private fun PromotionV2(
    width: Float,
    chessPiece: ChessPiece,
    promotionSelectionShowing: MutableState<Boolean>,
    rank: Int,
    file: Int,
    viewModel: GameViewModel
) {
    val pieces = mutableListOf<ChessPiece>()
    val blackPieceImages = listOf(
        ChessPiece("black", "queen", R.drawable.ic_bq_alpha, Square(rank, file)),
        ChessPiece("black", "rook", R.drawable.ic_br_alpha, Square(rank, file)),
        ChessPiece("black", "bishop", R.drawable.ic_bb_alpha, Square(rank, file)),
        ChessPiece("black", "knight", R.drawable.ic_bn_alpha, Square(rank, file))
    )

    val whitePieceImages = listOf(
        ChessPiece("white", "queen", R.drawable.ic_wq_alpha, Square(rank, file)),
        ChessPiece("white", "rook", R.drawable.ic_wr_alpha, Square(rank, file)),
        ChessPiece("white", "bishop", R.drawable.ic_wb_alpha, Square(rank, file)),
        ChessPiece("white", "knight", R.drawable.ic_wn_alpha, Square(rank, file))
    )

    if (chessPiece.color == "white") {
        pieces.addAll(whitePieceImages)
    } else {
        pieces.addAll(blackPieceImages)
    }

    val offsetX = width * file
    var offsetY = (7 - rank) * width
    if (rank == 0) {
        offsetY = 4 * width
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(id = R.color.transparentBlack))
            .zIndex(2f)
    )
    {
        Card(
            modifier = Modifier
                .width(width.dp)
                .height((width * 4).dp)
                .absoluteOffset(offsetX.dp, offsetY.dp),
            elevation = 5.dp,
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(Modifier.fillMaxWidth()) {
                pieces.forEach { selectedPiece ->
                    Image(
                        imageVector = ImageVector.vectorResource(selectedPiece.pieceDrawable),
                        contentDescription = "Chess Piece",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(5.dp)
                            .clickable {
                                promotionSelectionShowing.value = false
                                viewModel.promotion(Square(rank, file), selectedPiece)
                            }
                    )
                }
            }
        }

    }
}

@Composable
private fun EndOfGameCard(
    header: String,
    message: String,
    cardVisible: MutableState<Boolean>
) {
    if (cardVisible.value) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(colorResource(id = R.color.transparentBlack))
                .zIndex(2f)
        )
        {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(50.dp),
                elevation = 10.dp,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(50.dp))
                        Text(
                            text = header,
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            modifier = Modifier.absolutePadding(10.dp)
                        )
                        Text(text = message, textAlign = TextAlign.Center, fontSize = 14.sp)
                    }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        RoundDialogButton(buttonText = "Close", bool = cardVisible)
                        RoundDialogButton(buttonText = "Rematch", bool = cardVisible)
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RoundDialogButton(buttonText: String, bool: MutableState<Boolean>) {
    Button(
        onClick = { bool.value = false },
        shape = CircleShape,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 5.dp)
            .wrapContentHeight(),
        border = BorderStroke(2.dp, colorResource(id = R.color.teal)),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.White,
            contentColor = colorResource(id = R.color.teal)
        )
    )
    {
        Text(buttonText)
    }
}