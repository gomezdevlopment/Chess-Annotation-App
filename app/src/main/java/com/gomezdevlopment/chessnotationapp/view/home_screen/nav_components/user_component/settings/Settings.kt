package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import com.gomezdevlopment.chessnotationapp.view.theming.*
import com.gomezdevlopment.chessnotationapp.view.userIcon
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel
import com.gomezdevlopment.chessnotationapp.view_model.UserViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun Settings(
    userNavController: NavController,
    homeNavController: NavController,
    userViewModel: UserViewModel
) {
    val animationSpeedOptions = listOf(
        "Fast" to 50,
        "Medium" to 250,
        "Slow" to 450,
    )

    val themeOptions = listOf("Light", "Dark", "System")
    val highlightStyles = listOf("Outline", "Solid")

    DeleteAccountDialog(userViewModel = userViewModel)

    Column(
        Modifier
            .padding(30.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SettingsItemButton(text = "Board Theme") {
            userNavController.navigate("boardThemes")
        }
        SettingsItemButton(text = "Pieces Theme") {
            userNavController.navigate("pieceThemes")
        }
        Text("Piece Animation Speed", modifier = Modifier.padding(10.dp))
        Row() {
            animationSpeedOptions.forEach {
                ChipItem(
                    text = it.first,
                    border = BorderStroke(
                        1.dp,
                        if (it.second == userViewModel.pieceAnimationSpeed) teal else Color.Transparent
                    )
                ) {
                    userViewModel.setPieceAnimationSpeed(it.second)
                }
            }
        }
        Text("Theme", modifier = Modifier.padding(10.dp))
        Row() {
            themeOptions.forEach {
                ChipItem(
                    text = it,
                    border = BorderStroke(
                        1.dp,
                        if (it == userViewModel.themeSelection) teal else Color.Transparent
                    )
                ) {
                    userViewModel.setTheme(it)
                }
            }
        }
        Text("Piece Highlight Styles", modifier = Modifier.padding(10.dp))
        Row() {
            highlightStyles.forEach {
                ChipItem(
                    text = it,
                    border = BorderStroke(
                        1.dp,
                        if (it == userViewModel.highlightStyle) teal else Color.Transparent
                    )
                ) {
                    userViewModel.setHighlightStyle(it)
                }
            }
        }

        Divider(
            color = Color.Black, modifier = Modifier
                .fillMaxWidth()
                .width(1.dp)
                .padding(0.dp, 30.dp, 0.dp, 0.dp)
        )

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = {
                userViewModel.signOut()
            }) {
                Text("Sign Out")
            }

            TextButton(onClick = {
                userViewModel.showDeleteAccountDialog.value = true
            }) {
                Text("Delete Account")
            }

            TextButton(onClick = {
                userViewModel.openPrivacyPolicy.value = true
            }) {
                if (userViewModel.openPrivacyPolicy.value) {
                    OpenPrivacyPolicy()
                    userViewModel.openPrivacyPolicy.value = false
                }
                Text("Privacy Policy")
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = "App Version: $appVersion")
        }
        Spacer(modifier = Modifier.height(100.dp))
    }


    if (userViewModel.signedOutStatusBarColorChange.value) {
        rememberSystemUiController().setStatusBarColor(if (isSystemInDarkTheme()) backgroundDark else background)
    }
}

@Composable
fun OpenPrivacyPolicy() {
    val uriHandler = LocalUriHandler.current
    uriHandler.openUri(privacyPolicy)
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
        modifier = Modifier
            .wrapContentWidth()
            .padding(10.dp, 0.dp),
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

@Composable
fun DeleteAccountDialog(userViewModel: UserViewModel) {
    if (userViewModel.showDeleteAccountDialog.value) {
        AlertDialog(
            onDismissRequest = { userViewModel.showDeleteAccountDialog.value = false },
            title = {
                Text(
                    text = "Are you sure you want to delete your account?",
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                   Text("All your data will be deleted, this cannot be undone.", color = MaterialTheme.colors.onBackground)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        userViewModel.deleteAccount()
                    }
                ) {
                    Text("Yes", color = MaterialTheme.colors.primary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        userViewModel.showDeleteAccountDialog.value = false
                    }
                ) {
                    Text("Cancel", color = MaterialTheme.colors.primary)
                }
            },
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.background
        )
    }
}
