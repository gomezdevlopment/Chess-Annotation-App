package com.gomezdevlopment.chessnotationapp.view.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.databinding.FragmentSignInBinding
import com.gomezdevlopment.chessnotationapp.databinding.LoadingScreenBinding
import com.gomezdevlopment.chessnotationapp.view_model.SignInViewModel

class LoadingFragment: Fragment(R.layout.loading_screen) {
    private lateinit var signInViewModel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signInViewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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