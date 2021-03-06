package com.gomezdevlopment.chessnotationapp.model.game_logic

import androidx.compose.runtime.MutableState
import com.gomezdevlopment.chessnotationapp.model.data_classes.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.data_classes.GameState
import com.gomezdevlopment.chessnotationapp.model.data_classes.Square

class EndOfGameConditions(private val gameEndSound: MutableState<Boolean>) {

    fun checkCheckmate(
        allLegalMoves: MutableList<Square>,
        kingInCheck: Boolean,
        checkmate: MutableState<Boolean>
    ) {
        if (allLegalMoves.isEmpty() && kingInCheck) {
            checkmate.value = true
            gameEndSound.value = true
        }
    }

    fun checkStalemate(
        allLegalMoves: MutableList<Square>,
        kingInCheck: Boolean,
        stalemate: MutableState<Boolean>
    ) {
        if (allLegalMoves.isEmpty() && !kingInCheck) {
            stalemate.value = true
            gameEndSound.value = true
        }
    }

    fun checkFiftyMoveCount(
        allLegalMoves: MutableList<Square>,
        fiftyMoveCount: Int,
        fiftyMoveRule: MutableState<Boolean>
    ) {
        if (allLegalMoves.isNotEmpty()) {
            if (fiftyMoveCount == 100) {
                fiftyMoveRule.value = true
                gameEndSound.value = true
            }
        }
    }

    fun checkThreefoldRepetition(
        previousGameStates: List<GameState>,
        threeFoldRepetition: MutableState<Boolean>
    ) {
        val fenPositions = arrayListOf<String>()
        for (gameState in previousGameStates) {
            fenPositions.add(gameState.fenPosition)
        }
        var duplicatePositions: Int
        for (position in fenPositions.asReversed()) {
            duplicatePositions = 0
            for (positionToCompare in fenPositions.asReversed()) {
                if (position == positionToCompare) {
                    duplicatePositions++
                    if (duplicatePositions == 3) {
                        threeFoldRepetition.value = true
                        gameEndSound.value = true
                        break
                    }
                }
            }
            if (threeFoldRepetition.value) {
                break
            }
        }
    }

    fun checkInsufficientMaterial(
        piecesOnBoard: List<ChessPiece>,
        insufficientMaterial: MutableState<Boolean>
    ) {
        if (checkIfPlayerHasInsufficientMaterial(piecesOnBoard, "white")
            && checkIfPlayerHasInsufficientMaterial(piecesOnBoard, "black")
        ) {
            insufficientMaterial.value = true
            gameEndSound.value = true
        }
    }

    fun checkIfPlayerHasInsufficientMaterial(
        piecesOnBoard: List<ChessPiece>,
        playerColor: String
    ): Boolean {
        val currentPlayerPieces = arrayListOf<ChessPiece>()
        for (piece in piecesOnBoard) {
            when (piece.color) {
                playerColor -> {
                    currentPlayerPieces.add(piece)
                }
            }
        }

        when (currentPlayerPieces.size) {
            1 -> return true
            2 -> {
                for (piece in currentPlayerPieces) {
                    return when (piece.piece) {
                        "king" -> continue
                        "knight" -> true
                        "bishop" -> true
                        else -> false
                    }
                }
            }
        }

        return false
    }
}