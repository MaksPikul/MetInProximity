package com.example.metinproximityfront.data.remote

import com.example.metinproximityfront.config.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object PublicHttpClient {


    val publicRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())  // Used to convert JSON response to Kotlin objects
            .build()
    }

}