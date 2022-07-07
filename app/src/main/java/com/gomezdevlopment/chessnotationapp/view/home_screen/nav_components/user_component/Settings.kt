@file:OptIn(ExperimentalFoundationApi::class)

package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view.*
import com.gomezdevlopment.chessnotationapp.view.game_screen.board.ChessBoard
import com.gomezdevlopment.chessnotationapp.view.theming.blueBoardOutlined
import com.gomezdevlopment.chessnotationapp.view.theming.greyBoard
import com.gomezdevlopment.chessnotationapp.view.theming.orangeBoard
import com.gomezdevlopment.chessnotationapp.view.theming.tealBoard
import com.gomezdevlopment.chessnotationapp.view_model.UserViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SettingsNavigation(viewModel: UserViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "settings") {
        composable("settings") { Settings(navController) }
        composable("boardThemes") { BoardThemes(navController, viewModel) }
        composable("pieceThemes") { BoardThemes(navController, viewModel) }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BoardThemes(navController: NavController, viewModel: UserViewModel) {
    val boardThemes = listOf(
        "Teal" to tealBoard,
        "Orange" to orangeBoard,
        "Blue Outlined" to blueBoardOutlined,
        "Grey" to greyBoard
    )

    Dialog(onDismissRequest = {}, properties = DialogProperties(usePlatformDefaultWidth = false)) {
//        rememberSystemUiController().setStatusBarColor(MaterialTheme.colors.background)
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            Row(Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.Start) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Go Back",
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    })
            }
            Row(Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.Start) {
               Text("Board Theme", fontWeight = FontWeight.Bold, fontSize = 24.sp)
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
    val chessBoardVector = ImageVector.vectorResource(id = board)
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
                chessBoardVector = chessBoardVector,
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

@Composable
fun Settings(navController: NavController) {
    Text(text = "Settings")

    Column(Modifier.padding(30.dp)) {
        SettingsItemButton(text = "Board Theme") {
            navController.navigate("boardThemes")
        }
        SettingsItemButton(text = "Pieces Theme") {
            navController.navigate("boardThemes")
        }
        SettingsItemButton(text = "Theme") {
            navController.navigate("boardThemes")
        }
    }
}

@Composable
fun SettingsItem(text: String) {
    Row(
        Modifier
            .background(color = cardWhite, shape = CircleShape)
            .fillMaxWidth()
            .padding(20.dp, 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(text = text, fontSize = 18.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_navigation_arrow),
                contentDescription = text,
                Modifier.size(12.dp)
            )
        }
    }
}

@Composable
fun SettingsItemButton(text: String, onClick: () -> Unit) {
    Button(
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier
            .fillMaxWidth(),
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)
    ) {
        Column() {
            Text(
                text = text,
                fontSize = 16.sp,
                color = tealDarker,
                modifier = Modifier.padding(5.dp)
            )
        }
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_navigation_arrow),
                contentDescription = text,
                Modifier.size(12.dp),
                tint = tealDarker
            )
        }
    }

    Spacer(modifier = Modifier.height(15.dp))
}