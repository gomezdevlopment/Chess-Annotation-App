package com.gomezdevlopment.chessnotationapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.databinding.ActivityMainBinding
import com.gomezdevlopment.chessnotationapp.realtime_database.OnlineGame
import com.gomezdevlopment.chessnotationapp.realtime_database.User
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    companion object{
        var user: User? = null
        var userColor: String = "both"
        var game: OnlineGame? = null
        var gameID: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ChessNotationApp)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}