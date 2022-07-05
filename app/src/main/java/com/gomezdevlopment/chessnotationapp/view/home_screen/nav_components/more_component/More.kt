package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.more_component

import androidx.compose.foundation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.data_classes.GameState
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square
import com.gomezdevlopment.chessnotationapp.model.utils.Utils
import com.gomezdevlopment.chessnotationapp.view.*
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.user
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.userColor
import com.gomezdevlopment.chessnotationapp.view.game_screen.board.*
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel
import com.gomezdevlopment.chessnotationapp.view_model.UserViewModel

@Composable
fun UserScreen(userViewModel: UserViewModel, navController: NavController, gameViewModel: GameViewModel) {
    Column(Modifier.fillMaxSize()) {
        User()
        UserNavbar(userViewModel)
        when (userViewModel.destination.value) {
            "Games" -> Games(userViewModel, navController, gameViewModel)
            "Friends" -> Friends()
            "Settings" -> Settings()
        }
    }
//    Column(
//        Modifier
//            .fillMaxSize()
//            .background(orange),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("User", fontSize = 30.sp)
//    }
}

@Composable
fun Settings() {
    Text(text = "Settings")

    Column(Modifier.padding(30.dp)) {
        SettingsItemButton(text = "Board Theme")
        SettingsItemButton(text = "Pieces Theme")
        SettingsItemButton(text = "Theme")
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
fun SettingsItemButton(text: String) {
    Button(
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier
            .fillMaxWidth(),
        onClick = {},
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


@Composable
@Preview
fun Preview() {
    //GamesCard(result = "Win", opponent = "Fuku")
    //SettingsItemButton(text = "Board Theme")
}

@Composable
fun Games(viewModel: UserViewModel, navController: NavController, gameViewModel: GameViewModel) {
    viewModel.initializeGamesList()
    val games = viewModel.userGames
    LazyColumn(){
        itemsIndexed(games) { index, game ->
            val color = game["color"]
            if(color != null){
                userColor = color
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
    val color = userColor
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
@Composable
fun Friends() {
    Text(text = "Friends")
}

@Composable
fun User() {
    Row(
        Modifier
            .fillMaxWidth()
            .background(tealDarker)
            .padding(20.dp),
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_user_icon),
            contentDescription = "User Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(2.dp, tealDarker, CircleShape)
        )
        Column() {
            Text(
                user?.username ?: "Username",
                Modifier.padding(10.dp, 5.dp),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = textWhite
            )
            val wins = user?.wins
            val losses = user?.losses
            val draws = user?.draws
            Text("$wins Wins/$losses Losses/$draws Draws", Modifier.padding(10.dp, 5.dp), color = textWhite)
        }
    }
}

@Composable
fun UserNavbar(userViewModel: UserViewModel) {
    val settings: Boolean = userViewModel.destination.value == "Settings"
    val games: Boolean = userViewModel.destination.value == "Games"
    val friends: Boolean = userViewModel.destination.value == "Friends"

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .background(tealDarker)
            .padding(0.dp, 10.dp)
    ) {
        UserNavbarItem2(
            modifier = Modifier
                .weight(1f), "Games", games, userViewModel
        )
        UserNavbarItem2(
            modifier = Modifier
                .weight(1f), "Friends", friends, userViewModel
        )
        UserNavbarItem2(
            modifier = Modifier
                .weight(1f), "Settings", settings, userViewModel
        )
    }
}

@Composable
fun UserNavbarItem(
    modifier: Modifier,
    text: String,
    isSelected: Boolean,
    viewModel: UserViewModel
) {
    Column(
        modifier = modifier
            .padding(10.dp, 5.dp)
            .clickable {
                viewModel.destination.value = text
            }, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text,
            color = if (isSelected) tealDarker else textWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

@Composable
fun UserNavbarItem2(modifier: Modifier,
                    text: String,
                    isSelected: Boolean,
                    viewModel: UserViewModel){
    Button(
        shape = RoundedCornerShape(25.dp),
        modifier = modifier
            .padding(15.dp, 5.dp)
            .fillMaxWidth(),
        onClick = {viewModel.destination.value = text},
        colors = ButtonDefaults.buttonColors(backgroundColor = if (isSelected) MaterialTheme.colors.surface else tealDarker),
        elevation = ButtonDefaults.elevation(0.dp)
    ) {
        Column() {
            Text(
                text = text,
                fontSize = 16.sp,
                color = if (isSelected) tealDarker else textWhite,
                modifier = Modifier.padding(0.dp)
            )
        }
    }
}

private fun Modifier.highlight(isSelected: Boolean) =
    padding(10.dp, 1.dp)
        .background(
            color = if (isSelected) cardWhite else tealDarker,
            shape = RoundedCornerShape(20.dp),
        )

//@Composable
//fun Settings(signOutViewModel: SignOutViewModel, navController: NavController) {
//    Column(verticalArrangement = Arrangement.Center) {
//        Button(
//            onClick = {
//                signOutViewModel.signOut()
//                navController.navigate(R.id.action_settingsFragment_to_signInFragment)
//            }
//        ) {
//            Text("Sign Out")
//        }
//    }
//}