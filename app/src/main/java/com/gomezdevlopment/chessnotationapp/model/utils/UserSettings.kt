package com.gomezdevlopment.chessnotationapp.model.utils

import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view.theming.alpha
import com.gomezdevlopment.chessnotationapp.view.theming.leipzig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


class UserSettings @Inject constructor(private val context: Application){
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    val BOARD_THEME = intPreferencesKey("board")
    val PIECE_THEME = stringPreferencesKey("pieceTheme")
    val chessBoardTheme = mutableStateOf(R.drawable.ic_chess_board_teal)
    val pieceTheme = mutableStateOf("Alpha")

    val pieceThemeMap: MutableState<Map<String, Int>> = mutableStateOf(alpha)

    private fun setPieceThemeMap() {
        when(pieceTheme.value){
            "Alpha" -> pieceThemeMap.value = alpha
            "Leipzig" -> pieceThemeMap.value = leipzig
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
            setPieceThemeMap()
        }
    }

    private suspend fun chessBoardTheme(): Int {
        val preferences = context.dataStore.data.first()
        return preferences[BOARD_THEME] ?: R.drawable.ic_chess_board_teal
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            chessBoardTheme.value = chessBoardTheme()
        }
    }
}