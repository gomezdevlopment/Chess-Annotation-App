package com.gomezdevlopment.chessnotationapp.model

import androidx.lifecycle.ViewModel

class ChessPieceMovesRepository(): ViewModel() {

    //private var gameRepository: GameRepository = GameRepository.getGameRepository()
    private var enemyPieceBlocking = false
    private var squaresToBlock = mutableListOf<Square>()

    fun setSquaresToBlock(squares: ArrayList<Square>){
        println("working")
        println(squares)
        squaresToBlock = squares
    }

    fun checkLegalMoves(occupiedSquares: MutableMap<Square, ChessPiece>, piece: ChessPiece): List<Square>{
        var listOfMoves = mutableListOf<Square>()
        when (piece.piece) {
            "pawn" -> {
                listOfMoves = pawnMoves(occupiedSquares as HashMap<Square, ChessPiece>, piece) as MutableList<Square>
            }
            "rook" -> {
                listOfMoves = rookMoves(occupiedSquares as HashMap<Square, ChessPiece>, piece) as MutableList<Square>
            }
            "knight" -> {
                listOfMoves = knightMoves(occupiedSquares as HashMap<Square, ChessPiece>, piece) as MutableList<Square>
            }
            "bishop" -> {
                listOfMoves = bishopMoves(occupiedSquares as HashMap<Square, ChessPiece>, piece) as MutableList<Square>
            }
            "king" -> {
                listOfMoves = kingMoves(occupiedSquares as HashMap<Square, ChessPiece>, piece) as MutableList<Square>
            }
            "queen" -> {
                listOfMoves = queenMoves(occupiedSquares as HashMap<Square, ChessPiece>, piece) as MutableList<Square>
            }
        }
        return listOfMoves
    }

    private fun pawnMoves(occupiedSquares: HashMap<Square, ChessPiece>, piece: ChessPiece): List<Square>{
        val listOfMoves = mutableListOf<Square>()
        var moveSquare = Square(piece.square.rank+1, piece.square.file)
        if(piece.color == "black"){
            moveSquare = Square(piece.square.rank-1, piece.square.file)
        }
        if(!illegalMove(moveSquare, occupiedSquares, piece)){
            listOfMoves.add(moveSquare)
            if(piece.square.rank == 1 && piece.color == "white"){
                moveSquare = Square(piece.square.rank+2, piece.square.file)
                if(!illegalMove(moveSquare, occupiedSquares, piece)){
                    listOfMoves.add(moveSquare)
                }
            }else if(piece.square.rank == 6 && piece.color == "black"){
                moveSquare = Square(piece.square.rank-2, piece.square.file)
                if(!illegalMove(moveSquare, occupiedSquares, piece)){
                    listOfMoves.add(moveSquare)
                }
            }
        }
        if(piece.color == "white"){
            //Check for captures
            moveSquare = Square(piece.square.rank+1, piece.square.file-1)
            if(isCapture(moveSquare, occupiedSquares, piece)){
                if(!illegalMove(moveSquare, occupiedSquares, piece)){
                    listOfMoves.add(moveSquare)
                }
            }
            moveSquare = Square(piece.square.rank+1, piece.square.file+1)
            if(isCapture(moveSquare, occupiedSquares, piece)){
                if(!illegalMove(moveSquare, occupiedSquares, piece)){
                    listOfMoves.add(moveSquare)
                }
            }
        }else{
            //Check for captures
            moveSquare = Square(piece.square.rank-1, piece.square.file-1)
            if(isCapture(moveSquare, occupiedSquares, piece)){
                if(!illegalMove(moveSquare, occupiedSquares, piece)){
                    listOfMoves.add(moveSquare)
                }
            }
            moveSquare = Square(piece.square.rank-1, piece.square.file+1)
            if(isCapture(moveSquare, occupiedSquares, piece)){
                if(!illegalMove(moveSquare, occupiedSquares, piece)){
                    listOfMoves.add(moveSquare)
                }
            }
        }
        return listOfMoves
    }

