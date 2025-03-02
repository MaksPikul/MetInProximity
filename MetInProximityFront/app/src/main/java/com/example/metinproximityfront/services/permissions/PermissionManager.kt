package com.example.metinproximityfront.services.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager {

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.FOREGROUND_SERVICE_LOCATION
    )

    fun checkPermissions(context: Context, listener: PermissionListener) {
        if (arePermissionsGranted(context)) {
            listener.onPermissionsGranted()
        } else {
            requestPermissions(context, listener)
        }
    }

    private fun arePermissionsGranted(context: Context): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions(context: Context, listener: PermissionListener) {
        ActivityCompat.requestPermissions(
            context as Activity, permissions, 1
        )
    }

    fun handlePermissionsResult(
        context: Context,
        requestCode: Int,
        grantResults: IntArray,
        listener: PermissionListener
    ) {
        if (requestCode == 1) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                listener.onPermissionsGranted()
            } else {
                listener.onPermissionsDenied()
            }
        }
        logPermissionsStatus(context)
    }

     fun logPermissionsStatus(context: Context) {
        Log.e("Permissions", "Fine location granted: ${ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED}")
        Log.e("Permissions", "Coarse location granted: ${ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED}")
        Log.e("Permissions", "Background location granted: ${ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED}")
        Log.e("Permissions", "Foreground service location granted: ${ContextCompat.checkSelfPermission(context, Manifest.permission.FOREGROUND_SERVICE_LOCATION) == PackageManager.PERMISSION_GRANTED}")
    }
}
