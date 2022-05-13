package com.gomezdevlopment.chessnotationapp.view.move_input_feature

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.Navigation
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.blackAnnotations
import com.gomezdevlopment.chessnotationapp.view.MainActivity.Companion.whiteAnnotations
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.databinding.FragmentAddNotationBinding

class AddNotationFragment : Fragment() {

    private lateinit var binding: FragmentAddNotationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                if(whiteAnnotations.size == blackAnnotations.size){
                    whiteAnnotations.add(annotation)
                }else{
                    blackAnnotations.add(annotation)
                }
            }
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