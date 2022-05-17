package com.gomezdevlopment.chessnotationapp.view.home_screen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.adapter.MoveAdapter
import com.gomezdevlopment.chessnotationapp.databinding.FragmentHomeBinding
import com.gomezdevlopment.chessnotationapp.view_model.HomeViewModel
import java.io.File

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var whiteMovesAdapter: MoveAdapter
    private lateinit var blackMovesAdapter: MoveAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel.init()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val whiteMovesRecycler = binding.whiteMovesRecycler
        val blackMovesRecycler = binding.blackMovesRecycler

        whiteMovesRecycler.layoutManager = LinearLayoutManager(view.context)
        whiteMovesAdapter = MoveAdapter(arrayListOf())
        whiteMovesRecycler.adapter = whiteMovesAdapter

        blackMovesRecycler.layoutManager = LinearLayoutManager(view.context)
        blackMovesAdapter = MoveAdapter(arrayListOf())
        blackMovesRecycler.adapter = blackMovesAdapter

        homeViewModel.getBlackMoves().observe(viewLifecycleOwner, Observer {
            blackMovesAdapter.addData(it)
        })

        homeViewModel.getWhiteMoves().observe(viewLifecycleOwner, Observer {
            whiteMovesAdapter.addData(it)
        })

        binding.newGameButton.setOnClickListener {
            homeViewModel.createNewGameDialog(view.context)
        }

        binding.exportButton.setOnClickListener {
            if (whiteMovesAdapter.itemCount > 0) {
                exportGameFile(view.context)
            }
        }

        binding.floatingActionButton.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_homeFragment_to_addNotationFragment)
        }

        binding.settingsIcon.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_settingsFragment)
        }
    }

    private fun exportGameFile(context: Context) {
        if (homeViewModel.getWhiteMoves().value.isNullOrEmpty()) {
            println("Empty")
            Toast.makeText(context, "Cannot export game because there are no annotations!", Toast.LENGTH_SHORT).show()
        } else {
            if (whiteMovesAdapter.itemCount != blackMovesAdapter.itemCount) {
                homeViewModel.addBlackMove("")
            }
            val filename = "chess_game.pgn"
            val path = activity?.getExternalFilesDir(null)
            val pgnFile = File(path, filename)
            pgnFile.delete()
            pgnFile.createNewFile()

            val whiteMoves = homeViewModel.getWhiteMoves().value!!
            val blackMoves = homeViewModel.getBlackMoves().value!!

            for (notation in whiteMoves) {
                val index = whiteMoves.indexOf(notation)
                pgnFile.appendText("${index + 1}. $notation ${blackMoves[index]} ")
            }
            val uri = FileProvider.getUriForFile(
                context,
                activity?.applicationContext?.packageName.toString() + ".provider",
                pgnFile
            )
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            sendIntent.putExtra(Intent.EXTRA_STREAM, uri)
            sendIntent.type = "text/pgn"
            startActivity(Intent.createChooser(sendIntent, "SHARE"))
        }
    }
}