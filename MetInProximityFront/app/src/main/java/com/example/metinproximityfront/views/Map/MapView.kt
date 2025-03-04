package com.example.metinproximityfront.views.Map

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.example.metinproximityfront.app.viewModels.HomeViewModel


@Composable
fun MapView(
    homeVm : HomeViewModel
) {
    val map = homeVm.mapService.map.collectAsState()

    map.value?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Map Tile",
            modifier = Modifier.fillMaxSize()
        )
    } ?: run {
        Text(text = "Map Loading", modifier = Modifier.fillMaxSize())
    }
}