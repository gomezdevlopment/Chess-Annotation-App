package com.gomezdevlopment.chessnotationapp.view.home_screen.nav_components.user_component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gomezdevlopment.chessnotationapp.view_model.UserViewModel

@Composable
fun FriendsList(viewModel: UserViewModel) {
    FriendSearchBar(viewModel = viewModel)
    LazyColumn() {
        val friends = viewModel.friendsList
        itemsIndexed(friends) { index, friend ->
            if (index % 2 == 0) {
                FriendsCard(username = friend, status = friends[index + 1]) {
                    println("This is a Friend")
                }
            }
        }
    }
}

@Composable
fun FriendsCard(username: String, status: String, onClick: () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(20.dp, 5.dp)
            .clickable {
                onClick()
                println(status)
            }) {
        Row() {
            Text(username)
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
        leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")  },
        keyboardActions = KeyboardActions(
            onSearch = {
                viewModel.newSearch(search)
            }
        )
    )
}