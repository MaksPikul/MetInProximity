package com.example.metinproximityfront.views.Chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.metinproximityfront.app.viewModels.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun InputBar (
    homeVm: HomeViewModel
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            shape = RoundedCornerShape(8.dp),
            value = homeVm.chatState.value.inputText,
            maxLines = 2,
            isError = homeVm.chatState.value.error,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = if (homeVm.chatState.value.error) Color.Red else Color.Blue,  // Border color
                unfocusedIndicatorColor = if (homeVm.chatState.value.error) Color.Red else Color.Gray,
                cursorColor = if (homeVm.chatState.value.error) Color.Red else Color.Black
            ),
            onValueChange = {homeVm.changeTextValue(it)},
            modifier = Modifier
                .weight(1f)
                .height(60.dp)
                .verticalScroll(
                    scrollState
                ), // similar to flex
            placeholder = { Text("message...") },
        )

        if (homeVm.chatState.value.inputText != "") {
            Button(
                onClick = {
                    coroutineScope.launch {
                        homeVm.onMsgSend()
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
    }
}