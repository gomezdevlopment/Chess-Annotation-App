package com.gomezdevlopment.chessnotationapp.realtime_database

import com.gomezdevlopment.chessnotationapp.view.MainActivity
import com.google.firebase.database.*
import javax.inject.Inject

class RealtimeDatabaseDAO @Inject constructor(private val db: FirebaseDatabase) {
    fun addUser(user: User) {
        val dbReference = db.getReference(User::class.java.simpleName)
        val userCopy = user.copy(username = createUsername(dbReference))
        dbReference.child(userCopy.username).setValue(userCopy).addOnFailureListener {
            println(it)
        }
    }

    fun addGame(onlineGame: OnlineGame, username: String, waitForMatch: () -> Unit) {
        val dbReference = db.getReference(OnlineGame::class.java.simpleName)
        dbReference.child(username).setValue(onlineGame).addOnSuccessListener {
            waitForMatch()
        }
    }

    fun getUserMap(email: String) {
        var userMap: User? = null
        println(email)
        val query = db.getReference(User::class.java.simpleName).orderByChild("email").equalTo(email)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println("snapshot")
                snapshot.children.forEach { user ->
                    println(user)
                    userMap = user.getValue(User::class.java)
                    MainActivity.user = userMap
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun createUsername(dbReference: DatabaseReference): String {
        var username = "Player${getRandomString()}"

        fun checkIfUsernameIsUnique(name: String) {
            dbReference.equalTo(name).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        username = "Player${getRandomString()}"
                        checkIfUsernameIsUnique(username)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
        checkIfUsernameIsUnique(username)
        return username
    }

    private fun getRandomString(): String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        return List(7) { charset.shuffled().random() }
            .joinToString("")
    }
}