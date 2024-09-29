package com.example.arduinonano

import android.annotation.SuppressLint
import android.content.Context
import android.bluetooth.BluetoothDevice


object BluetoothHandler {

    @SuppressLint("MissingPermission")
    fun connectToDevice(context: Context, device: BluetoothDevice, showToast: (String) -> Unit) {
        Thread {
            GattHandler(context).connectGatt(device)
            showToast("Connected to device: ${device.name ?: device.address}")
        }.start()
    }
}
