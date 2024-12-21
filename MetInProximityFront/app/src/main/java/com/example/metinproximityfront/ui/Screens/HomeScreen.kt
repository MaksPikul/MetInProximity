package com.example.metinproximityfront.ui.Screens

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color



@Composable
fun HomeScreen(
    deepLinkState : MutableState<String?>
){
    Button(
        onClick = {deepLinkState.value = "SignIn"}
    ) { Text(
        text = "Sign Out",
        style = MaterialTheme.typography.bodyLarge,
        color = Color.White
    )}


}