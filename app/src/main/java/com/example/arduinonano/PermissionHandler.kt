package com.example.arduinonano

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts

class PermissionHandler(activity: AppCompatActivity, private val onPermissionsGranted: (Boolean) -> Unit) {

    private val requestPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        onPermissionsGranted(allGranted)
    }

    fun checkPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        requestPermissionLauncher.launch(permissions)
    }
}
