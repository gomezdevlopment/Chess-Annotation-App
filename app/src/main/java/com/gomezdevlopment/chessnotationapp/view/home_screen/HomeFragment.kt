package com.gomezdevlopment.chessnotationapp.view.home_screen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.model.utils.UserSettings
import com.gomezdevlopment.chessnotationapp.view.theming.AppTheme
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.SoundFX
import com.gomezdevlopment.chessnotationapp.view.theming.backgroundDark
import com.gomezdevlopment.chessnotationapp.view.theming.background
import com.gomezdevlopment.chessnotationapp.view_model.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var puzzleViewModel: PuzzleViewModel
    val gameViewModel: GameViewModel by viewModels()
    val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        puzzleViewModel = ViewModelProvider(this).get(PuzzleViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return ComposeView(requireContext()).apply {
            val matchmakingViewModel: MatchmakingViewModel by viewModels()
            val userSettings: UserSettings by viewModels()
            setContent {
                val darkTheme =
                    if (userSettings.theme.value == "System") isSystemInDarkTheme() else userSettings.isDarkThemeSelected.value
                        ?: false
                if (darkTheme) {
                    rememberSystemUiController().setStatusBarColor(backgroundDark, false)
                } else {
                    rememberSystemUiController().setStatusBarColor(
                        com.gomezdevlopment.chessnotationapp.view.theming.background,
                        true
                    )
                }

                AppTheme(darkTheme = darkTheme) {
                    Navigation(
                        gameViewModel,
                        matchmakingViewModel,
                        puzzleViewModel,
                        userViewModel
                    )
                    SoundFX(viewModel = gameViewModel)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.signedOut.observe(viewLifecycleOwner, Observer {
            if (it) {
                println("signed out")
                androidx.navigation.Navigation.findNavController(view)
                    .navigate(R.id.action_homeFragment_to_signInFragment)
            }
        })


        gameViewModel.export.observe(viewLifecycleOwner, Observer {
            if (it) {
                val uri by gameViewModel.uri
                if (uri != null) {
                    val sendIntent = Intent(Intent.ACTION_SEND)
                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri)
                    sendIntent.type = "text/pgn"
                    ContextCompat.startActivity(
                        this.requireContext(),
                        Intent.createChooser(sendIntent, "SHARE"),
                        null
                    )
                }
            }
        })
    }
}