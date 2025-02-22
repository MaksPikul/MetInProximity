package com.example.metinproximityfront.services.permissions

import android.app.Activity
import android.util.Log
import android.widget.Toast

class PermissionListener(
    private val activity : Activity
){

    fun onPermissionsGranted() {
        Log.i("prems", "Permissions granted")
    }

    fun onPermissionsDenied() {
        Log.e("prems", "Permissions denied, put onto a screen with a button?")
        activity.finish()
        Toast.makeText(activity, "Permissions are required to use the app", Toast.LENGTH_LONG).show()
    }
}
