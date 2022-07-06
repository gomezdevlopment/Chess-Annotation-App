package com.gomezdevlopment.chessnotationapp.view.game_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view.game_screen.board.GameScreen
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.SoundFX
import com.gomezdevlopment.chessnotationapp.view.home_screen.Navigation
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel


class GameFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            val viewModel: GameViewModel by viewModels()
            setContent {
                //GameScreen(viewModel = viewModel)
                SoundFX(viewModel = viewModel)
            }
        }
    }
}