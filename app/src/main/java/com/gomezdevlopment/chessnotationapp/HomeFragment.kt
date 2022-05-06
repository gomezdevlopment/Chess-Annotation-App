package com.gomezdevlopment.chessnotationapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.gomezdevlopment.chessnotationapp.MainActivity.Companion.blackAnnotations
import com.gomezdevlopment.chessnotationapp.MainActivity.Companion.whiteAnnotations
import com.gomezdevlopment.chessnotationapp.databinding.FragmentHomeBinding

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
    }
}