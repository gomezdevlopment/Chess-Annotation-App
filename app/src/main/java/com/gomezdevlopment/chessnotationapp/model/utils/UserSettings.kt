package com.gomezdevlopment.chessnotationapp.model.utils

import android.app.Application
import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view.theming.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSettings @Inject constructor(private val context: Application): ViewModel(){

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    val BOARD_THEME = intPreferencesKey("board")
    val PIECE_THEME = stringPreferencesKey("pieceTheme")
    val THEME = stringPreferencesKey("theme")
    val PIECE_ANIMATION_SPEED = intPreferencesKey("pieceAnimationSpeed")
    val HIGHLIGHT_STYLE = stringPreferencesKey("highlightStyle")
    val chessBoardTheme = mutableStateOf(R.drawable.ic_chess_board_teal)
    val pieceTheme = mutableStateOf("Alpha")
    val pieceThemeMap: MutableState<Map<String, Int>> = mutableStateOf(alpha)
    val pieceAnimationSpeed = mutableStateOf(100)
    val theme = mutableStateOf("System")
    val isDarkThemeSelected = mutableStateOf(false)
    val highlightStyle = mutableStateOf("outline")

    private fun setPieceThemeMap(theme: String) {
        when(theme){
            "Alpha" -> pieceThemeMap.value = alpha
            "Leipzig" -> pieceThemeMap.value = leipzig
            "Chess7" -> pieceThemeMap.value = chess7
            "Checkers" -> pieceThemeMap.value = checkers
        }
    }

    suspend fun setBoardTheme(board: Int) {
        context.dataStore.edit { settings ->
            settings[BOARD_THEME] = board
            chessBoardTheme.value = board
        }
    }

    suspend fun setPieceTheme(theme: String){
        context.dataStore.edit { settings ->
            settings[PIECE_THEME] = theme
            pieceTheme.value = theme
            setPieceThemeMap(theme)
        }
    }

    suspend fun setTheme(selectedTheme: String){
        context.dataStore.edit { settings ->
            settings[THEME] = selectedTheme
            theme.value = selectedTheme
            if(selectedTheme == "Dark"){
                isDarkThemeSelected.value = true
            }else if(selectedTheme == "Light"){
                isDarkThemeSelected.value = false
            }
        }
    }

    suspend fun setHighlightStyle(highlightStyle: String){
        context.dataStore.edit { settings ->
            settings[HIGHLIGHT_STYLE] = highlightStyle
            this.highlightStyle.value = highlightStyle
        }
    }

    suspend fun setPieceAnimationSpeed(speed: Int){
        context.dataStore.edit { settings ->
            settings[PIECE_ANIMATION_SPEED] = speed
            pieceAnimationSpeed.value = speed
        }
    }

    private suspend fun chessBoardTheme(): Int {
        val preferences = context.dataStore.data.first()
        return preferences[BOARD_THEME] ?: tealBoard
    }

    private suspend fun pieceTheme(): String {
        val preferences = context.dataStore.data.first()
        return preferences[PIECE_THEME] ?: "Alpha"
    }

    private suspend fun theme(): String {
        val preferences = context.dataStore.data.first()
        return preferences[THEME] ?: "System"
    }

    private suspend fun highlightStyle(): String {
        val preferences = context.dataStore.data.first()
        return preferences[HIGHLIGHT_STYLE] ?: "Outline"
    }

    private suspend fun pieceAnimationSpeed(): Int {
        val preferences = context.dataStore.data.first()
        return preferences[PIECE_ANIMATION_SPEED] ?: 50
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            chessBoardTheme.value = chessBoardTheme()
            pieceTheme.value = pieceTheme()
            pieceAnimationSpeed.value = pieceAnimationSpeed()
            theme.value = theme()
            highlightStyle.value = highlightStyle()
            isDarkThemeSelected.value = false
            if(theme.value == "Dark"){
                isDarkThemeSelected.value = true
            }
            setPieceThemeMap(pieceTheme.value)
        }
    }
}