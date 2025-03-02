package com.example.metinproximityfront

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

import android.app.ActivityManager
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.metinproximityfront.app.MainActivity
import com.example.metinproximityfront.app.viewModels.MainViewModel
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var appContext: Context

    @Before
    fun useAppContext() {
        // Context of the app under test.
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        //assertEquals("com.example.metinproximityfront", appContext.packageName)
    }


    @Test
    fun ViewModels_Initialise(){

        ActivityScenario.launch(MainActivity::class.java).onActivity { activity ->

            val mainVm = ViewModelProvider(activity).get(MainViewModel::class.java)
            assertNotNull("ViewModel should be initialized", mainVm)

        }
    }


}