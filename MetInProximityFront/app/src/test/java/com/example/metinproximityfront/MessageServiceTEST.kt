package com.example.metinproximityfront

import com.example.metinproximityfront.services.location.LocationServiceBinder
import com.example.metinproximityfront.data.repositories.MessageRepository
import com.example.metinproximityfront.services.message.MessageService
import com.example.metinproximityfront.services.preference.IStoreService
import org.mockito.Mockito.mock

class MessageServiceTEST {

    // Send Messages public and private messages,
    // Retrieve public and private messages
    // Also check if Store works by itself

    private val mockStore: IStoreService = mock()
    private val mockMsgRepo: MessageRepository = mock()
    private val mockMsgLocBinder: LocationServiceBinder = mock()

    private lateinit var msgService: MessageService


}