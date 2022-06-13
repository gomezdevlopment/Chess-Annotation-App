package com.gomezdevlopment.chessnotationapp.view.move_input_feature

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.databinding.FragmentAddNotationBinding
import com.gomezdevlopment.chessnotationapp.view_model.HomeViewModel

class AddNotationFragment : Fragment() {

    private lateinit var binding: FragmentAddNotationBinding
    private lateinit var homeViewModel: HomeViewModel

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
        binding = FragmentAddNotationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createButtonHandlers(binding.textView)

        binding.enter.setOnClickListener {
            val annotation = binding.textView.text.toString()
            if(annotation.isNotEmpty()){
//                if(homeViewModel.getWhiteMoves().value?.size == homeViewModel.getBlackMoves().value?.size || homeViewModel.getWhiteMoves().value == null){
//                    homeViewModel.addWhiteMove(annotation)
//                }else{
//                    homeViewModel.addBlackMove(annotation)
//                }
                Navigation.findNavController(view).navigate(R.id.action_addNotationFragment_to_homeFragment)
            }else{
                Toast.makeText(view.context, "Please enter your notation.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cancelButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_addNotationFragment_to_homeFragment)
        }
    }



    private fun createButtonHandlers(textView: TextView) {
        val buttons: ArrayList<Button> = arrayListOf(
            binding.king, binding.queen, binding.rook, binding.bishop, binding.knight,
            binding.a, binding.b, binding.c, binding.d, binding.e, binding.f, binding.g, binding.h,
            binding.one, binding.two, binding.three, binding.four, binding.five, binding.six, binding.seven, binding.eight,
            binding.capture, binding.check, binding.checkmate, binding.kingSideCastle, binding.queenSideCastle
        )

        for(button in buttons){
            button.setOnClickListener {
                textView.append(button.text)
            }
        }
    }
}