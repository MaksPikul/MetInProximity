package com.example.metinproximityfront.views.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.metinproximityfront.app.ui.theme.MetInProximityFrontTheme
import com.example.metinproximityfront.views.Chat.ChatScreen
import com.example.metinproximityfront.views.Home.components.ProfileButton
import kotlinx.coroutines.launch

@Composable
fun NestedHomeView (
    padding: PaddingValues,
    currentScreen: String,
    drawerState: DrawerState,
) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    PaddingValues(
                        start = 16.dp,
                        top = padding.calculateTopPadding() + 16.dp,
                        end = 16.dp,
                        bottom = padding.calculateBottomPadding()
                    )
                )
                .background(color = Color.Red, shape = RoundedCornerShape(16.dp)),
        ) {
            ScreenManager(currentScreen)
            ProfileButton(drawerState)
        }
}

@Composable
fun ScreenManager(
    currentScreen: String,

) {
    val messages = remember { mutableStateListOf("Hello!", "How are you?") }

    when (currentScreen) {
        "Map" -> MapScreen()
        "Chat" -> ChatScreen(messages, true)
    }
}

@Composable
fun MapScreen(){

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview7() {
    MetInProximityFrontTheme {
        //HomeView({})
    }
}
