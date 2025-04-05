package com.example.metinproximityfront.views.Chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metinproximityfront.data.entities.account.User
import com.example.metinproximityfront.data.entities.message.MsgResObject

@Composable
fun MessageBubble(
    msgObj: MsgResObject,
    boxColor : Color
) {
    val user = User.userData.collectAsState()
    val isUser = user.value?.userId == msgObj.userId

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                if (isUser) Color(0xFFD1C4E9) else boxColor,
                RoundedCornerShape(12.dp)
            ),
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

        Column{
            Text(
                text = msgObj.userId.take(6),
                fontSize = 10.sp,
                color = Color.DarkGray
            )
            Text(
                text = msgObj.body,
                color = Color.Black
            )
        }

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