package com.gomezdevlopment.chessnotationapp.view.login

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.compose.material.MaterialTheme
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.Navigation
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.databinding.FragmentSignInBinding
import com.gomezdevlopment.chessnotationapp.view_model.SignInViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val signInViewModel: SignInViewModel by viewModels()
        signInViewModel.getUserMutableLiveDate().observe(viewLifecycleOwner, Observer {
            if(it != null){
                Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_homeFragment)
                Navigation.findNavController(view).clearBackStack(R.id.signInFragment)
            }
        })

        binding.submitButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            signInViewModel.signIn(email, password)
        }

        binding.signUpButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        this.requireView().setOnKeyListener { v, keyCode, event ->
            keyCode == KeyEvent.KEYCODE_BACK
        }
    }
}