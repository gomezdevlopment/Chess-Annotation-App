package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component.games

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.gomezdevlopment.chessnotationapp.model.data_classes.GameState
import com.gomezdevlopment.chessnotationapp.model.data_classes.OnlineGame
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.utils.Utils
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import com.gomezdevlopment.chessnotationapp.view.game_screen.board.notClickable
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel
import com.gomezdevlopment.chessnotationapp.view_model.UserViewModel
import com.gomezdevlopment.chessnotationapp.view_model.UserViewModel.Companion.userGames
import kotlinx.coroutines.CoroutineScope

@Composable
fun Games(
    viewModel: UserViewModel,
    homeNavController: NavController,
    gameViewModel: GameViewModel
) {
    if (userGames.isEmpty()) {
        println("init")
        viewModel.initializeGamesList()
    }

    var recentGames = userGames
    var olderGames = listOf<Map<String, String>>()

    if(userGames.size > 3){
        recentGames = userGames.subList(0, 3)
        olderGames = userGames.subList(3, userGames.lastIndex)
    }

    LazyColumn() {

        item {
            Text(text = "Recent Games", modifier = Modifier.padding(20.dp, 5.dp))
        }
        itemsIndexed(recentGames) { index, game ->
            val color = game["color"]
            if (color != null) {
                MainActivity.userColor = color
            }
            GamesCard(game["result"], game["opponent"], viewModel, index, true) {
                viewModel.goToGameReview(gameViewModel, game, homeNavController)
            }
        }
        item {
            Text(text = "Older Games", modifier = Modifier.padding(20.dp, 5.dp))
        }
        itemsIndexed(olderGames) { index, game ->
            val color = game["color"]
            if (color != null) {
                MainActivity.userColor = color
            }
            GamesCard(game["result"], game["opponent"], viewModel, index, false) {
                viewModel.goToGameReview(gameViewModel, game, homeNavController)
            }
        }
        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}


@Composable
fun ChessBoard(board: Int) {
    val chessBoardVector = ImageVector.vectorResource(id = board)
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
fun GamesCardBoardState(viewModel: UserViewModel, index: Int) {
    val color = MainActivity.userColor
    BoxWithConstraints(
        modifier = Modifier
            .width(100.dp)
            .aspectRatio(1f),
    ) {
        val size = (100.dp / 8).value
        ChessBoard(viewModel.chessBoardTheme)
        val game = userGames[index]
        val finalPosition = game[(game.size - 5).toString()]
        if (finalPosition != null) {
            viewModel.parseFEN(finalPosition).forEach {
                key(it) {
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
fun GamesCard(
    result: String?,
    opponent: String?,
    viewModel: UserViewModel,
    index: Int,
    recentGame: Boolean,
    onClick: () -> Unit
) {
    Card(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(20.dp, 5.dp)
            .clickable { onClick() }) {
        Row() {
            if (recentGame) {
                GamesCardBoardState(viewModel = viewModel, index)
            }
            Column() {
                result?.let {
                    Text(
                        it,
                        Modifier.padding(5.dp, 2.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                opponent?.let { Text("opponent: $it", Modifier.padding(5.dp, 2.dp)) }
            }
        }

    }
}