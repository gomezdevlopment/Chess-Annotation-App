package com.gomezdevlopment.chessnotationapp.view.game_screen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.gomezdevlopment.chessnotationapp.databinding.FragmentGameBinding


class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val width = resources.configuration.screenWidthDp
        return ComposeView(requireContext()).apply {
            setContent {
                ChessCanvas(width)
            }
        }
        //binding = FragmentGameBinding.inflate(layoutInflater, container, false)
        //return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun pxFromDp(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    fun dpFromPx(context: Context, px: Float): Float {
        return px / context.resources.displayMetrics.density
    }
}