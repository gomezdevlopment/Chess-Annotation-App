package com.gomezdevlopment.chessnotationapp.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeRepository() : ViewModel() {

    private val whiteMovesData: ArrayList<String> = arrayListOf()
    private val blackMovesData: ArrayList<String> = arrayListOf()

    companion object {
        @Volatile
        private var INSTANCE: HomeRepository? = null

        fun getHomeRepository(): HomeRepository {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = HomeRepository()
                INSTANCE = instance
                return instance
            }
        }
    }

    fun getWhiteMoves(): MutableLiveData<ArrayList<String>> {
        return MutableLiveData(whiteMovesData)
    }

    fun getBlackMoves(): MutableLiveData<ArrayList<String>>{
        return MutableLiveData(blackMovesData)
    }

    fun addWhiteMove(move: String){
        whiteMovesData.add(move)
    }

    fun addBlackMove(move: String){
        blackMovesData.add(move)
    }

    fun clearMoves(){
        whiteMovesData.clear()
        blackMovesData.clear()
    }
}