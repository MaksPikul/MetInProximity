package com.example.metinproximityfront.views.Chat

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metinproximityfront.data.entities.message.MsgResObject
import com.example.metinproximityfront.data.entities.users.ChatUser
import com.example.metinproximityfront.app.viewModels.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date


@Composable
fun ChatView(
    homeVm: HomeViewModel,
    chatUser: ChatUser?
) {
    val messages by homeVm.msgService.messages.collectAsState()
    var errorState by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        // Scroll to the last item (bottom) in the list
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    var text by remember { mutableStateOf("") }

    val onMsgSend: suspend () -> Unit = {
        val error: String? = homeVm.msgService.sendMessage(text, chatUser)
        text = ""
        error?.let { str ->
            errorState = true
            delay(1000)
        } ?: run {
            errorState = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
    ) {

        ChatHeader(chatUser)

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = false
        ) {
            items(messages) { message ->
                MessageBubble(message, false)
            }
        }

        InputBar(
            onMsgSend,
            text,
            onTextChange = {
                text = it
                errorState = false
            },
            errorState
        )
        CustomErrorToast(errorState)
    }
}

@Composable
fun MessageBubble(
    msgObj: MsgResObject,
    isUser: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                if (isUser) Color(0xFFD1C4E9) else Color(0xFFEFEFEF),
                RoundedCornerShape(12.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "User Icon",
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp)
            )
        }

        Text(
            text = msgObj.body,
            modifier = Modifier.padding(horizontal = 8.dp),
            color = Color.Black
        )

        if (isUser) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Sent",
                modifier = Modifier
                    .size(16.dp)
                    .padding(start = 8.dp),
                tint = Color.Gray
            )
        }
    }
}


@Composable
fun InputBar (
    onMsgSend : suspend () -> Unit,
    text : String,
    onTextChange: (String) -> Unit,
    error: Boolean
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            ,
            //.background(Color.LightGray, RoundedCornerShape(20.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            shape = RoundedCornerShape(8.dp),
            value = text,
            maxLines = 2,
            isError = error,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = if (error) Color.Red else Color.Blue,  // Border color
                unfocusedIndicatorColor = if (error) Color.Red else Color.Gray,
                cursorColor = if (error) Color.Red else Color.Black
            ),
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(1f)
                .height(60.dp)
                .verticalScroll(
                    scrollState
                ), // similar to flex
            placeholder = { Text("message...") },
        )

        if (text != "") {
            Button(
                onClick = {
                    coroutineScope.launch {
                        onMsgSend()
                    }
                },
                modifier = Modifier.height(60.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    modifier = Modifier
                        .size(25.dp)
                        .padding(start = 0.dp),
                    tint = Color.White
                )
            }
        }
        /*
        IconButton(onClick = {
            if (text.isNotBlank()) {
                //messages.add(text)
                text = ""
            }
        }) {
            Icon(Icons.Default.Done, contentDescription = "Send")
        }
         */

    }
}

@Composable
fun ChatHeader (
    chatUser: ChatUser?
){
    Row(
        modifier = Modifier
            .height(72.dp)
            .fillMaxWidth()
            .shadow(8.dp)
            .background(Color.White, RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    )
    {
        var title = "Public Chat!"
        if (chatUser != null){
            title = "Private With " + chatUser.UserName + "!"
        }
        Text(
            text = title,
            color = Color.Black,
            fontSize = 20.sp
        )
    }
}

@Preview
@Composable
fun ChatViewPreview() {
    val testUser = ChatUser(
        "1234",
        "tester"
    )
    InputBar (
        {  },
        "s",
        { } ,
        false
    )

}
@Composable
fun CustomErrorToast(
    showToast: Boolean
) {
    if (showToast) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.BottomCenter)
                .padding(bottom = 200.dp)
        ) {
            Surface(
                modifier = Modifier,
                shape = RoundedCornerShape(8.dp),
                color = Color.Red.copy(alpha = 0.8f),
                contentColor = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp),

                ) {
                    Icon(Icons.Default.Warning, contentDescription = "Send")

                    Text(
                        text = "An Error Has occurred",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ChatHeaderPreview() {
    val testUser = ChatUser(
        "1234",
        "tester"
    )
    ChatHeader (testUser)
    CustomErrorToast( true)
}

@Preview
@Composable
fun MsgBubblePreview() {
    val testUser = ChatUser(
        "1234",
        "tester"
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

    MessageBubble(
        testMsg,
        true
    )
}