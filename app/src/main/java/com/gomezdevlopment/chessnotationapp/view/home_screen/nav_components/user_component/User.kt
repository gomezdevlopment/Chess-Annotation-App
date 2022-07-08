package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.user
import com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component.friends.FriendsList
import com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component.games.Games
import com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component.settings.BoardThemes
import com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component.settings.PieceThemes
import com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component.settings.Settings
import com.gomezdevlopment.chessnotationapp.view.theming.cardWhite
import com.gomezdevlopment.chessnotationapp.view.theming.tealDarker
import com.gomezdevlopment.chessnotationapp.view.theming.textWhite
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel
import com.gomezdevlopment.chessnotationapp.view_model.UserViewModel

@Composable
fun UserNavigation(userViewModel: UserViewModel, homeNavController: NavController, gameViewModel: GameViewModel) {
    val userNavController = rememberNavController()
    NavHost(navController = userNavController, startDestination = "user") {
        composable("user") { UserScreen(userViewModel, homeNavController, userNavController, gameViewModel) }
        composable("boardThemes") { BoardThemes(userNavController, userViewModel) }
        composable("pieceThemes") { PieceThemes(userNavController, userViewModel) }
    }
}

@Composable
fun UserScreen(userViewModel: UserViewModel, homeNavController: NavController, userNavController: NavController, gameViewModel: GameViewModel) {
    Column(Modifier.fillMaxSize()) {
        User()
        UserNavbar(userViewModel)
        when (userViewModel.destination.value) {
            "Games" -> Games(userViewModel, homeNavController, gameViewModel)
            "Friends" -> Friends(userViewModel)
            "Settings" -> Settings(userNavController)
        }
    }
}


@Composable
fun Friends(viewModel: UserViewModel) {
    FriendsList(viewModel = viewModel)
    LaunchedEffect(true){
        viewModel.initializeFriendRequestListener()
    }
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
        UserNavbarItem(
            modifier = Modifier
                .weight(1f), "Games", games, userViewModel
        )
        UserNavbarItem(
            modifier = Modifier
                .weight(1f), "Friends", friends, userViewModel
        )
        UserNavbarItem(
            modifier = Modifier
                .weight(1f), "Settings", settings, userViewModel
        )
    }
}

@Composable
fun UserNavbarItem(modifier: Modifier,
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