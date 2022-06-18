package com.gomezdevlopment.chessnotationapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.databinding.ActivityMainBinding
import com.gomezdevlopment.chessnotationapp.model.data_classes.User
import com.gomezdevlopment.chessnotationapp.view_model.HomeViewModel
import com.google.firebase.firestore.DocumentReference


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object{
        lateinit var user: User
        lateinit var userColor: String
        lateinit var gameDocumentReference: DocumentReference
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ChessNotationApp)
        //installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onBackPressed() {
    }

    override fun onDestroy() {
        gameDocumentReference.delete().addOnFailureListener {
            println("fail to delete")
        }
        super.onDestroy()

    }
}