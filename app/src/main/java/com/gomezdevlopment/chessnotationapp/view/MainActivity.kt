package com.gomezdevlopment.chessnotationapp.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.databinding.ActivityMainBinding
import com.gomezdevlopment.chessnotationapp.model.data_classes.OnlineGame
import com.gomezdevlopment.chessnotationapp.model.data_classes.User
import com.google.firebase.firestore.DocumentReference
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Flow


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object{
        var user: User? = null
        var userColor: String = "both"
        var gameDocumentReference: DocumentReference? = null
        var userDocumentReference: DocumentReference? = null
        var game: OnlineGame? = null


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ChessNotationApp)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onBackPressed() {
    }
}