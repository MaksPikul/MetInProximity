package com.example.metinproximityfront.views.Home.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateChatListSheet (
    showBottomSheet : Boolean,
    sheetState : SheetState,
    changeState : () -> Unit
) {
    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = changeState
        ) {
            Text(
                "This will contain people available for private talk, they are removed when they leave radius",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}