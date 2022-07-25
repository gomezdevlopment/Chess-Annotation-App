package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component.friends

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gomezdevlopment.chessnotationapp.realtime_database.Friends
import com.gomezdevlopment.chessnotationapp.view.MainActivity
import com.gomezdevlopment.chessnotationapp.view_model.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Collections.list

@Composable
fun FriendsList(viewModel: UserViewModel) {
    viewModel.initializeFriendRequestListener()
    val requests = viewModel.requests
    val pending = viewModel.pending
    val friendsList = viewModel.friendsList
    val friends = mutableListOf<String>()

    friendsList.forEach { friend ->
        if (friend.sender == MainActivity.user?.username) {
            friends.add(friend.receiver)
        } else {
            friends.add(friend.sender)
        }
    }

    FriendSearchBar(viewModel = viewModel)
    LazyColumn() {

        if (requests.isNotEmpty()) {
            item() {
                Text("Friend Requests", modifier = Modifier.padding(20.dp, 5.dp))
            }
            items(requests) { friend ->
                FriendsCard(friend = friend.sender, true, viewModel) {
                    println("This is a Friend Request")
                }
            }
        }

        if (pending.isNotEmpty()) {
            item() {
                Text("Pending", modifier = Modifier.padding(20.dp, 5.dp))
            }
            items(pending) { friend ->
                FriendsCard(friend = friend.receiver, false, viewModel) {
                    println("This is a Pending Friend")
                }
            }
        }

        if (friends.isNotEmpty()) {
            item() {
                Text("Friends", modifier = Modifier.padding(20.dp, 5.dp))
            }
            items(friends) { friend ->
                FriendsCard(friend = friend, false, viewModel) {
                    println("This is a Friend")
                }
            }
        }
    }
}

@Composable
fun FriendsCard(friend: String, pending: Boolean, viewModel: UserViewModel, onClick: () -> Unit) {
    val username = MainActivity.user?.username
    Card(
        Modifier
            .fillMaxWidth()
            .padding(20.dp, 5.dp)
            .wrapContentHeight()
            .clickable {
                onClick()
            }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                friend, modifier = Modifier
                    .padding(10.dp, 10.dp)
                    .weight(1f)
            )
            if (pending) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(5.dp, 5.dp)
                ) {
                    TextButton(onClick = {
                        if (username != null) {
                            viewModel.declineFriendRequest(
                                Friends(
                                    sender = friend,
                                    receiver = username,
                                    status = "declined"
                                )
                            )
                        }

                    }) {
                        Text("Decline")
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(5.dp, 5.dp)
                ) {
                    TextButton(onClick = {
                        if (username != null) {
                            viewModel.acceptFriendRequest(
                                Friends(
                                    sender = friend,
                                    receiver = username,
                                    status = "accepted"
                                )
                            )
                        }
                    }) {
                        Text("Accept")
                    }
                }
            }
        }
    }
}

@Composable
fun FriendSearchBar(viewModel: UserViewModel) {
    val search = viewModel.search.value
    TextField(
        value = search,
        onValueChange = { newValue ->
            viewModel.onSearchChanged(newValue)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        label = { Text("Search Friend") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "Search") },
        keyboardActions = KeyboardActions(
            onSearch = {
                val username = MainActivity.user?.username
                if (username != null) {
                    viewModel.newSearch(username, search)
                }
            }
        )
    )
}