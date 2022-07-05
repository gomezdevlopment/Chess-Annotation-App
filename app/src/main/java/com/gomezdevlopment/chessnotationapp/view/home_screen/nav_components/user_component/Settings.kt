package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view.cardWhite
import com.gomezdevlopment.chessnotationapp.view.tealDarker

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