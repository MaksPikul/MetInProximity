package com.example.metinproximityfront

import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.api.RefreshTokenApi
import com.example.metinproximityfront.data.entities.account.AuthResponse
import com.example.metinproximityfront.data.entities.error.AuthException
import com.example.metinproximityfront.data.remote.ApiTokenWrapper
import com.example.metinproximityfront.services.preference.IStoreService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import retrofit2.Response
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before


class ApiTokenWrapperTest {

    private lateinit var mockApi: RefreshTokenApi
    private lateinit var mockStore: IStoreService
    private lateinit var apiTokenWrapper: ApiTokenWrapper

    @Before
    fun setUp() {
        mockApi = mockk()

        val expired = "expired"
        val valid = "valid"

        mockStore = mockk()
        every { mockStore.getFromPref(Constants.ACCESS_TOKEN_KEY) } returns expired
        every { mockStore.getFromPref(Constants.REFRESH_TOKEN_KEY) } returns valid

        every { mockStore.saveIntoPref(Constants.ACCESS_TOKEN_KEY, valid) } returns Unit

        every {mockStore.removeFromPref(Constants.ACCESS_TOKEN_KEY)} returns Unit
        every {mockStore.removeFromPref(Constants.REFRESH_TOKEN_KEY)} returns Unit

        val mockRetrofit: Retrofit = mockk()

        every { mockRetrofit.create(RefreshTokenApi::class.java) } returns mockApi

        apiTokenWrapper = ApiTokenWrapper(mockStore, mockApi)
    }

    @Test
    fun RefreshAccess_ShouldSucceed() = runTest {

        val expired = "expired"
        val valid = "valid"

        coEvery { mockApi.RefreshAccessToken(any()) } returns Response.success(
            AuthResponse(
                accessToken = valid,
                refreshToken = expired
            )
        )

        val result = apiTokenWrapper.callApiWithToken { accessToken ->
            if (accessToken == "Bearer expired") {
                throw HttpException(Response.error<Any>(401, mockk()))
            } else {
                Response.success("Success")
            }
        }

        assertEquals(result, "Success")

        // Confirms that new access token has been saved
        verify { mockStore.saveIntoPref(Constants.ACCESS_TOKEN_KEY, valid) }

        // verifies that inital failed, and needs to call refresh token
        coVerify { mockApi.RefreshAccessToken(any()) }

        // confirms that refresh token succeeded, no need to remove keys
        verify(exactly = 0) { mockStore.removeFromPref(Constants.ACCESS_TOKEN_KEY) }
        verify(exactly = 0) { mockStore.removeFromPref(Constants.REFRESH_TOKEN_KEY) }

    }

    @Test
    fun RefreshAccess_ShouldFail_ThrowAuthError() = runTest {

        coEvery { mockApi.RefreshAccessToken(any()) } returns Response.error(400,
            "Error refreshing token".toResponseBody(null)
        )
        try {

            runBlocking { apiTokenWrapper.callApiWithToken { accessToken ->
                if (accessToken == "Bearer expired") {
                    throw HttpException(Response.error<Any>(401, mockk()))
                } else {
                    Response.success("Success")
                }
            } }

        } catch (e: AuthException) {
            // Catches AuthException, Will cause logout
            assertEquals("Error : Failed to refresh access token", e.message)
        }

        // verifies that tokens have been removed on failed refresh
        verify { mockStore.removeFromPref(Constants.ACCESS_TOKEN_KEY) }
        verify { mockStore.removeFromPref(Constants.REFRESH_TOKEN_KEY) }

    }
}