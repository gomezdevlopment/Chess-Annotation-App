package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.data_classes.GameState
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.utils.Utils
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import com.gomezdevlopment.chessnotationapp.view.game_screen.board.notClickable
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel
import com.gomezdevlopment.chessnotationapp.view_model.UserViewModel

@Composable
fun Games(viewModel: UserViewModel, navController: NavController, gameViewModel: GameViewModel) {
    viewModel.initializeGamesList()
    val games = viewModel.userGames
    LazyColumn(){
        itemsIndexed(games) { index, game ->
            val color = game["color"]
            if(color != null){
                MainActivity.userColor = color
            }
            GamesCard(game["result"], game["opponent"], viewModel, index){
                val annotations = mutableListOf<String>()
                for(i in 0..game.size-4){
                    val fen = game[i.toString()]
                    if(fen != null){
                        annotations.add(i, fen)
                    }
                }
                gameViewModel.previousGameStates.clear()
                annotations.forEach {
                    gameViewModel.previousGameStates.add(GameState(Square(10,10), Square(10,10), it))
                }
                val notations = game["notations"]?.split(" ")?.filter { it.isNotBlank() }
                gameViewModel.notations.clear()
                if (notations != null) {
                    gameViewModel.notations.addAll(notations)
                }
                navController.navigate("gameReview")
            }
        }
    }
}

@Composable
fun ChessBoard() {
    val chessBoardVector = ImageVector.vectorResource(id = R.drawable.ic_chess_board_teal)
    Image(
        imageVector = chessBoardVector,
        //imageVector = chessBoardVector,
        contentDescription = "Chess Board",
        modifier = Modifier
            .width(100.dp)
            .aspectRatio(1f)
            .zIndex(1f)
    )
}

@Composable
fun GamesCardBoardState(viewModel: UserViewModel, index: Int){
    val color = MainActivity.userColor
    BoxWithConstraints(
        modifier = Modifier
            .width(100.dp)
            .aspectRatio(1f),
    ) {
        val size = (100.dp/8).value
        ChessBoard()
        val game = viewModel.userGames[index]
        val finalPosition = game[(game.size - 5).toString()]
        if(finalPosition != null){
            viewModel.parseFEN(finalPosition).forEach {
                key(it){
                    val square = it.square
                    val offsetX = Utils().offsetX(size, square.file, color)
                    val offsetY = Utils().offsetY(size, square.rank, color)
                    val offset = Offset(offsetX, offsetY)
                    val imageVector = ImageVector.vectorResource(id = it.pieceDrawable)
                    Image(
                        imageVector = imageVector,
                        contentDescription = "Chess Piece",
                        modifier = Modifier.notClickable(size.dp, offset)
                    )
                }
            }
        }
    }
}

@Composable
fun GamesCard(result: String?, opponent: String?, viewModel: UserViewModel, index: Int, onClick: () -> Unit){
    Card(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(20.dp, 5.dp)
            .clickable { onClick() }) {
        Row(){
            GamesCardBoardState(viewModel = viewModel, index)
            Column() {
                result?.let { Text(it, Modifier.padding(5.dp, 2.dp), fontWeight = FontWeight.SemiBold) }
                opponent?.let { Text("opponent: $it", Modifier.padding(5.dp, 2.dp)) }
            }
        }

    }
}