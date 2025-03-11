package com.example.metinproximityfront.views.Home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metinproximityfront.data.entities.users.ChatUser
import com.example.metinproximityfront.data.enums.ScreenState
import com.example.metinproximityfront.app.viewModels.HomeViewModel
import com.example.metinproximityfront.data.entities.account.User
import com.example.metinproximityfront.views.Chat.InputBar
import kotlin.reflect.jvm.internal.impl.descriptors.deserialization.PlatformDependentDeclarationFilter.All

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateChatListSheet (
    sheetState : SheetState,
    homeVm: HomeViewModel
) {
    // function to make publicly available
    val chatUsers by homeVm.userActionService.chatUsers.collectAsState()
    val visibility by remember { mutableStateOf(User.userData?.openToPrivate) }

    val onVisibilityChange = {
        homeVm.userActionService.changeVisibility()
    }


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


                // TODO : create screen manager, too much nesting, looks ugly
                //when (homeVm.uiState.value.loadingState) {
                    //LoadingState.LOADING -> null

                    //LoadingState.READY ->
                LazyColumn(
                        //state = listState,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
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
fun PrivateChatHeader (
    ///homeVm: HomeViewModel,
    visible : Boolean
){
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Users for Private Chat",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Button (
            modifier = Modifier,
            onClick = {

            }
        ) {
            when (visible) {
                true -> Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Toggle Password Visibility"
                )
                false -> Icon(
                    imageVector = Icons.Default.VisibilityOff,
                    contentDescription = "Toggle Password Visibility"
                )
            }
        }
    }
}

@Composable
fun ChatUserBubble (
    chatUser: ChatUser,
    homeVm: HomeViewModel,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ){

        Text(
            text = chatUser.UserName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(12.dp))
        Button(
            onClick = {
                homeVm.changeScreen(ScreenState.PRIVATE, chatUser)
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Favorite")
            }
        }
    }
}

@Preview
@Composable
fun ChatViewPreview() {
    val testUser = ChatUser(
        "1234",
        "tester"
    )

    PrivateChatHeader (
        ///homeVm: HomeViewModel,
        false
    )

}

@Composable
fun CustomSuccessToast(
    showToast: Boolean,
    successMsg : String
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
                color = Color.Green.copy(alpha = 0.8f),
                contentColor = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp),

                    ) {
                    Icon(Icons.Default.Check, contentDescription = "Send")

                    Text(
                        text = successMsg,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}


