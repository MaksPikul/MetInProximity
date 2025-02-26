package com.example.metinproximityfront.views.Chat

import android.app.Application
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.metinproximityfront.data.entities.message.MsgResObject
import com.example.metinproximityfront.data.entities.users.ChatUser
import com.example.metinproximityfront.views.Home.HomeViewModel

@Composable
fun ChatView(
    homeVm: HomeViewModel,
    chatUser: ChatUser?
) {
    val messages by homeVm.msgService.messages.collectAsState()

    val listState = rememberLazyListState()
    var text by remember { mutableStateOf("") }

    val onMsgSend = {
        try {
            Log.e("chat", "attempts messsage?")
            homeVm.msgService.sendMessage(text, chatUser)
            text = ""
        }
        catch (ex : Throwable) {
            Log.e("chat error", ex.message.toString())
        }
        Unit
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green, RoundedCornerShape(8.dp))
    ) {
        //ChatHeader(privateUser)

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            reverseLayout = false
        ) {
            items(messages) { message ->
                MessageBubble(message, false)
            }
        }

        InputBar(
            onMsgSend ,
            text,
            onTextChange = { text = it }
        )
    }
}

@Composable
fun MessageBubble(msgObj: MsgResObject, isUser: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                if (isUser) Color(0xFFDCF8C6) else Color(0xFFEFEFEF),
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
            text = msgObj.Body,
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
    onMsgSend : () -> Unit,
    text : String,
    onTextChange: (String) -> Unit,
) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
            //.background(Color.LightGray, RoundedCornerShape(20.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(1f)
                .padding(4.dp), // similar to flex
            placeholder = { Text("Type a message...") },
        )


        Button(
            onClick = onMsgSend,
            modifier = Modifier.height(54.dp),
            shape = RoundedCornerShape(4.dp)
        ){
            Text(
                text = "Send",
                color = Color.White,
            )
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
    isPublic : Boolean
){
    Row(
        modifier = Modifier
            .height(72.dp)
            .fillMaxWidth()
            .background(Color.Cyan, RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    )
    {
        if (isPublic){
            Text(
                text = "Public Chat",
                color = Color.Red,
            )
        }
        else {
            Column {
                Text(
                    text = "Private Chat ",
                    color = Color.Red,
                )
                Text(
                    text = "Random User",
                    color = Color.Red,
                )
            }
        }
    }
}

@Preview
@Composable
fun ChatViewPreview() {
    val app = LocalContext.current.applicationContext as Application
    val context = LocalContext.current
/*
    ChatView(
        homeVm = HomeViewModel(
            app,
            EncryptedStoreService(context)
            NavController()
        ), // Pass application instance
        privateUser = false
    )
 */
}