package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components

import com.gomezdevlopment.chessnotationapp.R

sealed class NavComponent(
    val route: String,
    val title: String,
    val icon: Int
){
    object Play: NavComponent("play", "Play", R.drawable.ic_chess_board)
    object Puzzles: NavComponent("puzzles", "Puzzles", R.drawable.ic_puzzle)
    object User: NavComponent("user", "User", R.drawable.ic_user_icon)
}
