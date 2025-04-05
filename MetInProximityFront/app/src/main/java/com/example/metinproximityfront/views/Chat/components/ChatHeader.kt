package com.example.metinproximityfront.views.Chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metinproximityfront.data.entities.users.ChatUser

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
            title = "Private With " + chatUser.id.take(6)+ "!"
        }
        Text(
            text = title,
            color = Color.Black,
            fontSize = 20.sp
        )
    }
}