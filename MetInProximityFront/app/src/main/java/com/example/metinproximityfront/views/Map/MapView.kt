package com.example.metinproximityfront.views.Map

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
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
    var map = homeVm.mapService.map.collectAsState()

    LaunchedEffect(Unit) {
        homeVm.mapService.GetMapTiles()
    }

    Image(
        //bitmap = map.value.asImageBitmap(),
        contentDescription = "Map Tile",
        modifier = Modifier.fillMaxSize()
    )
}