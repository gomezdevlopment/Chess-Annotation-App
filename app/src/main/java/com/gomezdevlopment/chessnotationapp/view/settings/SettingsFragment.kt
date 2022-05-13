package com.gomezdevlopment.chessnotationapp.view.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.databinding.FragmentSettingsBinding
import com.gomezdevlopment.chessnotationapp.view_model.SignOutViewModel

class SettingsFragment: Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var signOutViewModel: SignOutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signOutViewModel = ViewModelProvider(this).get(SignOutViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signOutButton.setOnClickListener {
            signOutViewModel.signOut()
            Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_signInFragment)
        }

        binding.backButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_homeFragment)
        }
    }
}