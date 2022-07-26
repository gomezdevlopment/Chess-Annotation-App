package com.gomezdevlopment.chessnotationapp.realtime_database

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import com.gomezdevlopment.chessnotationapp.view_model.UserViewModel.Companion.userGames
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class RealtimeDatabaseDAO @Inject constructor(
    private val db: FirebaseDatabase,
    private val app: Application
) {
    fun addUser(user: User) {
        val dbReference = db.getReference(User::class.java.simpleName)
        val userCopy = user.copy(username = createUsername(dbReference))
        dbReference.child(userCopy.username).setValue(userCopy).addOnFailureListener {
            println(it)
        }
    }

    fun addGame(onlineGame: OnlineGame, id: String, waitForMatch: () -> Unit) {
        val dbReference = db.getReference(OnlineGame::class.java.simpleName)
        dbReference.child(id).setValue(onlineGame).addOnSuccessListener {
            waitForMatch()
        }
    }

    fun sendFriendRequest(request: Friends) {
        val sender = request.sender
        val receiver = request.receiver
        val userReference = db.getReference(User::class.java.simpleName)
        val query = userReference.orderByChild("username").equalTo(receiver)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println(snapshot)
                if (snapshot.hasChildren()) {
                    val dbReference = db.getReference(Friends::class.java.simpleName)
                    val friendRequestAlreadySent = dbReference.child("$sender$receiver").key != null
                            || dbReference.child("$receiver$sender").key != null

                    if (!friendRequestAlreadySent) {
                        dbReference.child("$sender$receiver").setValue(request)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    app,
                                    "Friend Request sent to $receiver",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            app,
                            "Already Friends or Friend Request Already Sent",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(app, "$receiver not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    fun checkFriends(
        username: String,
        friendsList: SnapshotStateList<Friends>,
        pending: SnapshotStateList<Friends>,
        requests: SnapshotStateList<Friends>
    ) {
        val dbReference = db.getReference(Friends::class.java.simpleName)
        val query = dbReference.orderByChild("sender").equalTo(username)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                    snapshot.children.forEach {
                        if (snapshot.exists()) {
                            val friend = it.getValue(Friends::class.java)
                            if (friend != null) {
                                when (friend.status) {
                                    "pending" -> {
                                        pending.add(friend)
                                    }
                                    "accepted" -> {
                                        friendsList.add(friend)
                                        pending.remove(friend.copy(status = "pending"))
                                    }
                                    "declined" -> {
                                        friendsList.add(friend)
                                        pending.remove(friend.copy(status = "pending"))
                                    }

                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        val query2 = dbReference.orderByChild("receiver").equalTo(username)
        query2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                    snapshot.children.forEach {
                        if (snapshot.exists()) {
                            val friend = it.getValue(Friends::class.java)
                            if (friend != null) {
                                when (friend.status) {
                                    "pending" -> {
                                        requests.add(friend)
                                    }
                                    "accepted" -> {
                                        friendsList.add(friend)
                                        requests.remove(friend.copy(status = "pending"))
                                    }
                                    "declined" -> {
                                        friendsList.add(friend)
                                        requests.remove(friend.copy(status = "pending"))
                                    }
                                }
                            }
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun acceptFriendRequest(request: Friends) {
        val sender = request.sender
        val receiver = request.receiver
        db.getReference(Friends::class.java.simpleName).child("$sender$receiver").updateChildren(
            mapOf("status" to "accepted")
        )
    }

    fun declineFriendRequest(request: Friends) {
        val sender = request.sender
        val receiver = request.receiver
        db.getReference(Friends::class.java.simpleName).child("$sender$receiver").updateChildren(
            mapOf("status" to "declined")
        )
    }

    fun getUserMap(email: String) {
        var userMap: User? = null
        val query =
            db.getReference(User::class.java.simpleName).orderByChild("email").equalTo(email)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { user ->
                    userMap = user.getValue(User::class.java)
                    MainActivity.user = userMap
                    user.child("games").children.forEach {
                        val key = it.key
                        val value = it.value
                        if (key != null && value != null) {
                            MainActivity.user?.games?.games?.add(value as MutableMap<String, String>)
                        }
                    }
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

    fun deleteUserData() {
        val username = MainActivity.user?.username
        if (username != null) {
            db.getReference(User::class.java.simpleName).child(username).removeValue()
        }

    }
}