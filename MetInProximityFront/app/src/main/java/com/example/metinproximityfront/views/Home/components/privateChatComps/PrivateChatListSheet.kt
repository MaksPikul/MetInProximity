package com.example.metinproximityfront.views.Home.components.privateChatComps

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.metinproximityfront.data.entities.users.ChatUser
import com.example.metinproximityfront.app.viewModels.HomeViewModel
import com.example.metinproximityfront.data.entities.account.User
import com.example.metinproximityfront.data.enums.UserLoadState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateChatListSheet (
    homeVm: HomeViewModel,
) {

    val chatUsers by homeVm.userActionService!!.chatUsers.collectAsState()
    val loadState by homeVm.userActionService!!.loadState.collectAsState()

    val userListState = rememberLazyListState()

    val user by User.userData.collectAsState()

    if(homeVm.uiState.value.botSheetVisible) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(300.dp),
            onDismissRequest = { homeVm.toggleBottomSheet() },
        ) {

            PrivateChatHeader(homeVm)

            if (user.openToPrivate) {
                when (loadState) {
                    UserLoadState.LOADING -> Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }

                    UserLoadState.READY -> if (chatUsers.isNotEmpty()) {
                        LazyColumn(
                            state = userListState,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            reverseLayout = false
                        ) {
                            items(chatUsers) { chatUser ->
                                PrivateChatUserBubble(
                                    chatUser,
                                    homeVm
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Text(text = "No users nearby")
                        }
                    }
                }
            }
            else {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(text = "Enable private messaging to see other nearby private users")
                }
            }
        }
    }
}

@Preview
@Composable
fun ChatViewPreview() {
    val testUser = ChatUser(
        "1234",
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


