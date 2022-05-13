package com.gomezdevlopment.chessnotationapp.view.home_screen

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.blackAnnotations
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.whiteAnnotations
import com.gomezdevlopment.chessnotationapp.view.move_input_feature.MoveAdapter
import com.gomezdevlopment.chessnotationapp.databinding.FragmentHomeBinding
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import com.gomezdevlopment.chessnotationapp.view.move_input_feature.AddNotationFragment
import java.io.File

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        val whiteMovesAdapter = MoveAdapter(whiteAnnotations)
        whiteMovesRecycler.adapter = whiteMovesAdapter

        blackMovesRecycler.layoutManager = LinearLayoutManager(view.context)
        val blackMovesAdapter = MoveAdapter(blackAnnotations)
        blackMovesRecycler.adapter = blackMovesAdapter

        binding.newGameButton.setOnClickListener {
            createNewGameAlert(view.context)
        }

        binding.exportButton.setOnClickListener {
            if (whiteAnnotations.isNotEmpty()) {
                exportGameFile(view.context)
            }
        }

        val floatingActionButton = binding.floatingActionButton

        binding.floatingActionButton.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainerView, HomeFragment())
            transaction?.disallowAddToBackStack()
            transaction?.commit()
        }
    }

    private fun createNewGameAlert(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Create a new game?")
        builder.setMessage("The current game will be deleted.")

        builder.setPositiveButton("Yes") { dialog, which ->
            binding.blackMovesRecycler.adapter?.notifyItemRangeRemoved(0, blackAnnotations.size)
            binding.whiteMovesRecycler.adapter?.notifyItemRangeRemoved(0, whiteAnnotations.size)
            whiteAnnotations.clear()
            blackAnnotations.clear()
        }

        builder.setNegativeButton("Cancel") { dialog, which ->

        }
        builder.show()
    }

    private fun exportGameFile(context: Context) {
        val filename = "chess_game.pgn"
        val path = activity?.getExternalFilesDir(null)
        val pgnFile = File(path, filename)
        pgnFile.delete()
        pgnFile.createNewFile()

        if (whiteAnnotations.size != blackAnnotations.size) {
            blackAnnotations.add("")
        }

        for (notation in whiteAnnotations) {
            val index = whiteAnnotations.indexOf(notation)
            pgnFile.appendText("${index + 1}. $notation ${blackAnnotations[index]} ")
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