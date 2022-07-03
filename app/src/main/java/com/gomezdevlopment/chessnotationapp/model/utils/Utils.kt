package com.gomezdevlopment.chessnotationapp.model.utils

import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.userColor

class Utils {

    fun offsetX(size: Float, file: Int): Float {
        var offsetX = (size * (file).toFloat())
        if(userColor == "black"){
            offsetX = (size * (7-file).toFloat())
        }
        return offsetX
    }

    fun offsetY(size: Float, rank: Int): Float {
        var offsetY = (size * (7 - rank).toFloat())
        if(userColor == "black"){
            offsetY = (size * (rank).toFloat())
        }
        return offsetY
    }

    fun offsetX(size: Float, file: Int, color: String): Float {
        var offsetX = (size * (file).toFloat())
        if(color == "black"){
            offsetX = (size * (7-file).toFloat())
        }
        return offsetX
    }

    fun offsetY(size: Float, rank: Int, color: String): Float {
        var offsetY = (size * (7 - rank).toFloat())
        if(color == "black"){
            offsetY = (size * (rank).toFloat())
        }
        return offsetY
    }
}