package com.gomezdevlopment.chessnotationapp.view.game_screen.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel

@Composable
fun SoundFX(viewModel: GameViewModel) {
    val pieceSound = remember { viewModel.getPieceSound() }
    val checkSound = remember { viewModel.getCheckSound() }
    val captureSound = remember { viewModel.getCaptureSound() }
    val castlingSound = remember { viewModel.getCastlingSound() }
    val gameEndSound = remember { viewModel.getGameEndSound() }

    if (pieceSound.value) {
        LaunchedEffect(0) {
            pieceSound.value = false
            viewModel.playSoundPool("piece")
        }
    }
    if (checkSound.value) {
        LaunchedEffect(0) {
            checkSound.value = false
            viewModel.playSoundPool("check")
        }
    }
    if (captureSound.value) {
        LaunchedEffect(0) {
            captureSound.value = false
            viewModel.playSoundPool("capture")
        }
    }
    if (castlingSound.value) {
        LaunchedEffect(0) {
            castlingSound.value = false
            viewModel.playSoundPool("castle")
        }
    }
    if (gameEndSound.value) {
        LaunchedEffect(0) {
            gameEndSound.value = false
            viewModel.playSoundPool("end")
        }
    }
}