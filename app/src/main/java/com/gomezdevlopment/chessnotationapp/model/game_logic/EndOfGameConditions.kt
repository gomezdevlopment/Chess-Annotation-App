package com.gomezdevlopment.chessnotationapp.model.game_logic

import androidx.compose.runtime.MutableState
import com.gomezdevlopment.chessnotationapp.model.ChessPiece
import com.gomezdevlopment.chessnotationapp.model.GameState
import com.gomezdevlopment.chessnotationapp.model.Square

class EndOfGameConditions(private val gameEndSound: MutableState<Boolean>) {

    fun checkCheckmate(allLegalMoves: MutableList<Square>, kingInCheck: Boolean, checkmate: MutableState<Boolean>) {
        if (allLegalMoves.isEmpty() && kingInCheck) {
            checkmate.value = true
            gameEndSound.value = true
        }
    }

    fun checkStalemate(allLegalMoves: MutableList<Square>, kingInCheck: Boolean, stalemate: MutableState<Boolean>) {
        if (allLegalMoves.isEmpty() && !kingInCheck) {
            stalemate.value = true
            gameEndSound.value = true
        }
    }

    fun checkFiftyMoveCount(allLegalMoves: MutableList<Square>, fiftyMoveCount: Int, fiftyMoveRule: MutableState<Boolean>) {
        if (allLegalMoves.isNotEmpty()) {
            if (fiftyMoveCount == 100) {
                fiftyMoveRule.value = true
                gameEndSound.value = true
            }
        }
    }

    fun checkThreefoldRepetition(previousGameStates: List<GameState>, threeFoldRepetition: MutableState<Boolean>) {
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

    fun checkInsufficientMaterial(piecesOnBoard: List<ChessPiece>, insufficientMaterial: MutableState<Boolean>) {
        if (piecesOnBoard.size == 2) {
            insufficientMaterial.value = (piecesOnBoard.size == 2)
        } else {
            var blackBishopsAndKnights = 0
            var whiteBishopsAndKnights = 0
            var onlyKnightsOrBishops = true
            if (piecesOnBoard.size < 5) {
                for (piece in piecesOnBoard) {
                    when (piece.color) {
                        "white" -> {
                            whiteBishopsAndKnights += when (piece.piece) {
                                "bishop" -> 1
                                "knight" -> 1
                                "king" -> 0
                                else -> {
                                    onlyKnightsOrBishops = false
                                    break
                                }
                            }
                        }
                        "black" -> {
                            blackBishopsAndKnights += when (piece.piece) {
                                "bishop" -> 1
                                "knight" -> 1
                                "king" -> 0
                                else -> {
                                    onlyKnightsOrBishops = false
                                    break
                                }
                            }
                        }
                    }
                }
                if (whiteBishopsAndKnights < 2 && blackBishopsAndKnights < 2 && onlyKnightsOrBishops) {
                    insufficientMaterial.value = true
                    gameEndSound.value = true
                }
            }
        }
    }
}