package com.gomezdevlopment.chessnotationapp.view.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.databinding.SignUpFragmentBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment: Fragment() {

    private lateinit var binding: SignUpFragmentBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SignUpFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.submitButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                if(password == confirmPassword){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if(it.isSuccessful){
                            Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_signInFragment)
                        }else{
                            Toast.makeText(view.context, it.exception.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                }else{
                    Toast.makeText(view.context, "Passwords do not match!", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(view.context, "Fields cannot be left empty!", Toast.LENGTH_LONG).show()
            }
        }

        binding.signInButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_signInFragment)
        }
    }
}