package com.gomezdevlopment.chessnotationapp.view.home_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gomezdevlopment.chessnotationapp.view.theming.tealDarker
import com.gomezdevlopment.chessnotationapp.view.theming.textWhite
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.NavComponent

@Composable
fun BottomNavBar(navController: NavHostController) {
    val listOfNavComponents = listOf(
        NavComponent.Play,
        NavComponent.Puzzles,
        NavComponent.User
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomNavigation(backgroundColor = MaterialTheme.colors.surface, modifier = Modifier.height(60.dp)) {
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
        selectedContentColor = MaterialTheme.colors.primary,
        unselectedContentColor = textWhite
    )
}