package com.example.metinproximityfront.views.Home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metinproximityfront.data.entities.users.ChatUser
import com.example.metinproximityfront.data.enums.ScreenState
import com.example.metinproximityfront.app.viewModels.HomeViewModel
import com.example.metinproximityfront.data.entities.account.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateChatListSheet (
    sheetState : SheetState,
    homeVm: HomeViewModel
) {
    // function to make publicly available
    val chatUsers by homeVm.userActionService.chatUsers.collectAsState()
    val visibility by remember { mutableStateOf(User.userData?.openToPrivate) }

    if (homeVm.uiState.value.botSheetVisible) {
        ModalBottomSheet(
            modifier = Modifier.
                fillMaxWidth().
                heightIn(300.dp),
            sheetState = sheetState,
            onDismissRequest = { homeVm.toggleBottomSheet() }
        ) {
            Column (

            ) {
                Row (

                ) {
                    Text(
                        text = "Private Chat with ...",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Button (
                        onClick = {
                            homeVm.userActionService.changeVisibility()
                        }
                    ) {
                        Text(
                            text = "Change visibility Status",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                // TODO : create screen manager, too much nesting, looks ugly
                //when (homeVm.uiState.value.loadingState) {
                    //LoadingState.LOADING -> null

                    //LoadingState.READY ->
                LazyColumn(
                        //state = listState,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        reverseLayout = false
                    ) {
                        items(chatUsers) { chatUser ->
                            ChatUserBubble (
                                chatUser,
                                homeVm
                            )
                        }
                    }
                //}
                // TODO -------------------------------------------------------
            }
        }
    }
}

@Composable
fun ChatUserBubble (
    chatUser: ChatUser,
    homeVm: HomeViewModel,
) {

    Button(
        onClick = {
            homeVm.changeScreen(ScreenState.PRIVATE, chatUser)
        }
    ) {
        Text(
            text = chatUser.UserName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }

}


