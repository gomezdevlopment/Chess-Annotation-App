package com.gomezdevlopment.chessnotationapp.model.repositories

import androidx.lifecycle.ViewModel
import com.gomezdevlopment.chessnotationapp.model.firestore_interaction.FirestoreInteraction
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserRepository @Inject constructor(val firestore: FirestoreInteraction): ViewModel() {

}