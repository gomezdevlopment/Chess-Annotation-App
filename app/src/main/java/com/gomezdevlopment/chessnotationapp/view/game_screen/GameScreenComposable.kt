package com.gomezdevlopment.chessnotationapp.view.game_screen

import android.app.Activity
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.GameEvent
import com.gomezdevlopment.chessnotationapp.model.Square
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel

@Composable
fun ChessCanvas(width: Float, viewModel: GameViewModel) {
    val chessBoardVector: ImageVector =
        ImageVector.vectorResource(id = R.drawable.ic_chess_board_blue_outlined)

    val rowWidthAndHeight: Float = (width/ 8f)
    println(rowWidthAndHeight)
    println(rowWidthAndHeight/2f)

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

}

@Composable
fun ChessSquaresV2(height: Float, viewModel: GameViewModel) {
    val hashMap = viewModel.getHashMap()
    val clicked = remember { mutableStateOf(false) }
    val squareClicked = remember { mutableStateOf(false) }
    val clickedPiece = remember {
        mutableStateOf(ChessPiece("black", "rook", R.drawable.ic_br_alpha, Square(7, 0)))
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

    for (rank in 7 downTo 0) {
        for (file in 0..7) {
            val square = Square(rank, file)
            val offsetX = height * file
            val offsetY = (7 - rank) * height
//            if(attackedSquares.contains(square)){
//                Highlight(height = height, square = square, Color.Blue)
//            }
//            if(xRays.contains(square)){
//                Outline(height = height, square = square, Color.Yellow)
//            }
//            if(attacks.contains(square)){
//                Highlight(height = height, square = square, Color.Blue)
//            }

            if (hashMap.containsKey(square)) {
                val chessPiece = hashMap[square]!!
                val imageVector = ImageVector.vectorResource(chessPiece.pieceDrawable)
                if (square == viewModel.getCurrentSquare().value) {
                    //Outline(height = height, square = square, Color.Blue)
                    Highlight(height = height, square = square, Color.Blue)
                }
                if(viewModel.kingInCheck() && square == viewModel.kingSquare()){
                    Outline(height = height, square = square, Color.Red)
                }
                Image(
                    imageVector = imageVector,
                    contentDescription = "Chess Piece",
                    modifier = Modifier
                        .height(height.dp)
                        .aspectRatio(1f)
                        .offset(offsetX.dp, offsetY.dp)
                        .clickable {

                            if (!squareClicked.value) {
                                clicked.value = false
                                clickedPiece.value = chessPiece
                                if (viewModel.getPlayerTurn() == clickedPiece.value.color) {
                                    clickedPiece.value.square
                                    clicked.value = true
                                }
                            }
                        }
                )
            }
        }
    }

    if (clicked.value) {
        val legalMoves = viewModel.onEvent(GameEvent.OnPieceClicked, clickedPiece.value)
        for (move in legalMoves) {
            if (viewModel.getHashMap().containsKey(move)) {
                PossibleCapture(height, move, targetRank, targetFile, squareClicked)
            }else{
                PossibleMove(height, move, targetRank, targetFile, squareClicked)
            }
        }
    }

    if (squareClicked.value) {

        if(clickedPiece.value.piece == "pawn" && (targetRank.value == 7 || targetRank.value == 0)){
            viewModel.movePiece(Square(targetRank.value, targetFile.value), clickedPiece.value)
            PromotionV2(height, clickedPiece.value, squareClicked, targetRank.value, targetFile.value, viewModel)
            //clicked.value = false
            //squareClicked.value = false
        }else{
            viewModel.changePiecePosition(
                Square(targetRank.value, targetFile.value),
                clickedPiece.value
            )
            clicked.value = false
            squareClicked.value = false
        }
    }

    val previousSquare = viewModel.getPreviousSquare().value
    if (previousSquare.rank != 10) {
        //Outline(height = height, square = previousSquare, Color.Blue)
        Highlight(height = height, square = previousSquare, color = Color.Blue)
    }


    if(checkmate.value){
        var winner = "White"
        if(viewModel.getPlayerTurn() == "white"){
            winner = "Black"
        }
        EndOfGameCard("Checkmate", message = "$winner Wins!")
    }

    if(insufficientMaterial.value){
        EndOfGameCard("Draw", message = "by Insufficient Material")
    }

    if(stalemate.value){
        EndOfGameCard("Draw", message = "Stalemate")
    }

    if(threeFoldRepetition.value){
        EndOfGameCard("Draw", message = "by Threefold Repetition")
    }

    if(fiftyMoveRule.value){
        EndOfGameCard("Draw", message = "by Fifty Move Rule")
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
            radius = size.width*.45f,
            alpha = .5f,
            style = Stroke(size.width*.05f)
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
            style = Stroke(size.width*.05f)
        )
    }
}

@Composable
private fun PromotionV2(width: Float, chessPiece: ChessPiece, squareClicked: MutableState<Boolean>, rank: Int, file: Int, viewModel: GameViewModel) {
    val pieces = mutableListOf<ChessPiece>()
    val blackPieceImages = listOf(
        ChessPiece("black", "queen", R.drawable.ic_bq_alpha, Square(rank, file)),
        ChessPiece("black", "rook", R.drawable.ic_br_alpha, Square(rank, file)),
        ChessPiece("black", "bishop",  R.drawable.ic_bb_alpha, Square(rank, file)),
        ChessPiece("black", "knight", R.drawable.ic_bn_alpha, Square(rank, file)))

    val whitePieceImages = listOf(
        ChessPiece("white", "queen", R.drawable.ic_wq_alpha, Square(rank, file)),
        ChessPiece("white", "rook", R.drawable.ic_wr_alpha, Square(rank, file)),
        ChessPiece("white", "bishop",  R.drawable.ic_wb_alpha, Square(rank, file)),
        ChessPiece("white", "knight", R.drawable.ic_wn_alpha, Square(rank, file)))

    if(chessPiece.color == "white"){
        pieces.addAll(whitePieceImages)
    }else{
        pieces.addAll(blackPieceImages)
    }

    val offsetX = width * file
    var offsetY = (7 - rank) * width
    if(rank == 0){
        offsetY = 4 * width
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(colorResource(id = R.color.transparentBlack))
        .zIndex(2f))
    {
        Card(modifier = Modifier
            .width(width.dp)
            .height((width * 4).dp)
            .absoluteOffset(offsetX.dp, offsetY.dp),
            elevation = 5.dp,
            shape = RoundedCornerShape(10.dp)
            ){
            Column(Modifier.fillMaxWidth()){
                pieces.forEach {selectedPiece ->
                    Image(
                        imageVector = ImageVector.vectorResource(selectedPiece.pieceDrawable),
                        contentDescription = "Chess Piece",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(5.dp)
                            .clickable {
                                squareClicked.value = false
                                viewModel.promotion(Square(rank, file), selectedPiece)
                            }
                    )
                }
            }
        }

    }
}

@Composable
private fun EndOfGameCard(header: String, message: String) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(colorResource(id = R.color.transparentBlack))
        .zIndex(2f))
    {
        Card(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(50.dp),
            elevation = 10.dp,
            shape = RoundedCornerShape(20.dp)
        ){
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                Text(text = header, textAlign = TextAlign.Center, fontSize = 24.sp, modifier = Modifier.padding(10.dp, 10.dp))
                Text(text = message, textAlign = TextAlign.Center, fontSize = 14.sp)
            }
        }
    }
}