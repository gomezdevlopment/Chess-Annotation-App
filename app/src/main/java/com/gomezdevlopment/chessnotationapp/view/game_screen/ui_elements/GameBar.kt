package com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view.theming.tealDarker
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel

@Composable
fun GameBar(viewModel: GameViewModel) {
    Row(Modifier.height(40.dp).background(color = MaterialTheme.colors.surface), verticalAlignment = Alignment.Bottom) {
        GameBarItem(
            modifier = Modifier.weight(1f),
            drawableResource = R.drawable.ic_resign,
            contDescr = "Resign",
            padding = 15.dp
        ) {
            viewModel.openResignDialog.value = true
        }
        GameBarItem(
            modifier = Modifier.weight(1f),
            drawableResource = R.drawable.ic_draw_offer,
            contDescr = "Offer Draw",
            padding = 5.dp
        ) {
            viewModel.openDrawOfferDialog.value = true
        }

        GameBarItem(
            modifier = Modifier.weight(1f),
            drawableResource = R.drawable.ic_round_arrow_left,
            contDescr = "Previous Move",
            padding = 10.dp
        ) {
            viewModel.previousNotation()
        }
        GameBarItem(
            modifier = Modifier.weight(1f),
            drawableResource = R.drawable.ic_round_arrow_right,
            contDescr = "Next Move",
            padding = 10.dp
        ) {
            viewModel.nextNotation()
        }
    }
}

@Composable
fun ReviewGameBar(viewModel: GameViewModel, navController: NavController) {
    Row(Modifier.height(40.dp).background(color = MaterialTheme.colors.surface), verticalAlignment = Alignment.Bottom) {
        GameBarItem(
            modifier = Modifier.weight(1f),
            drawableResource = R.drawable.ic_home,
            contDescr = "Go Home",
            padding = 10.dp
        ) {
            navController.popBackStack()
        }
        GameBarItem(
            modifier = Modifier.weight(1f),
            drawableResource = R.drawable.ic_export,
            contDescr = "Export",
            padding = 10.dp
        ) {
            viewModel.exportGameFile()
        }
        GameBarItem(
            modifier = Modifier.weight(1f),
            drawableResource = R.drawable.ic_round_arrow_left,
            contDescr = "Previous Move",
            padding = 10.dp
        ) {
            viewModel.previousNotation()
        }
        GameBarItem(
            modifier = Modifier.weight(1f),
            drawableResource = R.drawable.ic_round_arrow_right,
            contDescr = "Next Move",
            padding = 10.dp
        ) {
            viewModel.nextNotation()
        }
    }
}

@Composable
fun LocalGameBar(viewModel: GameViewModel, navController: NavController) {
    Row(Modifier.height(40.dp).background(color = MaterialTheme.colors.surface), verticalAlignment = Alignment.Bottom) {
        GameBarItem(
            modifier = Modifier.weight(1f),
            drawableResource = R.drawable.ic_home,
            contDescr = "Go Home",
            padding = 10.dp
        ) {
            navController.popBackStack()
        }
        GameBarItem(
            modifier = Modifier.weight(1f),
            drawableResource = R.drawable.ic_round_arrow_left,
            contDescr = "Previous Move",
            padding = 10.dp
        ) {
            viewModel.previousNotation()
        }
        GameBarItem(
            modifier = Modifier.weight(1f),
            drawableResource = R.drawable.ic_round_arrow_right,
            contDescr = "Next Move",
            padding = 10.dp
        ) {
            viewModel.nextNotation()
        }
    }
}

@Composable
fun GameBarItem(
    modifier: Modifier,
    drawableResource: Int,
    contDescr: String,
    padding: Dp,
    onClick: () -> Unit
) {
    val icon: ImageVector =
        ImageVector.vectorResource(id = drawableResource)
    Column(
        verticalArrangement = Arrangement.Center
        ,modifier = modifier
            .clickable {
                onClick()
            }
            .fillMaxHeight()

    ) {
        Icon(
            imageVector = icon,
            contentDescription = contDescr,
            tint = MaterialTheme.colors.primary,
            modifier = Modifier
                .height(50.dp)
                .padding(padding)
                .aspectRatio(1f)
                .align(Alignment.CenterHorizontally)
        )
    }
}