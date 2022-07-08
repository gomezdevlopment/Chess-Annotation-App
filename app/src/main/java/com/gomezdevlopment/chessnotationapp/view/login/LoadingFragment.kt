package com.gomezdevlopment.chessnotationapp.view.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.gomezdevlopment.chessnotationapp.R
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