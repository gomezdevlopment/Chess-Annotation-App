package com.gomezdevlopment.chessnotationapp.model.game_logic

class ChessEngine {
    var board: List<Char> = listOf(
        'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o',
        'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o',
        'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o',
        'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o',
        'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o',
        'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o',
        'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o',
        'R', 'B', 'N', 'Q', 'K', 'B', 'N', 'R', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o',
    )

    var coordinates: List<String> = listOf(
        "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
        "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
        "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
        "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
        "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
        "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
        "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
        "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"
    )

    val coord: HashMap<String, Int> = hashMapOf()

    private fun setCoordinates() {
        coordinates.forEachIndexed() { index, coordinate ->
            coord[coordinate] = index
        }
    }

    init {
        setCoordinates()
    }

    var blackKing = 0b00001000_00000000_00000000_00000000_00000000_00000000_00000000_00000000u
    var blackRooks = 0b10000001_00000000_00000000_00000000_00000000_00000000_00000000_00000000u
    var blackKnights = 0b01000010_00000000_00000000_00000000_00000000_00000000_00000000_00000000u
    var blackBishops = 0b00100100_00000000_00000000_00000000_00000000_00000000_00000000_00000000u
    var blackQueens = 0b00010000_00000000_00000000_00000000_00000000_00000000_00000000_00000000u
    var blackPawns = 0b00000000_11111111_00000000_00000000_00000000_00000000_00000000_00000000u

    var whiteKing = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00001000u
    var whiteRooks = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_10000001u
    var whiteKnights = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_01000010u
    var whiteBishops = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00100100u
    var whiteQueens = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00010000u
    var whitePawns = 0b00000000_00000000_00000000_00000000_00000000_00000000_11111111_00000000u

    var whitePieces = 0b00000000_00000000_00000000_00000000_00000000_00000000_11111111_11111111u
    var blackPieces = 0b11111111_11111111_00000000_00000000_00000000_00000000_00000000_00000000u

    var allPieces = 0b11111111_11111111_00000000_00000000_00000000_00000000_11111111_11111111u

    fun moves(index: Int) {
        val piece = board[index]

        when (piece) {
            'P' -> {

            }
        }
    }

    fun whitePawnMoves(index: Int) {
        val moves = mutableListOf<Int>()
        if (index >= coord["a7"]!!) {
            moves.add(index - 8)
            if (index >= coord["a2"]!! && index <= coord["h2"]!!) {
                moves.add(index - 16)
            }
        }
    }

    fun rookMoves(index: Int) {
        val pseudoLegalMoves = mutableListOf<Int>()
        val moves = mutableListOf<Int>()
        for (i in 1..8) {
            val moveUp = index - (i * 16)
            val moveDown = index + (i * 16)
            val moveRight = index + i
            val moveLeft = index - i
            pseudoLegalMoves.addAll(listOf(moveUp, moveDown, moveRight, moveLeft))
        }

        pseudoLegalMoves.forEach { move ->
            if (board[move] != 'o')
                moves.add(move)
        }
    }

    fun bishopMoves(index: Int, piece: Char) {
        val pseudoLegalMoves = mutableListOf<Int>()
        val moves = mutableListOf<Int>()
        for (i in 1..8) {
            val moveUpRight = index - (i * 17)
            val moveUpLeft = index - (i * 15)
            val moveDownRight = index + (i * 17)
            val moveDownLeft = index + (i * 15)
            pseudoLegalMoves.addAll(listOf(moveUpRight, moveUpLeft, moveDownRight, moveDownLeft))
        }

        pseudoLegalMoves.forEach { move ->
            val boardMove = board[move]
            if (boardMove != 'o'){
                if(!pieceBlocking(boardMove, piece)){
                    moves.add(move)
                }
            }
        }
    }

    private fun pieceBlocking(boardMove: Char, piece: Char): Boolean {
        if(boardMove.isLowerCase() && piece.isLowerCase()) return true
        if(!boardMove.isLowerCase() && !piece.isLowerCase()) return true
        return false
    }
}
