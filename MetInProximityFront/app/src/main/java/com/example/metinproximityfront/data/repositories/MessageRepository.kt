package com.example.metinproximityfront.data.repositories

import android.util.Log
import androidx.navigation.NavController
import com.example.metinproximityfront.data.remote.ApiTokenWrapper
import com.example.metinproximityfront.data.api.MessageApi
import com.example.metinproximityfront.data.entities.error.AuthException
import com.example.metinproximityfront.data.entities.message.MsgReqObject
import com.example.metinproximityfront.data.entities.message.MsgResObject
import com.example.metinproximityfront.data.remote.ApiServiceFactory
import com.example.metinproximityfront.data.remote.PublicHttpClient.publicRetrofit

class MessageRepository(
    private val apiTokenWrapper: ApiTokenWrapper,
    private val navController: NavController
) {

    private val messageApi: MessageApi by lazy {
        ApiServiceFactory(publicRetrofit)
    }

    suspend fun SendPublicMessageRepo(
        msgObj : MsgReqObject
        // onSuccess : ()-> Unit
    ) : MsgResObject? {
        return try {
            apiTokenWrapper.callApiWithToken { token: String ->
                messageApi.SendPublicMessage(msgObj, token) // Extract response body
            }
        } catch (e : AuthException){
            Log.e("Auth Error", e.message.toString())
            navController.navigate("Login")
            null
        }
        catch (e: Exception) {
            Log.e("Api Call Error", e.message.toString())
            throw e
        }
    }

    suspend fun SendPrivateMessageRepo(
        msgObj : MsgReqObject
        // onSuccess : ()-> Unit
    ) : MsgResObject? {
        return try {
            apiTokenWrapper.callApiWithToken { token: String ->
                messageApi.SendPrivateMessage(msgObj, token) // Extract response body
            }
        } catch (e : AuthException){
            Log.e("Auth Error", e.message.toString())
            navController.navigate("Login")
            null
        }
        catch (e: Exception) {
            Log.e("Api Call Error", e.message.toString())
            throw e
        }
    }
}