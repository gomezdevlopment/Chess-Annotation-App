package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component.settings

import android.widget.Space
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.gomezdevlopment.chessnotationapp.view.game_screen.board.ChessBoard
import com.gomezdevlopment.chessnotationapp.view.theming.*
import com.gomezdevlopment.chessnotationapp.view_model.UserViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoardThemes(navController: NavController, viewModel: UserViewModel) {
    val boardThemes = listOf(
        "Teal" to tealBoard,
        "Dark Cyan" to darkCyanBoard,
        "Brown" to brownBoard,
        "Orange" to orangeBoard,
        "Blue" to blueBoard,
        "Purple" to purpleBoard,
        "Grey" to greyBoard,
        "Checkers" to checkersBoard,
        "Wood" to woodBoard,
        "Purple Pearl" to purplePearlBoard
    )

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp, 5.dp), horizontalArrangement = Arrangement.Start) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Go Back",
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        navController.popBackStack()
                    })
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp, 5.dp), horizontalArrangement = Arrangement.Start) {
            Text("Board Theme", fontWeight = FontWeight.Bold, fontSize = 30.sp)
        }
        Row() {
            LazyVerticalGrid(cells = GridCells.Fixed(2)) {
                itemsIndexed(boardThemes) { _, board ->
                    BoardSelectionItem(
                        boardName = board.first,
                        board = board.second,
                        viewModel
                    ) {
                        viewModel.setChessBoardTheme(board.second)
                    }
                }
                item(){
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

    }
}

@Composable
fun BoardSelectionItem(
    boardName: String,
    board: Int,
    viewModel: UserViewModel,
    onClick: () -> Unit
) {
    val currentSelection = viewModel.chessBoardTheme

    Card(
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(5.dp, if (board == currentSelection) teal else Color.Transparent),
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier
            .fillMaxWidth(.5f)
            .padding(15.dp)
            .clickable {
                onClick()
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(15.dp)
        ) {
            ChessBoard(
                chessBoard = board,
                modifier = Modifier.chessBoardThemeSelection()
            )
            Text(
                text = boardName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}

fun Modifier.chessBoardThemeSelection() =
    fillMaxWidth()
        .aspectRatio(1f)
        .zIndex(1f)