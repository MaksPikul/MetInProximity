package com.example.metinproximityfront

import android.app.Application
import androidx.navigation.NavHostController
import com.example.metinproximityfront.app.viewModels.MainViewModel
import com.example.metinproximityfront.services.location.LocationServiceBinder
import com.example.metinproximityfront.services.map.MapService
import com.example.metinproximityfront.services.message.SignalRMsgReceiver
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before

class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private val mockApplication: Application = mockk(relaxed = true)
    private val mockNavController: NavHostController = mockk(relaxed = true)
    private val mockLocBinder: LocationServiceBinder = mockk(relaxed = true)
    private val mockMapService: MapService = mockk(relaxed = true)
    private val mockSignalR: SignalRMsgReceiver = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())

        viewModel = MainViewModel(mockApplication).apply {
            mainNavController = mockNavController
            //locBinder = mockLocBinder
            mapService = mockMapService
            //signalRMsgReceiver = mockSignalR
        }
    }


}