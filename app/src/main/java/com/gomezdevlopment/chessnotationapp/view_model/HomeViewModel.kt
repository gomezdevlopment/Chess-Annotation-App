package com.gomezdevlopment.chessnotationapp.view_model

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gomezdevlopment.chessnotationapp.model.HomeRepository
import java.io.File

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var homeRepository: HomeRepository
    private lateinit var whiteMovesMutableLiveData: MutableLiveData<ArrayList<String>>
    private lateinit var blackMovesMutableLiveData: MutableLiveData<ArrayList<String>>

    fun init() {
        homeRepository = HomeRepository.getHomeRepository()
        whiteMovesMutableLiveData = homeRepository.getWhiteMoves()
        blackMovesMutableLiveData = homeRepository.getBlackMoves()
    }

    fun getWhiteMoves(): MutableLiveData<ArrayList<String>> {
        return whiteMovesMutableLiveData
    }

    fun getBlackMoves(): MutableLiveData<ArrayList<String>> {
        return blackMovesMutableLiveData
    }

    fun addWhiteMove(move: String) {
        homeRepository.addWhiteMove(move)
        val moves = whiteMovesMutableLiveData.value
        whiteMovesMutableLiveData.postValue(moves)
    }

    fun addBlackMove(move: String) {
        homeRepository.addBlackMove(move)
        val moves = blackMovesMutableLiveData.value
        blackMovesMutableLiveData.postValue(moves)
    }

    fun createNewGameDialog(context: Context) {
        if(getWhiteMoves().value.isNullOrEmpty()){
            Toast.makeText(context, "No game is currently in progress.", Toast.LENGTH_SHORT).show()
        }else{
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Create a new game?")
            builder.setMessage("The current game will be deleted.")
            builder.setPositiveButton("Yes") { _, _ ->
                homeRepository.clearMoves()
                val whiteMoves = whiteMovesMutableLiveData.value
                whiteMovesMutableLiveData.postValue(whiteMoves)
                val blackMoves = blackMovesMutableLiveData.value
                blackMovesMutableLiveData.postValue(blackMoves)
            }
            builder.setNegativeButton("Cancel") { _, _ -> }
            builder.show()
        }
    }

    fun createPGNString(): String{
        return homeRepository.createPGNString()
    }
}