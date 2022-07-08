package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
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

@Composable
fun PieceThemes(navController: NavController, viewModel: UserViewModel) {
    val pieceThemes = listOf(
        "Alpha" to alphaTheme,
        "Leipzig" to leipzigTheme,
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
            Text("Piece Theme", fontWeight = FontWeight.Bold, fontSize = 30.sp)
        }
        Row {
            LazyColumn(){
                itemsIndexed(pieceThemes) { _, theme ->
                    PieceThemeSelectionItem(
                        pieceThemeName = theme.first,
                        theme = theme.second,
                        viewModel
                    ) {
                        viewModel.setChessPieceTheme(theme.first)
                    }
                }
            }
        }
    }
}

@Composable
fun PieceThemeSelectionItem(
    pieceThemeName: String,
    theme: Int,
    viewModel: UserViewModel,
    onClick: () -> Unit
) {
    val pieceThemeVector = ImageVector.vectorResource(id = theme)
    val currentSelection = viewModel.pieceThemeSelection

    Card(
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(5.dp, if (pieceThemeName == currentSelection) teal else Color.Transparent),
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier
            .fillMaxWidth()
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
                chessBoardVector = pieceThemeVector,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = pieceThemeName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}

