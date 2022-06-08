package com.gomezdevlopment.chessnotationapp.view.game_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
            viewModel.playSound(R.raw.piece_sound)
        }
    }
    if (checkSound.value) {
        LaunchedEffect(0) {
            checkSound.value = false
            viewModel.playSound(R.raw.check_sound)
        }
    }
    if (captureSound.value) {
        LaunchedEffect(0) {

            captureSound.value = false
            viewModel.playSound(R.raw.capture_sound)
        }
    }
    if (castlingSound.value) {
        LaunchedEffect(0) {
            castlingSound.value = false
            viewModel.playSound(R.raw.castling_sound)
        }
    }
    if (gameEndSound.value) {
        LaunchedEffect(0) {
            gameEndSound.value = false
            viewModel.playSound(R.raw.end_of_game_sound)
        }
    }
}