    private fun rookMoves(occupiedSquares: HashMap<Square, ChessPiece>, piece: ChessPiece): List<Square>{
        val listOfMoves = mutableListOf<Square>()
        var moveSquare: Square
        enemyPieceBlocking = false
        for(rank in piece.square.rank+1..7){
            moveSquare = Square(rank, piece.square.file)
            if(!illegalMove(moveSquare, occupiedSquares, piece)){
                listOfMoves.add(moveSquare)
            }
            else{
                break
            }
        }
        enemyPieceBlocking = false
        for(rank in piece.square.rank-1 downTo 0){
            moveSquare = Square(rank, piece.square.file)
            if(!illegalMove(moveSquare, occupiedSquares, piece)){
                listOfMoves.add(moveSquare)
            }
            else{
                break
            }
        }
        enemyPieceBlocking = false
        for(file in piece.square.file+1..7){
            moveSquare = Square(piece.square.rank, file)
            if(!illegalMove(moveSquare, occupiedSquares, piece)){
                listOfMoves.add(moveSquare)
            }
            else{
                break
            }
        }
        enemyPieceBlocking = false
        for(file in piece.square.file-1 downTo 0){
            moveSquare = Square(piece.square.rank, file)
            if(!illegalMove(moveSquare, occupiedSquares, piece)){
                listOfMoves.add(moveSquare)
            }
            else{
                break
            }
        }
        return listOfMoves
    }

