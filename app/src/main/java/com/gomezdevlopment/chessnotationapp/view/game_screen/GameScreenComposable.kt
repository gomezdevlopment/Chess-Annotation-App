package com.gomezdevlopment.chessnotationapp.view.game_screen

import android.content.Context
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.GameEvent
import com.gomezdevlopment.chessnotationapp.model.Square
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel

@Composable
fun AnnotationBar(viewModel: GameViewModel) {
    val scrollState = rememberLazyListState()
    viewModel.onUpdate.value
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
fun SoundFX(viewModel: GameViewModel){
    val pieceSound = remember { viewModel.getPieceSound() }
    val checkSound = remember { viewModel.getCheckSound() }
    val captureSound = remember { viewModel.getCaptureSound() }
    val castlingSound = remember { viewModel.getCastlingSound() }
    val gameEndSound = remember { viewModel.getGameEndSound() }

    if (pieceSound.value) {
        pieceSound.value = false
        viewModel.playSound(R.raw.piece_sound)
    }
    if (checkSound.value) {
        checkSound.value = false
        viewModel.playSound(R.raw.check_sound)
    }
    if (captureSound.value) {
        captureSound.value = false
        viewModel.playSound(R.raw.capture_sound)
    }
    if (castlingSound.value) {
        castlingSound.value = false
        viewModel.playSound(R.raw.castling_sound)
    }
    if (gameEndSound.value) {
        gameEndSound.value = false
        viewModel.playSound(R.raw.end_of_game_sound)
    }
}

@Composable
fun ChessCanvas(width: Float, viewModel: GameViewModel) {
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
        println("recomposing main")
        Pieces(viewModel = viewModel, height = rowWidthAndHeight)
        ChessSquaresV2(height = rowWidthAndHeight, viewModel = viewModel)
        val onBackPressedDispatcher =
            LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
        Button(
            onClick =
            {
                onBackPressedDispatcher?.onBackPressed()
                viewModel.resetGame()
            },
            modifier = Modifier.offset((50).dp, (-50).dp),
            enabled = true
        ) {
            Text(text = "Exit")
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
}

@Composable
fun Piece(
    piece: ChessPiece,
    height: Float,
    viewModel: GameViewModel
) {
    key(piece){
        val square = piece.square
        val offsetX = height * square.file
        val offsetY = (7 - square.rank) * height
        val offsetXAnimated by animateDpAsState(targetValue = offsetX.dp, tween(250))
        val offsetYAnimated by animateDpAsState(targetValue = offsetY.dp, tween(250))
        val imageVector = ImageVector.vectorResource(piece.pieceDrawable)
        Image(
            imageVector = imageVector,
            contentDescription = "Chess Piece",
            modifier = Modifier
                .height(height.dp)
                .aspectRatio(1f)
                .offset(offsetXAnimated, offsetYAnimated)
                .clickable {
                    viewModel.selectPiece(piece)
                }
        )
    }

}

@Composable
fun Pieces(viewModel: GameViewModel, height: Float) {
    println("printing pieces again")
    viewModel.piecesOnBoard.value.forEach { piece ->
        viewModel.onUpdate.value
        if (viewModel.kingInCheck() && piece.square == viewModel.kingSquare()) {
            Outline(height = height, square = piece.square, Color.Red)
        }
        if (piece.square == viewModel.getCurrentSquare().value) {
            Highlight(height = height, square = piece.square, Color.Blue)
        }
        key(piece) {
            Piece(
                piece = piece,
                height = height,
                viewModel = viewModel
            )
        }
    }
    val previousSquare = viewModel.getPreviousSquare().value
    if (previousSquare.rank != 10) {
        Highlight(height = height, square = previousSquare, color = Color.Blue)
    }
}

@Composable
fun ChessSquaresV2(height: Float, viewModel: GameViewModel) {
    val cardVisible = remember { mutableStateOf(false) }
    // val piecesOnBoard = viewModel.getPiecesOnBoard() as List<ChessPiece>
    val hashMap = viewModel.getHashMap()
    val clicked = remember { viewModel.isPieceClicked() }
    val squareClicked = remember { mutableStateOf(false) }
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
        if (clickedPiece.value.piece == "pawn" && (targetRank.value == 7 || targetRank.value == 0)) {
            viewModel.movePiece(
                Square(targetRank.value, targetFile.value),
                clickedPiece.value
            )
            promotionSelectionShowing.value = true
        } else {
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
            alpha = .3f
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