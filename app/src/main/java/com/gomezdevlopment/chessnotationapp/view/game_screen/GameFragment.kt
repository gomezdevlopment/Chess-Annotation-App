package com.gomezdevlopment.chessnotationapp.view.game_screen

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel


class GameFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val width = resources.configuration.screenWidthDp.toFloat()
        return ComposeView(requireContext()).apply {
            val viewModel = GameViewModel(requireActivity().application)
            setContent {
                ChessCanvas(width, viewModel, context)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}