    private fun kingMoves(occupiedSquares: HashMap<Square, ChessPiece>, piece: ChessPiece): List<Square>{
        val listOfMoves = mutableListOf<Square>()
        var moveSquare: Square = Square(piece.square.rank+1, piece.square.file+1)
        if(!illegalMove(moveSquare, occupiedSquares, piece) || isCapture(moveSquare, occupiedSquares, piece)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(piece.square.rank+1, piece.square.file-1)
        if(!illegalMove(moveSquare, occupiedSquares, piece) || isCapture(moveSquare, occupiedSquares, piece)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(piece.square.rank+1, piece.square.file)
        if(!illegalMove(moveSquare, occupiedSquares, piece) || isCapture(moveSquare, occupiedSquares, piece)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(piece.square.rank-1, piece.square.file)
        if(!illegalMove(moveSquare, occupiedSquares, piece) || isCapture(moveSquare, occupiedSquares, piece)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(piece.square.rank, piece.square.file+1)
        if(!illegalMove(moveSquare, occupiedSquares, piece) || isCapture(moveSquare, occupiedSquares, piece)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(piece.square.rank, piece.square.file-1)
        if(!illegalMove(moveSquare, occupiedSquares, piece) || isCapture(moveSquare, occupiedSquares, piece)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(piece.square.rank-1, piece.square.file-1)
        if(!illegalMove(moveSquare, occupiedSquares, piece) || isCapture(moveSquare, occupiedSquares, piece)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(piece.square.rank-1, piece.square.file+1)
        if(!illegalMove(moveSquare, occupiedSquares, piece) || isCapture(moveSquare, occupiedSquares, piece)){
            listOfMoves.add(moveSquare)
        }
        return listOfMoves
    }

    private fun queenMoves(occupiedSquares: HashMap<Square, ChessPiece>, piece: ChessPiece): List<Square>{
        val listOfMoves = mutableListOf<Square>()
        var moveSquare: Square

        //Horizontal Moves
        enemyPieceBlocking = false
        for(rank in piece.square.rank+1..7){
            moveSquare = Square(rank, piece.square.file)
            if(!illegalMove(moveSquare, occupiedSquares, piece)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }
        enemyPieceBlocking = false
        for(rank in piece.square.rank-1 downTo 0){
            moveSquare = Square(rank, piece.square.file)
            if(!illegalMove(moveSquare, occupiedSquares, piece)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }
        enemyPieceBlocking = false
        for(file in piece.square.file+1..7){
            moveSquare = Square(piece.square.rank, file)
            if(!illegalMove(moveSquare, occupiedSquares, piece)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }
        enemyPieceBlocking = false
        for(file in piece.square.file-1 downTo 0){
            moveSquare = Square(piece.square.rank, file)
            if(!illegalMove(moveSquare, occupiedSquares, piece)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }

        //Diagonal Moves
        enemyPieceBlocking = false
        for(rank in piece.square.rank+1..7){
            moveSquare = Square(rank, piece.square.file+(rank-piece.square.rank))
            if(!illegalMove(moveSquare, occupiedSquares, piece)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }
        enemyPieceBlocking = false
        for(rank in piece.square.rank+1..7){
            moveSquare = Square(rank, piece.square.file-(rank-piece.square.rank))
            if(!illegalMove(moveSquare, occupiedSquares, piece)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }
        enemyPieceBlocking = false
        for(rank in piece.square.rank-1 downTo 0){
            moveSquare = Square(rank, piece.square.file+(rank-piece.square.rank))
            if(!illegalMove(moveSquare, occupiedSquares, piece)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }
        enemyPieceBlocking = false
        for(rank in piece.square.rank-1 downTo 0){
            moveSquare = Square(rank, piece.square.file-(rank-piece.square.rank))
            if(!illegalMove(moveSquare, occupiedSquares, piece)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }

        return listOfMoves
    }

    private fun bishopMoves(occupiedSquares: HashMap<Square, ChessPiece>, piece: ChessPiece): List<Square>{
        val listOfMoves = mutableListOf<Square>()
        var moveSquare: Square
        enemyPieceBlocking = false
        for(rank in piece.square.rank+1..7){
            moveSquare = Square(rank, piece.square.file+(rank-piece.square.rank))
            if(!illegalMove(moveSquare, occupiedSquares, piece)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }
        enemyPieceBlocking = false
        for(rank in piece.square.rank+1..7){
            moveSquare = Square(rank, piece.square.file-(rank-piece.square.rank))
            if(!illegalMove(moveSquare, occupiedSquares, piece)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }
        enemyPieceBlocking = false
        for(rank in piece.square.rank-1 downTo 0){
            moveSquare = Square(rank, piece.square.file+(rank-piece.square.rank))
            if(!illegalMove(moveSquare, occupiedSquares, piece)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }
        enemyPieceBlocking = false
        for(rank in piece.square.rank-1 downTo 0){
            moveSquare = Square(rank, piece.square.file-(rank-piece.square.rank))
            if(!illegalMove(moveSquare, occupiedSquares, piece)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }
        return listOfMoves
    }

    private fun knightMoves(occupiedSquares: HashMap<Square, ChessPiece>, piece: ChessPiece): List<Square> {
        val listOfMoves = mutableListOf<Square>()
        var moveSquare = Square(piece.square.rank+2, piece.square.file+1)
        if(!illegalMove(moveSquare, occupiedSquares, piece) || isCapture(moveSquare, occupiedSquares, piece)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(piece.square.rank+2, piece.square.file-1)
        if(!illegalMove(moveSquare, occupiedSquares, piece) || isCapture(moveSquare, occupiedSquares, piece)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(piece.square.rank-2, piece.square.file+1)
        if(!illegalMove(moveSquare, occupiedSquares, piece) || isCapture(moveSquare, occupiedSquares, piece)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(piece.square.rank-2, piece.square.file-1)
        if(!illegalMove(moveSquare, occupiedSquares, piece) || isCapture(moveSquare, occupiedSquares, piece)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(piece.square.rank+1, piece.square.file+2)
        if(!illegalMove(moveSquare, occupiedSquares, piece) || isCapture(moveSquare, occupiedSquares, piece)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(piece.square.rank-1, piece.square.file+2)
        if(!illegalMove(moveSquare, occupiedSquares, piece) || isCapture(moveSquare, occupiedSquares, piece)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(piece.square.rank+1, piece.square.file-2)
        if(!illegalMove(moveSquare, occupiedSquares, piece) || isCapture(moveSquare, occupiedSquares, piece)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(piece.square.rank-1, piece.square.file-2)
        if(!illegalMove(moveSquare, occupiedSquares, piece) || isCapture(moveSquare, occupiedSquares, piece)){
            listOfMoves.add(moveSquare)
        }
        return listOfMoves
    }

    private fun illegalMove(square: Square, occupiedSquares: HashMap<Square, ChessPiece>, piece: ChessPiece): Boolean {
        //prevent king from entering into check
        if(squaresToBlock.isNotEmpty()){
            if(squaresToBlock.contains(square) && piece.piece == "king") return true

            if(!squaresToBlock.contains(square) && piece.piece != "king") return true
        }
        if(square.rank > 7 || square.rank < 0 || square.file > 7 || square.file < 0) return true
        if(occupiedSquares.contains(square)){
            return if(isCapture(square, occupiedSquares, piece) && !enemyPieceBlocking){
                enemyPieceBlocking = true
                false
            }else{
                true
            }
        }
        return false
    }

    fun isCapture(square: Square, occupiedSquares: HashMap<Square, ChessPiece>, piece: ChessPiece): Boolean{
        var isEnemyPiece = false
        if(occupiedSquares.containsKey(square)){
            isEnemyPiece = occupiedSquares[square]?.color != piece.color
        }
        return isEnemyPiece
    }
}