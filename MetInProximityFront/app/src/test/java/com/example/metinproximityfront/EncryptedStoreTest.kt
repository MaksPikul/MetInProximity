package com.example.metinproximityfront

import android.content.Context
import com.example.metinproximityfront.services.preference.EncryptedStoreService
import org.junit.Test

import org.junit.Assert.*
import org.mockito.Mockito

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class EncryptedStoreTest {





    @Test
    fun addition_isCorrect() {

        val context = Mockito.mock(Context::class.java)
        Mockito.`when`(context.getString(Mockito.anyInt())).thenReturn("Mocked String")

        val store = EncryptedStoreService(context);

    }
}