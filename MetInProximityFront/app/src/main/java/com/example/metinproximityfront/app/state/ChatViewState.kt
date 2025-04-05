package com.example.metinproximityfront.app.state

import androidx.compose.foundation.lazy.LazyListState
import com.example.metinproximityfront.data.entities.users.ChatUser

data class ChatViewState(
    val error : Boolean = false,
    val inputText : String = "",
    val listState : LazyListState = LazyListState(),
    val currentChatUser: ChatUser? = null,
)
