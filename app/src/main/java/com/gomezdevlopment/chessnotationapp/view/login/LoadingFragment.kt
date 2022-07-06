package com.gomezdevlopment.chessnotationapp.view.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.databinding.FragmentSignInBinding
import com.gomezdevlopment.chessnotationapp.databinding.LoadingScreenBinding
import com.gomezdevlopment.chessnotationapp.view.AppTheme
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.SoundFX
import com.gomezdevlopment.chessnotationapp.view.home_screen.Navigation
import com.gomezdevlopment.chessnotationapp.view_model.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoadingFragment: Fragment(R.layout.loading_screen) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val signInViewModel: SignInViewModel by viewModels()
        signInViewModel.checkIfUserIsSignedIn()

        signInViewModel.signedOut.observe(viewLifecycleOwner, Observer {
            if(it){
                Navigation.findNavController(view).navigate(R.id.action_loadingFragment_to_signInFragment)
            }
        })

        signInViewModel.getUserMutableLiveDate().observe(viewLifecycleOwner, Observer {
            if(it != null){
                Navigation.findNavController(view).navigate(R.id.action_loadingFragment_to_homeFragment)
            }else{
                Navigation.findNavController(view).navigate(R.id.action_loadingFragment_to_signInFragment)
            }
        })
    }
}