package com.example.metinproximityfront.views.Chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.metinproximityfront.data.entities.message.MsgResObject
import com.example.metinproximityfront.data.entities.users.ChatUser
import com.example.metinproximityfront.app.viewModels.HomeViewModel
import com.example.metinproximityfront.views.Chat.components.ChatHeader
import com.example.metinproximityfront.views.Chat.components.CustomErrorToast
import com.example.metinproximityfront.views.Chat.components.InputBar
import com.example.metinproximityfront.views.Chat.components.MessageBubble
import kotlinx.coroutines.delay
import java.util.Date

@Composable
fun ChatView(
    homeVm: HomeViewModel
) {
    val messages by homeVm.msgService!!.messages.collectAsState()

    val userColorMap = mutableMapOf<String, Color>()
    val colorPalette = listOf(
        Color(0xFFBBDEFB),
        Color(0xFFC8E6C9),
        Color(0xFFFFF9C4),
        Color(0xFFFFCCBC),
        Color(0xFFD1C4E9)
    )

    fun getBoxColor(userId: String): Color {
        return userColorMap.getOrPut(userId) {
            colorPalette[
                userColorMap.size % colorPalette.size
            ]
        }
    }

    LaunchedEffect(messages.size) {
        // Scroll to the last item (bottom) in the list
        if (messages.isNotEmpty()) {
            homeVm.chatState.value.listState
                .animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
    ) {
        ChatHeader(homeVm.chatState.value.currentChatUser)

        LazyColumn(
            state = homeVm.chatState.value.listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = false
        ) {
            itemsIndexed(messages) { index, message ->
                val boxColor = getBoxColor(message.userId)
                MessageBubble(
                    message,
                    boxColor
                )
            }
        }
        InputBar(
            homeVm
        )
        CustomErrorToast(homeVm.chatState.value.error)
    }
}


@Preview
@Composable
fun ChatHeaderPreview() {
    val testUser = ChatUser(
        "1234",
    )
    ChatHeader (testUser)
    CustomErrorToast( true)
}

@Preview
@Composable
fun MsgBubblePreview() {
    val testUser = ChatUser(
        "1234",
    )
    val testMsg = MsgResObject(
        "Some Message for testing" +
                "Some Message for testing" +
                "Some Message for testing" +
                "Some Message for testing",
        "1234",
        false,
        "3333",
        Date()
    )

}