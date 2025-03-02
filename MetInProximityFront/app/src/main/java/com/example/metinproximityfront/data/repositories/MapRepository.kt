package com.example.metinproximityfront.data.repositories

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.example.metinproximityfront.config.Constants
import com.example.metinproximityfront.data.api.LocationApi
import com.example.metinproximityfront.data.api.MapApi
import com.example.metinproximityfront.data.remote.ApiServiceFactory
import com.example.metinproximityfront.data.remote.ApiTokenWrapper
import com.example.metinproximityfront.data.remote.PublicHttpClient.publicRetrofit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapRepository(
    private val app: Application
) {

    private val mapApi: MapApi by lazy {
        ApiServiceFactory(publicRetrofit)
    }

    fun getMapTilesRepo(
        lon : Double,
        lat : Double,
        onSucc : (Bitmap) -> Unit,
        onFail : (VolleyError) -> Unit
    ) {
        /*
        CoroutineScope(Dispatchers.IO).launch {
            apiTokenWrapper.callApiWithToken { token: String ->
                mapApi.GetMapTilesApi(long, lat, token)
            }
        }
         */
        val url = Constants.BASE_URL + "map?lon=$lon&lat=$lat"

        val queue: RequestQueue = Volley.newRequestQueue(app.applicationContext)
        val imageRequest = ImageRequest(
            url,
            { response: Bitmap ->
                // On success, pass the bitmap to the callback
                onSucc(response)
            },
                0,
                0,
                null,
                Bitmap.Config.ARGB_8888,
            { error ->
                onFail(error)
            }
        )

        queue.add(imageRequest)
    }

}

/*
        val queue: RequestQueue = Volley.newRequestQueue(app.applicationContext)
        val imageRequest = object : ImageRequest(
            url,
            { response: Bitmap ->
                onSucc(response)
            },
            0, 0, null,
            Bitmap.Config.ARGB_8888,
            { error ->
                onFail(error)
            }
        ) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = mutableMapOf<String, String>()
                headers["Authorization"] = "Bearer $"  // Add Bearer token to headers
                return headers
            }
        }
         */