package com.example.metinproximityfront.views.Home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ProfileButton (
    drawerState : DrawerState
) {
    val scope = rememberCoroutineScope()
    FloatingActionButton(
        containerColor = Color.White,
        modifier = Modifier.padding(8.dp),
        onClick = {
            scope.launch {
                drawerState.apply {
                    if (isClosed) open() else close()
                }
            }
        }) {
        Text("x")
        //Icon(Icons.Default.Person, contentDescription = "Add")
    }
}