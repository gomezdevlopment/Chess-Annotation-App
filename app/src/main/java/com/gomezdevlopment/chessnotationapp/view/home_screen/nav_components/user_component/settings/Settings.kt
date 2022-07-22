
package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view.theming.*
import com.gomezdevlopment.chessnotationapp.view_model.UserViewModel


@Composable
fun Settings(userNavController: NavController, userViewModel: UserViewModel) {
    val animationSpeedOptions = listOf(
        "Fast" to 50,
        "Medium" to 250,
        "Slow" to 450,
    )

    val themeOptions = listOf("Light", "Dark", "System",)
    val highlightStyles = listOf("Outline", "Solid")

    Column(Modifier.padding(30.dp)) {
        SettingsItemButton(text = "Board Theme") {
            userNavController.navigate("boardThemes")
        }
        SettingsItemButton(text = "Pieces Theme") {
            userNavController.navigate("pieceThemes")
        }
        Text("Piece Animation Speed", modifier = Modifier.padding(10.dp))
        Row() {
            animationSpeedOptions.forEach {
                ChipItem(text = it.first,
                    border = BorderStroke(1.dp, if (it.second == userViewModel.pieceAnimationSpeed) teal else Color.Transparent)) {
                    userViewModel.setPieceAnimationSpeed(it.second)
                }
            }
        }
        Text("Theme", modifier = Modifier.padding(10.dp))
        Row() {
            themeOptions.forEach {
                ChipItem(text = it,
                    border = BorderStroke(1.dp, if (it == userViewModel.themeSelection) teal else Color.Transparent)) {
                        userViewModel.setTheme(it)
                }
            }
        }
        Text("Piece Highlight Styles", modifier = Modifier.padding(10.dp))
        Row() {
            highlightStyles.forEach {
                ChipItem(text = it,
                    border = BorderStroke(1.dp, if (it == userViewModel.highlightStyle) teal else Color.Transparent)) {
                    userViewModel.setHighlightStyle(it)
                }
            }
        }

    }
}

@Composable
fun SettingsItemButton(text: String, onClick: () -> Unit) {
    Button(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth(),
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)
    ) {
        Column() {
            Text(
                text = text,
                fontSize = 16.sp,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(5.dp)
            )
        }
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_navigation_arrow),
                contentDescription = text,
                Modifier.size(12.dp),
                tint = MaterialTheme.colors.primary
            )
        }
    }

    Spacer(modifier = Modifier.height(15.dp))
}

@Composable
fun ChipItem(text: String, border: BorderStroke, onClick: () -> Unit) {
    Button(
        border = border,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.wrapContentWidth().padding(10.dp, 0.dp),
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)
    ) {
        Column() {
            Text(
                text = text,
                fontSize = 16.sp,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(15.dp))
}
