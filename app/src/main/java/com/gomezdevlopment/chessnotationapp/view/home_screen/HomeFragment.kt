package com.gomezdevlopment.chessnotationapp.view.home_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.gomezdevlopment.chessnotationapp.view.AppTheme
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.SoundFX
import com.gomezdevlopment.chessnotationapp.view_model.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var puzzleViewModel: PuzzleViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel.init()
        puzzleViewModel = ViewModelProvider(this).get(PuzzleViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return ComposeView(requireContext()).apply {
            val viewModel = GameViewModel(requireActivity().application)
            val matchmakingViewModel = MatchmakingViewModel()
            val signOutViewModel = SignOutViewModel(requireActivity().application)
            val userViewModel = UserViewModel()
            setContent {
                AppTheme(darkTheme = isSystemInDarkTheme()) {
                    Navigation(viewModel, matchmakingViewModel, signOutViewModel, puzzleViewModel, userViewModel)
                    SoundFX(viewModel = viewModel)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val whiteMovesRecycler = binding.whiteMovesRecycler
//        val blackMovesRecycler = binding.blackMovesRecycler
//
//        whiteMovesRecycler.layoutManager = LinearLayoutManager(view.context)
//        whiteMovesAdapter = MoveAdapter(arrayListOf())
//        whiteMovesRecycler.adapter = whiteMovesAdapter
//
//        blackMovesRecycler.layoutManager = LinearLayoutManager(view.context)
//        blackMovesAdapter = MoveAdapter(arrayListOf())
//        blackMovesRecycler.adapter = blackMovesAdapter
//
//        homeViewModel.getBlackMoves().observe(viewLifecycleOwner, Observer {
//            blackMovesAdapter.addData(it)
//        })
//
//        homeViewModel.getWhiteMoves().observe(viewLifecycleOwner, Observer {
//            whiteMovesAdapter.addData(it)
//        })
//
//        binding.newGameButton.setOnClickListener {
//            //homeViewModel.createNewGameDialog(view.context)
//            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_gameFragment)
//        }
//
//        binding.exportButton.setOnClickListener {
//            if (whiteMovesAdapter.itemCount > 0) {
//                exportGameFile(view.context)
//            }
//        }
//
//        binding.floatingActionButton.setOnClickListener {
//            Navigation.findNavController(view)
//                .navigate(R.id.action_homeFragment_to_addNotationFragment)
//        }
//
//        binding.settingsIcon.setOnClickListener {
//            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_settingsFragment)
//        }
//    }
//
//    private fun exportGameFile(context: Context) {
//        if (homeViewModel.getWhiteMoves().value.isNullOrEmpty()) {
//            Toast.makeText(context, "Cannot export game because there are no annotations!", Toast.LENGTH_SHORT).show()
//        } else {
//            if (whiteMovesAdapter.itemCount != blackMovesAdapter.itemCount) {
//                homeViewModel.addBlackMove("")
//            }
//            val uri = createPGNFile(context)
//            launchShareIntent(uri)
//        }
//    }
//
//    private fun createPGNFile(context: Context): Uri {
//        val filename = "chess_game.pgn"
//        val path = activity?.getExternalFilesDir(null)
//        val pgnFile = File(path, filename)
//        pgnFile.delete()
//        pgnFile.createNewFile()
//        pgnFile.appendText(homeViewModel.createPGNString())
//        return FileProvider.getUriForFile(
//            context,
//            activity?.applicationContext?.packageName.toString() + ".provider",
//            pgnFile
//        )
//    }
//
//    private fun launchShareIntent(uri: Uri){
//        val sendIntent = Intent(Intent.ACTION_SEND)
//        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        sendIntent.putExtra(Intent.EXTRA_STREAM, uri)
//        sendIntent.type = "text/pgn"
//        startActivity(Intent.createChooser(sendIntent, "SHARE"))
//    }
    }
}