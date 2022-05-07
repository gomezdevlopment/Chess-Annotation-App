package com.gomezdevlopment.chessnotationapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gomezdevlopment.chessnotationapp.databinding.SignUpBinding

class SignUpActivity: AppCompatActivity() {

    private lateinit var binding: SignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}