package com.example.metinproximityfront.views.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun NestedHomeView (
    padding: PaddingValues,
    currentScreen: String
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

            // Todo : Screen Content "Map / Chat"
            ScreenManager(currentScreen)
            ProfileButton()

        }
}

@Composable
fun ScreenManager(currentScreen: String) {
    when (currentScreen) {
        "Map" -> MapScreen()
        "Chat" -> ChatScreen()
    }
}





@Composable
fun ChatScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Screen One", color = Color.White)
    }
}

@Composable
fun MapScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Screen Two", color = Color.White)
    }
}


@Composable
fun ProfileButton () {
    FloatingActionButton(
        containerColor = Color.White,
        modifier = Modifier.padding(8.dp),
        onClick = {/*
                    scope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
               */}) {
        Text("x")
        //Icon(Icons.Default.Person, contentDescription = "Add")
    }
}
