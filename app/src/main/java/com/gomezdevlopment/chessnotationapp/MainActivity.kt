package com.gomezdevlopment.chessnotationapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.gomezdevlopment.chessnotationapp.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    companion object{
        var notationFragmentOpen: Boolean = false
        val whiteAnnotations: ArrayList<String> = arrayListOf()
        val blackAnnotations: ArrayList<String> = arrayListOf()
        lateinit var floatingActionButton: FloatingActionButton
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(binding.fragmentContainerView.id, HomeFragment()).commit()
        }
        floatingActionButton = binding.floatingActionButton

        binding.floatingActionButton.setOnClickListener {
            if (savedInstanceState == null && !notationFragmentOpen) {
                val addNotationFragment = AddNotationFragment()
                val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(binding.fragmentContainerView.id, addNotationFragment).commit()
                notationFragmentOpen = true
                binding.floatingActionButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_cancel))
            }else{
                val homeFragment = HomeFragment()
                val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(binding.fragmentContainerView.id, homeFragment).commit()
                notationFragmentOpen = false
                binding.floatingActionButton.setImageDrawable(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_edit))
            }
        }
    }
}