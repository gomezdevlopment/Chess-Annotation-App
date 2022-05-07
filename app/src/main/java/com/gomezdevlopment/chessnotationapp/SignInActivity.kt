package com.gomezdevlopment.chessnotationapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gomezdevlopment.chessnotationapp.databinding.SignInLayoutBinding

class SignInActivity: AppCompatActivity() {

    private lateinit var binding: SignInLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignInLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}