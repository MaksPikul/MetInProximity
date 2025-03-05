package com.example.metinproximityfront

import android.util.Log
import com.example.metinproximityfront.data.entities.account.User
import com.example.metinproximityfront.services.token.TokenService
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test



/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TokenServiceTest {



    @Test
    fun Test_DecodeJwt(){
        var jwt = TestConstants.DOG_TEST_JWT
        var decodedJwt = TokenService.decodeJWT(jwt)

        //get("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress")
        assertEquals("DOG-test@example.com",  decodedJwt.get("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress"))
    }

    @Test
    fun Test_UserCreated(){
        var jwt = TestConstants.DOG_TEST_JWT
        User.create(jwt)

        assertEquals("DOG-test@example.com", User.userData?.email)
        assertEquals("John Doe", User.userData?.username)
    }


}