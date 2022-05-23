package com.gomezdevlopment.chessnotationapp.model

class ChessPiece(
    val color: String,
    private val piece: String,
    val pieceDrawable: Int,
    var square: Square
) {

    private var enemyPieceBlocking = false

    fun checkLegalMoves(occupiedSquares: MutableMap<Square, ChessPiece>): List<Square>{
        var listOfMoves = mutableListOf<Square>()
        when (piece) {
            "pawn" -> {
                listOfMoves = pawnMoves(occupiedSquares as HashMap<Square, ChessPiece>) as MutableList<Square>
            }
            "rook" -> {
                listOfMoves = rookMoves(occupiedSquares as HashMap<Square, ChessPiece>) as MutableList<Square>
            }
            "knight" -> {
                listOfMoves = knightMoves(occupiedSquares as HashMap<Square, ChessPiece>) as MutableList<Square>
            }
            "bishop" -> {
                listOfMoves = bishopMoves(occupiedSquares as HashMap<Square, ChessPiece>) as MutableList<Square>
            }
            "king" -> {
                listOfMoves = kingMoves(occupiedSquares as HashMap<Square, ChessPiece>) as MutableList<Square>
            }
            "queen" -> {
                listOfMoves = queenMoves(occupiedSquares as HashMap<Square, ChessPiece>) as MutableList<Square>
            }
        }
        return listOfMoves
    }

    private fun pawnMoves(occupiedSquares: HashMap<Square, ChessPiece>): List<Square>{
        val listOfMoves = mutableListOf<Square>()
        var moveSquare = Square(square.rank+1, square.file)
        if(color == "black"){
            moveSquare = Square(square.rank-1, square.file)
        }
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
            if(square.rank == 1 && color == "white"){
                moveSquare = Square(square.rank+2, square.file)
                if(!illegalMove(moveSquare, occupiedSquares)){
                    listOfMoves.add(moveSquare)
                }
            }else if(square.rank == 6 && color == "black"){
                moveSquare = Square(square.rank-2, square.file)
                if(!illegalMove(moveSquare, occupiedSquares)){
                    listOfMoves.add(moveSquare)
                }
            }
        }
        if(color == "white"){
            //Check for captures
            moveSquare = Square(square.rank+1, square.file-1)
            if(isCapture(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }
            moveSquare = Square(square.rank+1, square.file+1)
            if(isCapture(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }
        }else{
            //Check for captures
            moveSquare = Square(square.rank-1, square.file-1)
            if(isCapture(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }
            moveSquare = Square(square.rank-1, square.file+1)
            if(isCapture(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }
        }
        return listOfMoves
    }

    private fun rookMoves(occupiedSquares: HashMap<Square, ChessPiece>): List<Square>{
        val listOfMoves = mutableListOf<Square>()
        var moveSquare: Square
        enemyPieceBlocking = false
        for(rank in square.rank+1..7){
            moveSquare = Square(rank, square.file)
            if(!illegalMove(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }
            else{
                break
            }
        }
        enemyPieceBlocking = false
        for(rank in square.rank-1 downTo 0){
            moveSquare = Square(rank, square.file)
            if(!illegalMove(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }
            else{
                break
            }
        }
        enemyPieceBlocking = false
        for(file in square.file+1..7){
            moveSquare = Square(square.rank, file)
            if(!illegalMove(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }
            else{
                break
            }
        }
        enemyPieceBlocking = false
        for(file in square.file-1 downTo 0){
            moveSquare = Square(square.rank, file)
            if(!illegalMove(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }
            else{
                break
            }
        }
        return listOfMoves
    }

    private fun kingMoves(occupiedSquares: HashMap<Square, ChessPiece>): List<Square>{
        val listOfMoves = mutableListOf<Square>()
        var moveSquare: Square = Square(square.rank+1, square.file+1)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank+1, square.file-1)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank+1, square.file)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank-1, square.file)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank, square.file+1)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank, square.file-1)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank-1, square.file-1)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank-1, square.file+1)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        return listOfMoves
    }

    private fun queenMoves(occupiedSquares: HashMap<Square, ChessPiece>): List<Square>{
        val listOfMoves = mutableListOf<Square>()
        var moveSquare: Square

        //Horizontal Moves
        enemyPieceBlocking = false
        for(rank in square.rank+1..7){
            moveSquare = Square(rank, square.file)
            if(!illegalMove(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }
        enemyPieceBlocking = false
        for(rank in square.rank-1 downTo 0){
            moveSquare = Square(rank, square.file)
            if(!illegalMove(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }
        enemyPieceBlocking = false
        for(file in square.file+1..7){
            moveSquare = Square(square.rank, file)
            if(!illegalMove(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }
        enemyPieceBlocking = false
        for(file in square.file-1 downTo 0){
            moveSquare = Square(square.rank, file)
            if(!illegalMove(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }

        //Diagonal Moves
        enemyPieceBlocking = false
        for(rank in square.rank+1..7){
            moveSquare = Square(rank, square.file+(rank-square.rank))
            if(!illegalMove(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }
        enemyPieceBlocking = false
        for(rank in square.rank+1..7){
            moveSquare = Square(rank, square.file-(rank-square.rank))
            if(!illegalMove(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }
        enemyPieceBlocking = false
        for(rank in square.rank-1 downTo 0){
            moveSquare = Square(rank, square.file+(rank-square.rank))
            if(!illegalMove(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }
        enemyPieceBlocking = false
        for(rank in square.rank-1 downTo 0){
            moveSquare = Square(rank, square.file-(rank-square.rank))
            if(!illegalMove(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }

        return listOfMoves
    }

    private fun bishopMoves(occupiedSquares: HashMap<Square, ChessPiece>): List<Square>{
        val listOfMoves = mutableListOf<Square>()
        var moveSquare: Square
        enemyPieceBlocking = false
        for(rank in square.rank+1..7){
            moveSquare = Square(rank, square.file+(rank-square.rank))
            if(!illegalMove(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }
        enemyPieceBlocking = false
        for(rank in square.rank+1..7){
            moveSquare = Square(rank, square.file-(rank-square.rank))
            if(!illegalMove(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }
        enemyPieceBlocking = false
        for(rank in square.rank-1 downTo 0){
            moveSquare = Square(rank, square.file+(rank-square.rank))
            if(!illegalMove(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }
        enemyPieceBlocking = false
        for(rank in square.rank-1 downTo 0){
            moveSquare = Square(rank, square.file-(rank-square.rank))
            if(!illegalMove(moveSquare, occupiedSquares)){
                listOfMoves.add(moveSquare)
            }else{
                break
            }
        }
        return listOfMoves
    }

    private fun knightMoves(occupiedSquares: HashMap<Square, ChessPiece>): List<Square> {
        val listOfMoves = mutableListOf<Square>()
        var moveSquare = Square(square.rank+2, square.file+1)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank+2, square.file-1)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank-2, square.file+1)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank-2, square.file-1)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank+1, square.file+2)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank-1, square.file+2)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank+1, square.file-2)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        moveSquare = Square(square.rank-1, square.file-2)
        if(!illegalMove(moveSquare, occupiedSquares)){
            listOfMoves.add(moveSquare)
        }
        return listOfMoves
    }

    private fun illegalMove(square: Square, occupiedSquares: HashMap<Square, ChessPiece>): Boolean {
        if(square.rank > 7 || square.rank < 0 || square.file > 7 || square.file < 0) return true
        if(occupiedSquares.contains(square)){
            return if(isCapture(square, occupiedSquares) && !enemyPieceBlocking){
                enemyPieceBlocking = true
                false
            }else{
                true
            }
        }
        return false
    }

    private fun isCapture(square: Square, occupiedSquares: HashMap<Square, ChessPiece>): Boolean{
        var isEnemyPiece = false
        if(occupiedSquares.containsKey(square)){
            isEnemyPiece = occupiedSquares[square]?.color != color
        }
        return isEnemyPiece
    }
}




