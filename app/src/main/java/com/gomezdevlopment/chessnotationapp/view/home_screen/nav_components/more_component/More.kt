package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view.orange
import com.gomezdevlopment.chessnotationapp.view_model.SignOutViewModel

@Composable
fun UserScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .background(orange),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("User", fontSize = 30.sp)
    }
}

@Composable
fun Settings(signOutViewModel: SignOutViewModel, navController: NavController) {
    Column(verticalArrangement = Arrangement.Center) {
        Button(
            onClick = {
                signOutViewModel.signOut()
                navController.navigate(R.id.action_settingsFragment_to_signInFragment)
            }
        ) {
            Text("Sign Out")
        }
    }
}