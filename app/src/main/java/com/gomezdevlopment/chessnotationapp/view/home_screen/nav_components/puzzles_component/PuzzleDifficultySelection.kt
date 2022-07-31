package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.puzzles_component

import androidx.compose.foundation.background
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
import com.gomezdevlopment.chessnotationapp.view_model.PuzzleViewModel



@Composable
fun PuzzleDifficultySelectionScreen(
    puzzleViewModel: PuzzleViewModel,
    navController: NavController
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(20.dp, 5.dp)
    ) {
        Text(
            text = "Select Puzzle Difficulty",
            modifier = Modifier.padding(5.dp, 20.dp),
            fontSize = 30.sp,
            color = MaterialTheme.colors.primary
        )
        DifficultySelectionItem(text = "Beginner", difficultyLevel = 1) {
            puzzleViewModel.initializePuzzles(1)
            navController.navigate("puzzles")
        }
        DifficultySelectionItem(text = "Intermediate", difficultyLevel = 2) {
            puzzleViewModel.initializePuzzles(2)
            navController.navigate("puzzles")
        }
        DifficultySelectionItem(text = "Advanced", difficultyLevel = 3) {
            puzzleViewModel.initializePuzzles(3)
            navController.navigate("puzzles")
        }
    }
}

@Composable
fun DifficultySelectionItem(text: String, difficultyLevel: Int, onClick: () -> Unit) {
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
            Row() {
                for (i in 1..difficultyLevel) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_star),
                        contentDescription = "star",
                        Modifier.size(12.dp),
                        tint = MaterialTheme.colors.primary
                    )
                }
                Spacer(modifier = Modifier.width(15.dp))
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_navigation_arrow),
                    contentDescription = text,
                    Modifier.size(12.dp),
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(15.dp))
}