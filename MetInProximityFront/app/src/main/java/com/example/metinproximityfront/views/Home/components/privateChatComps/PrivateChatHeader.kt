package com.example.metinproximityfront.views.Home.components.privateChatComps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.metinproximityfront.app.viewModels.HomeViewModel
import com.example.metinproximityfront.data.entities.account.User

@Composable
fun PrivateChatHeader (
    homeVm: HomeViewModel
){
    val user = User.userData.collectAsState()

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

        val visButCols = ButtonColors(
            containerColor = if (user.value?.openToPrivate == true) Color.Green else Color.Red,
            contentColor = Color.White,
            disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor,
            disabledContentColor = ButtonDefaults.buttonColors().disabledContentColor
        )

        Button (
            modifier = Modifier,
            colors = visButCols,
            onClick = {
                homeVm.changeVisibility()
            }
        ) {
            when (user.value?.openToPrivate!!) {
                true -> Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Toggle Password Visibility",
                )
                false -> Icon(
                    imageVector = Icons.Default.VisibilityOff,
                    contentDescription = "Toggle Password Visibility",
                )
            }
        }
    }
}