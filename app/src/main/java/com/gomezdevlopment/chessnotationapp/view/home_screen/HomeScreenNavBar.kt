package com.gomezdevlopment.chessnotationapp.view.home_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gomezdevlopment.chessnotationapp.view.tealDarker
import com.gomezdevlopment.chessnotationapp.view.textWhite
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.NavComponent
import com.gomezdevlopment.chessnotationapp.view.navBarWhite

@Composable
fun BottomNavBar(navController: NavHostController) {
    val listOfNavComponents = listOf(
        NavComponent.Play,
        NavComponent.Puzzles,
        NavComponent.User
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomNavigation(backgroundColor = navBarWhite) {
        listOfNavComponents.forEach { component ->
            AddItem(component, currentDestination, navController)
        }
    }
}

@Composable
fun RowScope.AddItem(
    component: NavComponent,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    BottomNavigationItem(
        label = {
            Text(text = component.title)
        },
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(id = component.icon),
                contentDescription = component.title
            )
        },
        selected = currentDestination?.hierarchy?.any {
            it.route == component.route
        } == true,
        onClick = {
            if (currentDestination?.route != component.route)
                navController.navigate(component.route)
        },
        selectedContentColor = tealDarker,
        unselectedContentColor = textWhite
    )
}