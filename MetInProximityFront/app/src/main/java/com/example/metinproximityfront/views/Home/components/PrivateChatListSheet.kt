package com.example.metinproximityfront.views.Home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.metinproximityfront.views.Chat.MessageBubble
import com.example.metinproximityfront.views.Home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateChatListSheet (
    showBottomSheet : Boolean,
    sheetState : SheetState,
    changeState : () -> Unit,
    homeVm: HomeViewModel
) {
    // function to make publicly available

    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = changeState
        ) {
            Column (

            ){
                // Some Header With Button
                /*
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    reverseLayout = false
                ) {
                    items(messages) { message ->
                        MessageBubble(message, false)
                    }
                }
                 */
            }
        }
    }
}

@Composable
fun PrivateChatHeader(

){

}