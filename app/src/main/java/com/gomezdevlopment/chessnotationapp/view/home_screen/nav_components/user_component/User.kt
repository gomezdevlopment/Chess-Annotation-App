package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
fun UserScreen(userViewModel: UserViewModel, navController: NavController, gameViewModel: GameViewModel) {
    Column(Modifier.fillMaxSize()) {
        User()
        UserNavbar(userViewModel)
        when (userViewModel.destination.value) {
            "Games" -> Games(userViewModel, navController, gameViewModel)
            "Friends" -> Friends(userViewModel)
            "Settings" -> Settings(navController, userViewModel)
        }
    }
}


@Composable
fun Friends(viewModel: UserViewModel) {
    FriendsList(viewModel = viewModel)
}

@Composable
fun User() {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
            .padding(20.dp),
    ) {
        Column() {
            Text(
                user?.username ?: "Username",
                Modifier.padding(10.dp, 5.dp),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = MaterialTheme.colors.onBackground
            )
            val wins = user?.wins
            val losses = user?.losses
            val draws = user?.draws
            Text("$wins Wins/$losses Losses/$draws Draws", Modifier.padding(10.dp, 5.dp), color = MaterialTheme.colors.onBackground)
        }
    }
}

@Composable
fun UserNavbar(userViewModel: UserViewModel) {
    val settings: Boolean = userViewModel.destination.value == "Settings"
    val games: Boolean = userViewModel.destination.value == "Games"
    val friends: Boolean = userViewModel.destination.value == "Friends"

    val navItems = listOf("Games" to games, "Friends" to friends, "Settings" to settings)
    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()){
        navItems.forEach {
            UserNavbarItem(Modifier,it.first, it.second, userViewModel)
        }
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
            .padding(10.dp, 5.dp)
            .wrapContentWidth(),
        onClick = {viewModel.destination.value = text},
        colors = ButtonDefaults.buttonColors(backgroundColor = if (isSelected) MaterialTheme.colors.surface else MaterialTheme.colors.background),
        elevation = ButtonDefaults.elevation(0.dp)
    ) {
        Column() {
            Text(
                text = text,
                fontSize = if (isSelected) 22.sp else 12.sp,
                color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(0.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}