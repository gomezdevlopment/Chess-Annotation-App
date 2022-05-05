package com.gomezdevlopment.chessnotationapp

import android.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.gomezdevlopment.chessnotationapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(binding.fragmentContainerView.id, HomeFragment()).commit()
        }

        binding.floatingActionButton.setOnClickListener {
            if (savedInstanceState == null) {
                val addNotationFragment = AddNotationFragment()
                val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(binding.fragmentContainerView.id, addNotationFragment).commit()
            }
        }
    }
}