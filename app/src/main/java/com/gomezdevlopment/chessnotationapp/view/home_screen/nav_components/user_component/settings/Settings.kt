
package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view.*


@Composable
fun Settings(userNavController: NavController) {
    Column(Modifier.padding(30.dp)) {
        SettingsItemButton(text = "Board Theme") {
            userNavController.navigate("boardThemes")
        }
        SettingsItemButton(text = "Pieces Theme") {
            userNavController.navigate("boardThemes")
        }
        SettingsItemButton(text = "Theme") {
            userNavController.navigate("boardThemes")
        }
    }
}

@Composable
fun SettingsItemButton(text: String, onClick: () -> Unit) {
    Button(
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier
            .fillMaxWidth(),
        onClick = { onClick() },
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